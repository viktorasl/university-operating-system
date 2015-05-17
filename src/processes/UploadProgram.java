package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class UploadProgram extends TProcess {

	public UploadProgram(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.LOADPROGRAM, null);
	}

	public void phase2() {
		phase = 3;
		kernel.requestResource(this, ResourceClass.GENERALMEMORY, null);
	}
	
	public void phase3() {
		phase = 4;
		kernel.requestResource(this, ResourceClass.CHANNELDEVICE, null);
	}
	
	public void phase4() throws Exception {
		phase = 5;
		System.out.println("Copy program to general memory");
		TElement channelDevice = getElement(ResourceClass.CHANNELDEVICE);
		kernel.releaseResource(ResourceClass.GENERALMEMORY, channelDevice);
	}
	
	public void phase5() {
		phase = 1;
		kernel.releaseResource(ResourceClass.PROGRAMLOADED, new TElement(null, this, null));
	}
	
}
