package processes;

import java.util.List;

import models.ProcessInterrupt;
import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class StartStop extends TProcess {

	private enum Phase { PHASE1, PHASE2 }	
	
	private Phase phase = Phase.PHASE1;
	
	public StartStop(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	@Override
	public ProcessInterrupt resume() {
		switch (phase) {
			case PHASE1: return phase1();
			case PHASE2: return phase2();
		}
		return null;
	}
	
	private ProcessInterrupt phase1() {
		TElement idleElement = new TElement(null, this, null);
		kernel.createResource(this, ResourceClass.IDLE, true, new TElement[]{ idleElement });
		
		System.out.println("Create all system resources");
		System.out.println("Create all system processes");
		phase = Phase.PHASE2;
		return this.kernel.requestResource(this); //TODO: request Idle resource
	}
	
	private ProcessInterrupt phase2() {
		System.out.println("Destroy all system processes");
		System.out.println("Destroy all system resources");
		return ProcessInterrupt.SHUT_DOWN;
	}
	
}
