package models;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import processes.StartStop;

public class TKernel implements Runnable {
	PriorityQueue<TProcess> OSProcesses;
	PriorityQueue<TResource> OSResources;
	PriorityQueue<TProcess> OSReadyProc;
	TProcess OSCurrentProc;

	final Lock lock = new ReentrantLock();
	final Condition cond = lock.newCondition();
	final Condition resouceCond = lock.newCondition();
	
	Runnable runnable;
	
	public TKernel() {
		this.OSProcesses = new PriorityQueue<TProcess>();
		this.OSReadyProc = new PriorityQueue<TProcess>();
	}
	
	public Lock getLock() {
		return lock;
	}
		
	public Condition getCond() {
		return cond;
	}
	
	public Condition getResouceCond() {
		return resouceCond;
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
		for (TProcess p : this.OSProcesses) {
			p.getLock().lock();
			p.getCond().signalAll();
			p.getLock().unlock();
		}
		this.executePlanner();
	}
	
	private void executePlanner() {
		if (this.OSCurrentProc != null && this.OSCurrentProc.getpState() != TPState.WAITING) {
			// TODO: suspend process
		}
		// TODO: Check input procedure
		if (this.OSReadyProc.size() > 0) {
			this.startProcess(this.OSReadyProc.element());
			this.OSCurrentProc.getLock().lock();
			this.OSCurrentProc.getCond().signalAll();
			this.OSCurrentProc.getLock().unlock();
			
			lock.lock();
			try {
				resouceCond.await();
				if (OSCurrentProc.requestedResource != null) {
					this.requestResource(OSCurrentProc);
				}
				this.executeDistributor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		} else {
			// TODO: release Idle
		}
		this.updated();
	}
	
	public void createProcess(TProcess parent, TPState pState, int pPriority, List<TElement> pORElements) {
		StartStop process = new StartStop(this, pState, parent, pPriority, pORElements);
		this.OSProcesses.add(process);
		if (parent != null) {
			parent.addChild(process);
		}
		new Thread(process).start();
		System.out.println("Created process");
		this.executePlanner();
		this.activateProcess(process);
	}
	
	public void activateProcess(TProcess process) {
		System.out.println("Activated process");
		process.setpState(TPState.READY);
		this.OSReadyProc.add(process);
		this.executePlanner();
	}
	
	public void startProcess(TProcess process) {
		System.out.println("Started process");
		process.setpState(TPState.RUNNING);
		this.OSCurrentProc = process;
	}
	
	public void suspendProcess(TProcess process) {
		System.out.println("Suspend process");
		process.setpState(TPState.WAITING);
		this.OSReadyProc.remove(process);
	}
	
	public void requestResource(TProcess process) {
		suspendProcess(process);
		this.executeDistributor();
	}
	
}
