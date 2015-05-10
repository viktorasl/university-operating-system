import java.util.List;
import java.util.PriorityQueue;

public class TProcess {
	TPState pState;
	int pID;
	TProcess pParent;
	//TODO: pCPUState
	int pPriority;
	PriorityQueue<TProcess> pCProcesses;
	PriorityQueue<TResource> pResources;
	List<TElement> pORElements;
}
