package processes;

import interrupts.ProcessInterrupt;
import interrupts.ResourceRequestInterrupt;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class Idle extends TProcess {
	
	public Idle(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	@Override
	public void resume() throws ProcessInterrupt {
		phase1();
	}
	
	@Override
	public String getExternalName() {
		return "Idle";
	}
	
	public void phase1() throws ResourceRequestInterrupt {
		kernel.requestResource(this, ResourceClass.IDLE, null);
	}

}
