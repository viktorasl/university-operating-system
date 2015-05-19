package models;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import models.TResource.ResourceClass;

public abstract class TProcess implements Comparable<TProcess> {
	TPState pState;
	int pID;
	TProcess pParent;
	//TODO: pCPUState
	int pPriority;
	protected PriorityQueue<TProcess> pCProcesses;
	List<TResource> pCResources;
	List<TElement> pORElements;
	
	protected int phase = 1;
	
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
	
	public String getExternalName() {
		return getClass().getSimpleName();
	}
	
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
	
	public List<TElement> getpORElements() {
		return pORElements;
	}
	
	public PriorityQueue<TProcess> getpCProcesses() {
		return pCProcesses;
	}
	
	@Override
	public int compareTo(TProcess o) {
		if (this.getpPriority() > o.getpPriority()) {
			return +1;
		} else if (this.getpPriority() < o.getpPriority()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	protected TElement getElement(ResourceClass resClass) throws Exception {
		for (TElement element : pORElements) {
			if (element.getResource().getResourceClass() == resClass) {
				pORElements.remove(element);
				return element;
			}
		}
		throw new Exception();
	}
	
	public void resume() throws Exception {
		String methodName = "phase" + phase;
		System.out.println(getExternalName() + ":" + methodName);
		Method method = this.getClass().getDeclaredMethod( methodName );
		method.invoke(this);
	}
	
}
