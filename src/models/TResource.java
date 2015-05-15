package models;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TResource {
	
	public enum ResourceClass {
		IDLE,
		SHUTDOWN
	}
	
	final int rID;
	final TProcess rCreator;
	final boolean rReusable;
	final List<TElement> rAccElem;
	final List<TWaitingProc> rWaitProcList;
	int rAmount;
	
	static int autoId = 0;
	
	public TResource(TProcess process, TResource.ResourceClass resourceClass, boolean reusable, TElement[] availableElements) {
		rID = autoId++;
		rCreator = process;
		rReusable = reusable;
		rAccElem = Arrays.asList(availableElements);
		rWaitProcList = new ArrayList<TWaitingProc>();
		rAmount = availableElements.length;
	}
	
	public int getrID() {
		return rID;
	}
	
	public TProcess getrCreator() {
		return rCreator;
	}
	
	public List<TElement> getrAccElem() {
		return rAccElem;
	}
	
}
