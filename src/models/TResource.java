package models;
import java.util.List;

public abstract class TResource {
	static int rID;
	static int rAmount;
	static TProcess rCreator;
	static boolean rReusable;
	static List<TElement> rAccElem;
	static List<TWaitingProc> rWaitProcList;
	
	public static void requestsProcess() {
		System.out.println("process" + new Object() { }.getClass().getEnclosingClass().getName());
	}
}
