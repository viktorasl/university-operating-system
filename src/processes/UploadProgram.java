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
		kernel.requestResource(this, ResourceClass.LOADPROGRAM, 0);
	}

	public void phase2() {
		phase = 3;
		kernel.requestResource(this, ResourceClass.GENERALMEMORY, 0);
	}
	
	public void phase3() {
		phase = 4;
		kernel.requestResource(this, ResourceClass.CHANNELDEVICE, 0);
	}
	
	public void phase4() throws Exception {
		phase = 5;
		TElement loadProgram = getElement(ResourceClass.LOADPROGRAM);
		String[] addresses = loadProgram.getInfo().split(":");
		int start = Integer.valueOf(addresses[0]);
		int end = Integer.valueOf(addresses[1]);
		int gmIdx = 0;
		for (int i = start; i <= end; i++) {
			kernel.getGeneralMemory()[gmIdx++] = kernel.getHdd().getMemory(i / 10, i % 10);
		}
		TElement channelDevice = getElement(ResourceClass.CHANNELDEVICE);
		kernel.releaseResource(ResourceClass.CHANNELDEVICE, channelDevice);
	}
	
	public void phase5() {
		phase = 1;
		kernel.releaseResource(ResourceClass.PROGRAMLOADED, new TElement(null, this, null));
	}
	
}
