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
		createProcess(new StartStop(this, TPState.NEW, null, 1, new ArrayList<TElement>()));
		while (true) {
			updated();
			try {
				this.OSCurrentProc.resume();
			} catch (ResourceRequestInterrupt e) {
				executeDistributor();
			} catch (ShutDownInterrupt e) {
				break;
			} catch (ProcessInterrupt e) {
				e.printStackTrace();
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
	
	private void executeDistributor() {
		// TODO: lets say each resource is free and all processes are ready
		this.executePlanner();
	}
	
	private void executePlanner() {
		if (this.OSCurrentProc != null && this.OSCurrentProc.getpState() != TPState.WAITING) {
			suspendProcess(this.OSCurrentProc);
		}
		// TODO: Check input procedure
		if (this.OSReadyProc.size() > 0) {
			this.startProcess(this.OSReadyProc.element());
		} else {
			System.out.println("Release Idle");
			// TODO: release Idle
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
	}
	
	private void activateProcess(TProcess process) {
		System.out.println("Activated process " + process.getExternalName());
		process.setpState(TPState.READY);
		this.OSReadyProc.add(process);
		this.executePlanner();
	}
	
	private void startProcess(TProcess process) {
		System.out.println("Started process " + process.getExternalName());
		process.setpState(TPState.RUNNING);
		this.OSCurrentProc = process;
	}
	
	private void suspendProcess(TProcess process) {
		System.out.println("Suspend process " + process.getExternalName());
		process.setpState(TPState.WAITING);
		System.out.println("Ready processes " + OSReadyProc.size());
		this.OSReadyProc.remove(process);
		System.out.println("Ready processes " + OSReadyProc.size());
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
