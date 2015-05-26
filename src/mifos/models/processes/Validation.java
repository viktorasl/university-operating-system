package mifos.models.processes;

import java.util.List;

import mifos.models.TElement;
import mifos.models.TKernel;
import mifos.models.TPState;
import mifos.models.TProcess;
import mifos.models.TResource.ResourceClass;

public class Validation extends TProcess {

	public Validation(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
		// TODO Auto-generated constructor stub
	}
	
	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.PROGRAMLOADED, 0);
	}
	
	public void phase2() throws Exception {
		phase = 3;
		String[] generalMemory = kernel.getGeneralMemory();
		getElement(ResourceClass.PROGRAMLOADED);
		
		for (int i = 0; i < generalMemory.length; i++) {
			if (i == 0 && !generalMemory[i].equalsIgnoreCase("$TASK")) {
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Program is not in valid format ($TASK missing)"));
				return;
			}
			if (i > 111) {
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Program is not in valid format (too large)"));
				return;
			}
			if (generalMemory[i].equalsIgnoreCase("$END")) {
				phase = 1;
				int requiredTracks = (int) Math.ceil((i - 1)/10.);
				kernel.releaseResource(ResourceClass.PROGRAMVALID, new TElement(null, this, String.valueOf(requiredTracks)));
				return;
			}
		}
		kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Program is not in valid format ($END missing)"));
	}
	
	public void phase3() {
		phase = 1;
		kernel.releaseResource(ResourceClass.GENERALMEMORY, new TElement(null, this, null));
	}

}
