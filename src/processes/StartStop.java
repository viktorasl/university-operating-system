package processes;

import java.util.List;

import resources.Idle;
import models.ProcessInterrupt;
import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;

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
		System.out.println("Create all system resources");
		System.out.println("Create all system processes");
		//TODO: request Idle resource
		phase = Phase.PHASE2;
		ProcessInterrupt interrupt = ProcessInterrupt.REQUEST_RESOURCE;
		interrupt.requestClass = Idle.class;
		return ProcessInterrupt.REQUEST_RESOURCE;
	}
	
	private ProcessInterrupt phase2() {
		System.out.println("Destroy all system processes");
		System.out.println("Destroy all system resources");
		return ProcessInterrupt.SHUT_DOWN;
	}
	
}
