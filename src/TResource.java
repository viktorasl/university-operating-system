import java.util.List;

public class TResource {
	int rID;
	int rAmount;
	TProcess rCreator;
	boolean rReusable;
	List<TElement> rAccElem;
	List<TWaitingProc> rWaitProcList;
}
