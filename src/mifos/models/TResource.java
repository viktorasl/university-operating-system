package mifos.models;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TResource {
	
	public enum ResourceClass {
		IDLE,
		SHUTDOWN,
		INPUTEDLINE,
		LOADPROGRAM,
		PROGRAMLOADED,
		GENERALMEMORY,
		CHANNELDEVICE,
		PROGRAMVALID,
		LINETOPRINT,
		INTERRUPTINFO,
		INTERRUPT,
		PAGES
	}
	
	final ResourceClass resourceClass;
	final int rID;
	final TProcess rCreator;
	final boolean rReusable;
	final List<TElement> rAccElem;
	final List<TWaitingProc> rWaitProcList;
	
	static int autoId = 0;
	
	public TResource(TProcess process, ResourceClass resourceClass, boolean reusable, TElement[] availableElements) {
		rID = autoId++;
		this.resourceClass = resourceClass;
		rCreator = process;
		rReusable = reusable;
		rAccElem = availableElements != null ? new ArrayList<TElement>(Arrays.asList(availableElements)): new ArrayList<TElement>();
		for (TElement element : rAccElem) {
			element.assignToResource(this);
		}
		rWaitProcList = new ArrayList<TWaitingProc>();
	}
	
	public int getrID() {
		return rID;
	}
	
	public ResourceClass getResourceClass() {
		return resourceClass;
	}
	
	public TProcess getrCreator() {
		return rCreator;
	}
	
	public ArrayList<TElement> getrAccElem() {
		return (ArrayList<TElement>) rAccElem;
	}
	
	public int getrAmount() {
		return rAccElem.size();
	}
	
	public List<TWaitingProc> getrWaitProcList() {
		return rWaitProcList;
	}
	
	public boolean isrReusable() {
		return rReusable;
	}
	
}
