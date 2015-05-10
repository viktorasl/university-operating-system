import java.util.List;
import java.util.PriorityQueue;

public class TProcess {
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
	
	public void addChild(TProcess childProcess) {
		this.pCProcesses.add(childProcess);
	}
}
