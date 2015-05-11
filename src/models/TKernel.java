package models;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import processes.StartStop;

public class TKernel {
	PriorityQueue<TProcess> OSProcesses;
	PriorityQueue<TResource> OSResources;
	PriorityQueue<TProcess> OSReadyProc;
	TProcess OSCurrentProc;

	final Lock lock = new ReentrantLock();
	final Condition cond = lock.newCondition();
	
	Runnable runnable;
	
	public TKernel() {
		this.OSProcesses = new PriorityQueue<TProcess>();
	}
	
	public Lock getLock() {
		return lock;
	}
		
	public Condition getCond() {
		return cond;
	}
	
	public void startOS() {
		while (true){
			lock.lock();
			try {
				createProcess(null, TPState.READY, 0, new ArrayList<TElement>());
				cond.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                lock.unlock();
            }
		}
	}
	
	public void onUpdate(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void createProcess(TProcess parent, TPState pState, int pPriority, List<TElement> pORElements) {
		if (this.runnable != null) {
			this.runnable.run();
		}
		StartStop process = new StartStop(this, pState, parent, pPriority, pORElements);
		this.OSProcesses.add(process);
		if (parent != null) {
			parent.addChild(process);
		}
	}
}
