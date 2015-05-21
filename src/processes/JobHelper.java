package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class JobHelper extends TProcess {

	public JobHelper(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
		// TODO Auto-generated constructor stub
	}
	
	public void phase1() throws Exception {
		phase = 2;
		int available = kernel.availableResourceElementsFor(this, ResourceClass.PAGES);
		if (available >= 10) {
			kernel.requestResource(this, ResourceClass.PAGES, null, 10);
		} else {
			phase = 10;
			kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Too low memory to run a program"));
		}
	}
	
	public void phase2() {
		phase = 10;
		kernel.requestResource(this, ResourceClass.INPUTEDLINE, null);
	}
	
	public void phase3() {
		kernel.releaseResource(ResourceClass.GENERALMEMORY, new TElement(null, this, null));
		phase = 10;
	}
	
	public void phase10() {
		phase = 1;
		kernel.releaseResource(ResourceClass.PROGRAMVALID, new TElement(null, this, null));;
	}
	
}
