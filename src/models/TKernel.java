package models;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import processes.StartStop;

public class TKernel implements Runnable {
	final PriorityQueue<TProcess> OSProcesses;
	final PriorityQueue<TResource> OSResources;
	final PriorityQueue<TProcess> OSReadyProc;
	TProcess OSCurrentProc;

	final Lock lock = new ReentrantLock();
	final Condition cond = lock.newCondition();
	
	Runnable runnable;
	
	public TKernel() {
		OSProcesses = new PriorityQueue<TProcess>();
		OSResources = new PriorityQueue<TResource>();
		OSReadyProc = new PriorityQueue<TProcess>();
	}
	
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
	
	private void executeDistributor() {
		// TODO: lets say each resource is free and all processes are ready
		this.executePlanner();
	}
	
	private void executePlanner() {
		if (this.OSCurrentProc != null && this.OSCurrentProc.getpState() != TPState.WAITING) {
			// TODO: suspend process
		}
		// TODO: Check input procedure
		if (this.OSReadyProc.size() > 0) {
			this.startProcess(this.OSReadyProc.element());
			this.handleProcessInterrupt(this.OSCurrentProc.resume());
		} else {
			// TODO: release Idle
		}
		this.updated(); // TODO: decide where to put this
	}
	
	private void handleProcessInterrupt(ProcessInterrupt processInterrupt) {
		
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
		for (TElement element : availableElements) {
			element.assignToResource(resourceDesc);
		}
		process.getpCResources().add(resourceDesc);
		OSResources.add(resourceDesc);
		System.out.println("Created resource " + resourceDesc.getrID());
	}
	
	public ProcessInterrupt requestResource(TProcess process) {
		suspendProcess(process);
		return ProcessInterrupt.REQUEST_RESOURCE;
	}
	
}
