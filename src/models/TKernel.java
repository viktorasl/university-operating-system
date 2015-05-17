package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import models.TResource.ResourceClass;
import processes.StartStop;

public class TKernel implements Runnable {
	final PriorityQueue<TProcess> OSProcesses;
	final List<TResource> OSResources;
	final PriorityQueue<TProcess> OSReadyProc;
	TProcess OSCurrentProc;

	final Lock lock = new ReentrantLock();
	final Condition cond = lock.newCondition();
	
	Runnable runnable;
	
	public TKernel() {
		OSProcesses = new PriorityQueue<TProcess>();
		OSResources = new LinkedList<TResource>();
		OSReadyProc = new PriorityQueue<TProcess>();
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
	
	@Override
	public void run() {
		createProcess(new StartStop(this, TPState.NEW, null, 2, new ArrayList<TElement>()));
		while (true) {
			try {
				System.out.println("RESUME " + OSCurrentProc.getExternalName());
				this.OSCurrentProc.resume();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void onUpdate(Runnable runnable) {
		this.runnable = runnable;
	}
	
	private void updated() {
		lock.lock();
		try {
			if (this.runnable != null) {
				this.runnable.run();
			}
			cond.await();
		} catch (Exception e) {
            e.printStackTrace();
        }
		lock.unlock();
	}
	
	/*
	 * Operating system core procedures
	 */
	
	private void executeDistributor(TResource resource) {
		updated();
		System.out.println(resource.getResourceClass().toString() +":"+ resource.getrWaitProcList().size() + " waiting proceses and " + resource.getrAccElem().size() + " available elements");
		
		if (resource.getrWaitProcList().size() > 0 && resource.getrAccElem().size() > 0) {
			List<TProcess> servedProcesses = new LinkedList<TProcess>();
			
			List<TElement> usedElements = new LinkedList<TElement>();
			
			for (TElement element : resource.getrAccElem()) {
				for (TWaitingProc waitingProc : resource.getrWaitProcList()) {
					TProcess dedicatedProc = element.getProc();
					if (dedicatedProc == waitingProc.getReceiver() || dedicatedProc == null && !servedProcesses.contains(waitingProc.getReceiver())) {
						TProcess receiver = waitingProc.getReceiver();
						receiver.getpORElements().add(element);
						
						usedElements.add(element);
						resource.getrWaitProcList().remove(waitingProc);
						servedProcesses.add(receiver);
						break;
					}
				}	
			}
			
			if (servedProcesses.size() > 0) {
				resource.getrAccElem().removeAll(usedElements);
				
				for (TProcess process : servedProcesses) {
					activateProcess(process);
				}
			}
		}
		executePlanner();
	}
	
	int i = 1;
	private boolean checkInput() {
		if (i++ % 10 == 0) {
			releaseResource(ResourceClass.LINETOPRINT, new TElement(null, null, "Random string to print"));
//			releaseResource(ResourceClass.INPUTEDLINE, new TElement(null, null, "SHTDW"));
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
	
	/*
	 * Resource primitives
	 */
	
	public void createResource(TProcess process, TResource.ResourceClass resourceClass, boolean reusable, TElement[] availableElements) {
		TResource resourceDesc = new TResource(process, resourceClass, reusable, availableElements);
		process.getpCResources().add(resourceDesc);
		OSResources.add(resourceDesc);
		System.out.println("Created resource descriptor " + resourceDesc.getrID());
	}
	
	public void requestResource(TProcess process, ResourceClass resouceClass, String target) {
		TResource requestedResDesc = null;
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resouceClass) {
				requestedResDesc = res;
				break;
			}
		}
		requestedResDesc.getrWaitProcList().add(new TWaitingProc(process, target));
		System.out.println("Requested resource descriptor " + requestedResDesc.getResourceClass().toString());
		suspendProcess(process);
		executeDistributor(requestedResDesc);
	}
	
	public void releaseResource(ResourceClass resourceClass, TElement element) {
		TResource releaseResDesc = null;
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resourceClass) {
				releaseResDesc = res;
				break;
			}
		}
		element.assignToResource(releaseResDesc);
		releaseResDesc.getrAccElem().add(element);
		System.out.println("Release resource " + releaseResDesc.getResourceClass().toString());
		executeDistributor(releaseResDesc);
	}
	
}
