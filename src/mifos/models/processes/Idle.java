package mifos.models.processes;

import java.util.List;

import mifos.models.TElement;
import mifos.models.TKernel;
import mifos.models.TPState;
import mifos.models.TProcess;
import mifos.models.TResource.ResourceClass;

public class Idle extends TProcess {
	
	public Idle(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.IDLE, 0);
	}
	
	public void phase2() throws Exception {
		phase = 1;
		getElement(ResourceClass.IDLE);
	}

}
