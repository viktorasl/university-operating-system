package models;

import java.lang.reflect.Method;
import java.util.Date;
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
	long lastUsing;
	
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
	
	public void toggleLastUsing() {
		lastUsing = new Date().getTime();
	}
	
	public long getLastUsing() {
		return lastUsing;
	}
	
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
			return -1;
		} else if (this.getpPriority() < o.getpPriority()) {
			return +1;
		} else if (this.getLastUsing() > o.getLastUsing()) {
			return +1;
		} else if (this.getLastUsing() < o.getLastUsing()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	protected TElement[] getElements(ResourceClass resClass, int amount) throws Exception {
		List<TElement> elements = new LinkedList<TElement>();
		for (TElement element : pORElements) {
			if (element.getResource().getResourceClass() == resClass) {
				elements.add(element);
				if (elements.size() == amount) {
					break;
				}
			}
		}
		pORElements.removeAll(elements);
		if (elements.size() == amount) {
			return elements.toArray(new TElement[elements.size()]);
		}
		throw new Exception("Cannot get " + amount + " " + resClass + " resource elements");
	}
	
	protected TElement getElement(ResourceClass resClass) throws Exception {
		return getElements(resClass, 1)[0];
	}
	
	public void resume() throws Exception {
		String methodName = "phase" + phase;
		System.out.println(getExternalName() + ":" + methodName);
		Method method = this.getClass().getDeclaredMethod( methodName );
		method.invoke(this);
	}
	
}
