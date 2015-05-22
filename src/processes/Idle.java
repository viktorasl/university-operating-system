package processes;

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

	public void phase1() {
		kernel.requestResource(this, ResourceClass.IDLE, 0);
	}

}
