package models;
import interrupts.ProcessInterrupt;
import interrupts.ResourceRequestInterrupt;
import interrupts.ShutDownInterrupt;

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
	
	@Override
	public void run() {
		createProcess(null, TPState.NEW, 0, new ArrayList<TElement>());
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
	
	private void executeDistributor() {
		// TODO: lets say each resource is free and all processes are ready
		this.executePlanner();
	}
	
	static int ee = 0;
	
	private void executePlanner() {
		int ex = ee++;
		System.out.println("start planner " + ex);
		if (this.OSCurrentProc != null && this.OSCurrentProc.getpState() != TPState.WAITING) {
			// TODO: suspend process
		}
		// TODO: Check input procedure
		if (this.OSReadyProc.size() > 0) {
			this.startProcess(this.OSReadyProc.element());
			
			try {
				this.OSCurrentProc.resume();
			} catch (ResourceRequestInterrupt e) {
				executeDistributor();
			} catch (ShutDownInterrupt e) {
				
			} catch (ProcessInterrupt e) {
				e.printStackTrace();
			}
			
		} else {
			// TODO: release Idle
		}
		this.updated(); // TODO: decide where to put this
		System.out.println("end planner " + ex);
	}
	
	/*
	 * Processes primitives
	 */
	
	public void createProcess(TProcess parent, TPState pState, int pPriority, List<TElement> pORElements) {
		StartStop process = new StartStop(this, pState, parent, pPriority, pORElements);
		this.OSProcesses.add(process);
		if (parent != null) {
			parent.addChild(process);
		}
		System.out.println("Created process " + + process.getpID());
		this.executePlanner();
		this.activateProcess(process);
	}
	
	private void activateProcess(TProcess process) {
		System.out.println("Activated process " + + process.getpID());
		process.setpState(TPState.READY);
		this.OSReadyProc.add(process);
		this.executePlanner();
	}
	
	private void startProcess(TProcess process) {
		System.out.println("Started process " + + process.getpID());
		process.setpState(TPState.RUNNING);
		this.OSCurrentProc = process;
	}
	
	private void suspendProcess(TProcess process) {
		System.out.println("Suspend process " + process.getpID());
		process.setpState(TPState.WAITING);
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
	
	public void requestResource(TProcess process, ResourceClass resouceClass, String target) throws ResourceRequestInterrupt {
		TResource requestedResDesc = null;
		for (TResource res : OSResources) {
			if (res.getResourceClass() == resouceClass) {
				requestedResDesc = res;
				break;
			}
		}
		requestedResDesc.getrWaitProcList().add(new TWaitingProc(process, target));
		suspendProcess(process);
		throw new ResourceRequestInterrupt();
	}
	
}
