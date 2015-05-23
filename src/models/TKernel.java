package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

import machine.HardDrive;
import machine.OperativeMemory;
import machine.Processor;
import models.TResource.ResourceClass;
import processes.StartStop;

public class TKernel implements Runnable {
	final PriorityQueue<TProcess> OSProcesses;
	final List<TResource> OSResources;
	final PriorityQueue<TProcess> OSReadyProc;
	TProcess OSCurrentProc;

	final Lock lock = new ReentrantLock();
	final Condition cond = lock.newCondition();
	boolean stepRun;
	JTextArea printer;
	String inputedLine;
	
	final int pageSize = 10;
	final String[] generalMemory = new String[12 * pageSize];
	final OperativeMemory ram = new OperativeMemory(88, pageSize);
	final Processor processor = new Processor(ram);
	final HardDrive hdd = new HardDrive(100, pageSize);
	
	Runnable runnable;
	
	public TKernel(boolean stepRun) {
		OSProcesses = new PriorityQueue<TProcess>();
		OSResources = new LinkedList<TResource>();
		OSReadyProc = new PriorityQueue<TProcess>();
		
		Arrays.fill(generalMemory, "");
		
		this.stepRun = stepRun;
	}
	
	/*
	 * Getters/Setters
	 */
	public Lock getLock() {
		return lock;
	}
		
	public Condition getCond() {
		return cond;
	}
	
	public TProcess[] getOSProcesses() {
		return OSProcesses.toArray(new TProcess[OSProcesses.size()]);
	}
	
	public void print (String text) {
		if (this.printer != null) {
			this.printer.setText(text + "\n" + this.printer.getText());
		}
	}
	
	public void setPrinter(JTextArea printer) {
		this.printer = printer;
	}
	
	public Processor getProcessor() {
		return processor;
	}
	
	public OperativeMemory getRam() {
		return ram;
	}
	
	public HardDrive getHdd() {
		return hdd;
	}
	
	public String[] getGeneralMemory() {
		return generalMemory;
	}
	
	public void setStepRun(boolean stepRun) {
		this.stepRun = stepRun;
	}
	
	public void setInputedLine(String inputedLine) {
		this.inputedLine = inputedLine;
	}
	
	@Override
	public void run() {
		createProcess(new StartStop(this, TPState.NEW, null, 2, new ArrayList<TElement>()));
		while (true) {
			try {
				System.out.println("RESUME " + OSCurrentProc.getExternalName());
				this.OSCurrentProc.resume();
			} catch (Exception e) {
				print("Good bye!");
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void onUpdate(Runnable runnable) {
		this.runnable = runnable;
	}
	
	private void updated() {
		boolean step = stepRun;
		if (step) {
			lock.lock();
		}
		try {
			if (this.runnable != null) {
				this.runnable.run();
			}
			if (step) {
				cond.await();
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
		if (step) {
			lock.unlock();
		}
	}
	
	/*
	 * Operating system core procedures
	 */
	
	private void executeDistributor(TResource resource) {
		updated();
		System.out.println(resource.getResourceClass().toString() +":"+ resource.getrWaitProcList().size() + " waiting proceses and " + resource.getrAccElem().size() + " available elements");
		
		if (resource.getrWaitProcList().size() > 0 && resource.getrAccElem().size() > 0) {
			List<TWaitingProc> servedProcesses = new LinkedList<TWaitingProc>();
			
			for (TWaitingProc waitingProc : resource.getrWaitProcList()) {
				int neededAmount = waitingProc.getAmount();
				TProcess receiver = waitingProc.getReceiver();
				
				// Checking if there are enough available elements for particular waiting process
				if (neededAmount > 0 && neededAmount <= availableElements(resource, receiver)) {
					List<TElement> usedElements = new LinkedList<TElement>();
					
					// Assigning resource elements to process either if the element is dedicated for it or is for general usage
					for (TElement el : resource.getrAccElem()) {
						if (el.getProc() == null || el.getProc() == receiver) {
							el.setTarget(waitingProc.getTarget());
							receiver.getpORElements().add(el);
							usedElements.add(el);
							neededAmount--;
						}
						if (neededAmount <= 0) {
							break;
						}
					}
					
					// When process gets enough elements it is flagged as served and will be removed from waiting list
					servedProcesses.add(waitingProc);
					// Removing used elements from resource available elements list
					resource.getrAccElem().removeAll(usedElements);
				}
			}
			
			for (TWaitingProc process : servedProcesses) {
				activateProcess(process.getReceiver());
			}
			
			resource.getrWaitProcList().removeAll(servedProcesses);
		}
		executePlanner();
	}
	
	private TProcess OSProcessWithId(int id) {
		for (TProcess proc : OSProcesses) {
			if (proc.getpID() == id) {
				return proc;
			}
		}
		return null;
	}
	
	private int availableElements(TResource res, TProcess p) {
		int available = 0;
		for (TElement el : res.getrAccElem()) {
			if (el.getProc() == null || el.getProc() == p) {
				available++;
			}
		}
		return available;
	}
	
	public int availableResourceElementsFor(TProcess process, ResourceClass resourceClass) throws Exception {
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resourceClass) {
				return availableElements(res, process);
			}
		}
		throw new Exception("Resource class " + resourceClass + " does not exist in operating system");
	}
	
	int i = 1;
	private boolean checkInput() {
		if (inputedLine != null) {
			String line = inputedLine;
			inputedLine = null;
			
			int spaceIdx = line.indexOf(" ");
			if (spaceIdx > 0) {
				String procIdString = line.substring(0, spaceIdx);
				try {
					int procId = Integer.valueOf(procIdString);
					String msg = line.substring(spaceIdx + 1, line.length());
					TProcess proc = OSProcessWithId(procId);
					if (proc != null) {
						print(msg);
						releaseResource(ResourceClass.INPUTEDLINE, new TElement(proc, null, msg));
					} else {
						releaseResource(ResourceClass.LINETOPRINT, new TElement(null, null, "Process with id=" + procId + " does not exist"));
					}
				} catch (NumberFormatException e) {
					releaseResource(ResourceClass.LINETOPRINT, new TElement(null, null, "Process id should be numeric"));
				}
			} else {
				releaseResource(ResourceClass.LINETOPRINT, new TElement(null, null, "Invalid line format"));
			}
			
			return true;
		}
		return false;
	}
	
	private void executePlanner() {
		updated();
		if (this.OSCurrentProc != null && this.OSCurrentProc.getpState() != TPState.READY) {
			this.OSCurrentProc.setpState(TPState.READY);
		}
		if (!checkInput()) {
			if (this.OSReadyProc.size() > 0) {
				this.startProcess(this.OSReadyProc.element());
				TProcess headProcess = this.OSReadyProc.poll();
				headProcess.toggleLastUsing();
				OSReadyProc.add(headProcess);
				this.startProcess(headProcess);
			} else {
				releaseResource(ResourceClass.IDLE, new TElement(null, null, null));
			}
		}
	}
	
	/*
	 * Processes primitives
	 */
	
	public void createProcess(TProcess process) {
		this.OSProcesses.add(process);
		if (process.getpParent() != null) {
			process.getpParent().addChild(process);
		}
		System.out.println("Created process " + process.getExternalName());
		this.activateProcess(process);
		this.executePlanner();
	}
	
	private void activateProcess(TProcess process) {
		System.out.println("Activated process " + process.getExternalName());
		process.setpState(TPState.READY);
		this.OSReadyProc.add(process);
	}
	
	private void startProcess(TProcess process) {
		System.out.println("Started process " + process.getExternalName());
		process.setpState(TPState.RUNNING);
		this.OSCurrentProc = process;
		
		updated();
	}
	
	private void suspendProcess(TProcess process) {
		System.out.println("Suspend process " + process.getExternalName());
		process.setpState(TPState.WAITING);
		this.OSReadyProc.remove(process);
		OSCurrentProc = null;
	}
	
	public void destroyProcess(TProcess process) {
		System.out.println("Destroy process " + process.getExternalName());
		process.setpState(TPState.FINISHED);
		this.OSResources.removeAll(process.getpCResources());
		this.OSProcesses.removeAll(process.getpCProcesses());
		if (process.getpParent() != null) {
			process.getpParent().getpCProcesses().remove(process);
		}
		this.OSProcesses.remove(process);
		this.OSReadyProc.remove(process);
	}
	
	/*
	 * Resource primitives
	 */
	
	public void createResource(TProcess process, TResource.ResourceClass resourceClass, boolean reusable, TElement[] availableElements) {
		TResource resourceDesc = new TResource(process, resourceClass, reusable, availableElements);
		process.getpCResources().add(resourceDesc);
		OSResources.add(resourceDesc);
		System.out.println("Created resource descriptor " + resourceDesc.getrID());
	}
	
	public void requestResource(TProcess process, ResourceClass resouceClass, int target) {
		requestResource(process, resouceClass, target, 1);
	}
	
	public void requestResource(TProcess process, ResourceClass resouceClass, int target, int amount) {
		TResource requestedResDesc = null;
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resouceClass) {
				requestedResDesc = res;
				break;
			}
		}
		requestedResDesc.getrWaitProcList().add(new TWaitingProc(process, amount, target));
		System.out.println("Requested resource descriptor " + requestedResDesc.getResourceClass().toString());
		suspendProcess(process);
		executeDistributor(requestedResDesc);
	}
	
	public void releaseResource(ResourceClass resourceClass, TElement[] elements) {
		TResource releaseResDesc = null;
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resourceClass) {
				releaseResDesc = res;
				break;
			}
		}
		for (TElement el : elements) {
			el.assignToResource(releaseResDesc);
			releaseResDesc.getrAccElem().add(el);
		}
		System.out.println("Release resource " + releaseResDesc.getResourceClass().toString());
		executeDistributor(releaseResDesc);
	}
	
	public void releaseResource(ResourceClass resourceClass, TElement element) {
		releaseResource(resourceClass, new TElement[]{ element });
	}
	
}
