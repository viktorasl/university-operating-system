package models;
import java.util.List;
import java.util.PriorityQueue;

public abstract class TProcess implements Comparable<TProcess> {
	TPState pState;
	int pID;
	TProcess pParent;
	//TODO: pCPUState
	int pPriority;
	PriorityQueue<TProcess> pCProcesses;
	PriorityQueue<TResource> pCResources;
	List<TElement> pORElements;
	
	protected TKernel kernel;
	static int autoPID = 0;
	
	protected TResource requestedResource;
	
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
	
	/*
	 * Getters/Setters
	 */
	
	public int getpID() {
		return pID;
	}
	
	public int getpPriority() {
		return pPriority;
	}
	
	public TPState getpState() {
		return pState;
	}
	
	public void setpState(TPState pState) {
		this.pState = pState;
	}
	
	public TResource getRequestedResource() {
		return requestedResource;
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
	
	public abstract ProcessInterrupt resume();
	
}
