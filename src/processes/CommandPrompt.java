package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class CommandPrompt extends TProcess {
	
	public CommandPrompt(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.INPUTEDLINE, null);
	}
	
	public void phase2() throws Exception {
		TElement inputedLine = getElement(ResourceClass.INPUTEDLINE);
		phase = 1;
		if (inputedLine.getInfo().equalsIgnoreCase("SHTDW")) {
			kernel.releaseResource(ResourceClass.SHUTDOWN, new TElement(null, this, null));
		}
	}

}
