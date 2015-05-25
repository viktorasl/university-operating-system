package processes;

import java.util.ArrayList;
import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class MainProcess extends TProcess {

	public MainProcess(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
		// TODO Auto-generated constructor stub
	}
	
	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.PROGRAMVALID, 0);
	}
	
	public void phase2() throws Exception {
		phase = 1;
		TElement programValid = getElement(ResourceClass.PROGRAMVALID);
		if (programValid.getInfo() != null) {
			List<TElement> jobHelperElements = new ArrayList<TElement>();
			jobHelperElements.add(programValid);
			kernel.createProcess(new JobHelper(kernel, TPState.READY, this, 1, jobHelperElements));
		} else {
			TProcess proc = programValid.getCreator();
			kernel.destroyProcess(proc);
		}	
	}

}
