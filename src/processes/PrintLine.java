package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class PrintLine extends TProcess {
	
	private enum Phase { PHASE1, PHASE2, PHASE3 }
	Phase phase = Phase.PHASE1;
	
	public PrintLine(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	@Override
	public String getExternalName() {
		return "PrintLine";
	}

	@Override
	public void resume() throws Exception {
		switch (phase) {
		case PHASE1: phase1(); break;
		case PHASE2: phase2(); break;
		case PHASE3: phase3(); break;
		}
	}

	private void phase1() {
		phase = Phase.PHASE2;
		kernel.requestResource(this, ResourceClass.LINETOPRINT, null);
	}
	
	private void phase2() {
		phase = Phase.PHASE3;
		kernel.requestResource(this, ResourceClass.CHANNELDEVICE, null);
	}
	
	private void phase3() throws Exception {
		phase = Phase.PHASE1;
		TElement lineToPrint = getElement(ResourceClass.LINETOPRINT);
		System.out.println(lineToPrint.getInfo());
		TElement channelDevice = getElement(ResourceClass.CHANNELDEVICE);
		kernel.releaseResource(ResourceClass.CHANNELDEVICE, channelDevice);
	}

}
