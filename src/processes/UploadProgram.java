package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class UploadProgram extends TProcess {

	private enum Phase { P1, P2, P3, P4, P5 }
	Phase phase = Phase.P1;
	
	public UploadProgram(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	@Override
	public String getExternalName() {
		return "UploadProgram";
	}

	@Override
	public void resume() throws Exception {
		switch (phase) {
		case P1: p1(); break;
		case P2: p2(); break;
		case P3: p3(); break;
		case P4: p4(); break;
		case P5: p5(); break;
		}
	}
	
	private void p1() {
		phase = Phase.P2;
		kernel.requestResource(this, ResourceClass.LOADPROGRAM, null);
	}

	private void p2() {
		phase = Phase.P3;
		kernel.requestResource(this, ResourceClass.GENERALMEMORY, null);
	}
	
	private void p3() {
		phase = Phase.P4;
		kernel.requestResource(this, ResourceClass.CHANNELDEVICE, null);
	}
	
	private void p4() throws Exception {
		phase = Phase.P5;
		System.out.println("Copy program to general memory");
		TElement channelDevice = getElement(ResourceClass.CHANNELDEVICE);
		kernel.releaseResource(ResourceClass.GENERALMEMORY, channelDevice);
	}
	
	private void p5() {
		phase = Phase.P1;
		kernel.releaseResource(ResourceClass.PROGRAMLOADED, new TElement(null, this, null));
	}
	
}
