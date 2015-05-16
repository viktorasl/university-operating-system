package models;
import interrupts.ProcessInterrupt;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public abstract class TProcess implements Comparable<TProcess> {
	TPState pState;
	int pID;
	TProcess pParent;
	//TODO: pCPUState
	int pPriority;
	PriorityQueue<TProcess> pCProcesses;
	List<TResource> pCResources;
	List<TElement> pORElements;
	
	protected TKernel kernel;
	static int autoPID = 0;
	
	public TProcess(TKernel kernel, TPState pState, TProcess pParent, int pPriority, List<TElement> pORElements) {
		this.kernel = kernel;
		this.pState = pState;
		this.pParent = pParent;
		this.pPriority = pPriority;
		this.pORElements = pORElements;
		
		this.pID = autoPID++;
		//TODO: saving CPU registers
		this.pCProcesses = new PriorityQueue<TProcess>();
		this.pCResources = new LinkedList<TResource>();
	}
	
	/*
	 * Getters/Setters
	 */
	
	public abstract String getExternalName();
	
	public int getpID() {
		return pID;
	}
	
	public int getpPriority() {
		return pPriority;
	}
	
	public TPState getpState() {
		return pState;
	}
	
	public TProcess getpParent() {
		return pParent;
	}
	
	public void setpState(TPState pState) {
		this.pState = pState;
	}
	
	public void addChild(TProcess childProcess) {
		this.pCProcesses.add(childProcess);
	}
	
	public List<TResource> getpCResources() {
		return pCResources;
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
	
	public abstract void resume() throws ProcessInterrupt;
	
}
