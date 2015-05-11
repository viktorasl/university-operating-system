package models;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TProcess implements Comparable<TProcess>, Runnable {
	TPState pState;
	int pID;
	TProcess pParent;
	//TODO: pCPUState
	int pPriority;
	PriorityQueue<TProcess> pCProcesses;
	PriorityQueue<TResource> pCResources;
	List<TElement> pORElements;
	
	TKernel kernel;
	static int autoPID = 0;
	protected final Lock lock = new ReentrantLock();
	protected final Condition cond = lock.newCondition();
	
	public TProcess(TKernel kernel, TPState pState, TProcess pParent, int pPriority, List<TElement> pORElements) {
		this.kernel = kernel;
		this.pState = pState;
		this.pParent = pParent;
		this.pPriority = pPriority;
		this.pORElements = pORElements;
		
		this.pID = autoPID++;
		//TODO: saving CPU registers
		this.pCProcesses = new PriorityQueue<TProcess>();
		this.pCResources = new PriorityQueue<TResource>();
	}
	
	public int getpPriority() {
		return pPriority;
	}
	
//	public TKernel getKernel() {
//		return kernel;
//	}
	
	public Lock getLock() {
		return lock;
	}
	
	public Condition getCond() {
		return cond;
	}
	
	public TPState getpState() {
		return pState;
	}
	
	public void setpState(TPState pState) {
		this.pState = pState;
	}
	
	public void addChild(TProcess childProcess) {
		this.pCProcesses.add(childProcess);
	}
	
	@Override
	public int compareTo(TProcess o) {
		if (o.getpPriority() < this.getpPriority()) {
			return -1;
		} else if (o.getpPriority() < this.getpPriority()) {
			return 1;
		}
		return 0;
	}
	
	public void suspendProcess() {
		lock.lock();
		try {
			cond.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public abstract void run();
	
}
