package processes;

import interrupts.ProcessInterrupt;
import interrupts.ResourceRequestInterrupt;
import interrupts.ShutDownInterrupt;

import java.util.ArrayList;
import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class StartStop extends TProcess {

	private enum Phase { PHASE1, PHASE2, PHASE10 }	
	
	private Phase phase = Phase.PHASE1;
	
	public StartStop(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	@Override
	public void resume() throws ProcessInterrupt {
		System.out.println(getExternalName() + ":" + phase.toString());
		switch (phase) {
			case PHASE1: phase1(); break;
			case PHASE2: phase2(); break;
			
			case PHASE10: phase10(); break;
		}
	}
	
	@Override
	public String getExternalName() {
		return "StartStop";
	}
	
	private void phase1() throws ResourceRequestInterrupt {
		TElement idleElement = new TElement(null, this, null);
		kernel.createResource(this, ResourceClass.IDLE, true, new TElement[]{ idleElement });
		kernel.createResource(this, ResourceClass.SHUTDOWN, false, null);
		
		phase = Phase.PHASE2;
		kernel.createProcess(new Idle(kernel, TPState.NEW, this, -1, new ArrayList<TElement>()));
	}
	
	private void phase2() throws ResourceRequestInterrupt {
		phase = Phase.PHASE10;
		this.kernel.requestResource(this, ResourceClass.SHUTDOWN, null); //TODO: request Shutdown resource
	}
	
	private void phase10() throws ShutDownInterrupt {
		System.out.println("Destroy all system processes");
		System.out.println("Destroy all system resources");
		throw new ShutDownInterrupt();
	}
	
}
