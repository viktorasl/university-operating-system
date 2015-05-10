import java.util.List;
import java.util.PriorityQueue;

public class TKernel {
	PriorityQueue<TProcess> OSProcesses;
	PriorityQueue<TResource> OSResources;
	PriorityQueue<TProcess> OSReadyProc;
	TProcess OSCurrentProc;
	
	Runnable runnable;
	
	public TKernel() {
		this.OSProcesses = new PriorityQueue<TProcess>();
	}
	
	public void onUpdate(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void createProcess(TProcess parent, TPState pState, int pPriority, List<TElement> pORElements) {
		if (this.runnable != null) {
			this.runnable.run();
		}
		TProcess process = new TProcess(this, pState, parent, pPriority, pORElements);
		this.OSProcesses.add(process);
		if (parent != null) {
			parent.addChild(process);
		}
	}
}
