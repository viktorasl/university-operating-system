package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class Validation extends TProcess {

	public Validation(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
		// TODO Auto-generated constructor stub
	}
	
	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.PROGRAMLOADED, null);
	}
	
	public void phase2() {
		System.out.println("+++ Reading and analyzing given program code");
		phase = 1;
		kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Program is not in valid format"));
	}

}
