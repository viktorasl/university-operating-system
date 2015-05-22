package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class PrintLine extends TProcess {
	
	public PrintLine(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.LINETOPRINT, 0);
	}
	
	public void phase2() {
		phase = 3;
		kernel.requestResource(this, ResourceClass.CHANNELDEVICE, 0);
	}
	
	public void phase3() throws Exception {
		phase = 1;
		TElement lineToPrint = getElement(ResourceClass.LINETOPRINT);
		kernel.print(lineToPrint.getInfo());
		TElement channelDevice = getElement(ResourceClass.CHANNELDEVICE);
		kernel.releaseResource(ResourceClass.CHANNELDEVICE, channelDevice);
	}

}
