package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class VMInterrupt extends TProcess {

	public VMInterrupt(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.INTERRUPT, null);
	}
	
	public void phase2() throws Exception {
		TElement interrupt = getElement(ResourceClass.INTERRUPT);
		System.out.println("+++ Identifying interrupt type");
		System.out.println("+++ Setting InterruptInfo resource info");
		phase = 1;
		// TODO: release INTERRUPTINFO resource for required resource
	}
	
}
