package mifos.models.processes;

import java.util.ArrayList;
import java.util.List;

import mifos.interrupts.ShutDownInterrupt;
import mifos.models.TElement;
import mifos.models.TKernel;
import mifos.models.TPState;
import mifos.models.TProcess;
import mifos.models.TResource.ResourceClass;

public class StartStop extends TProcess {

	public StartStop(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	public void phase1() {
		kernel.createResource(this, ResourceClass.IDLE, true, null);
		kernel.createResource(this, ResourceClass.SHUTDOWN, false, null);
		kernel.createResource(this, ResourceClass.INPUTEDLINE, false, null);
		kernel.createResource(this, ResourceClass.LOADPROGRAM, true, null);
		kernel.createResource(this, ResourceClass.PROGRAMLOADED, true, null);
		kernel.createResource(this, ResourceClass.GENERALMEMORY, false, new TElement[]{ new TElement(null, this, null) });
		kernel.createResource(this, ResourceClass.CHANNELDEVICE, true, new TElement[]{ new TElement(null, this, null) });
		kernel.createResource(this, ResourceClass.PROGRAMVALID, false, null);
		kernel.createResource(this, ResourceClass.LINETOPRINT, false, null);
		kernel.createResource(this, ResourceClass.INTERRUPTINFO, true, null);
		kernel.createResource(this, ResourceClass.INTERRUPT, true, null);
		
		List<TElement> pages = new ArrayList<TElement>();
		for (int i = 0; i < kernel.getRam().getTracksCount(); i++) {
			pages.add(new TElement(null, this, String.valueOf(i)));
		}
		kernel.createResource(this, ResourceClass.PAGES, true, pages.toArray(new TElement[pages.size()]));
		
		phase = 2;
		kernel.createProcess(new Idle(kernel, TPState.READY, this, -1, new ArrayList<TElement>()));
	}
	
	public void phase2() {
		phase = 3;
		kernel.createProcess(new CommandPrompt(kernel, TPState.READY, this, 1, new ArrayList<TElement>()));
	}
	
	public void phase3() {
		phase = 4;
		kernel.createProcess(new PrintLine(kernel, TPState.READY, this, 1, new ArrayList<TElement>()));
	}
	
	public void phase4() {
		phase = 5;
		kernel.createProcess(new UploadProgram(kernel, TPState.READY, this, 1, new ArrayList<TElement>()));
	}
	
	public void phase5() {
		phase = 6;
		kernel.createProcess(new Validation(kernel, TPState.READY, this, 1, new ArrayList<TElement>()));
	}
	
	public void phase6() {
		phase = 7;
		kernel.createProcess(new MainProcess(kernel, TPState.READY, this, 1, new ArrayList<TElement>()));
	}
	
	public void phase7() {
		phase = 9;
		kernel.createProcess(new VMInterrupt(kernel, TPState.READY, this, 1, new ArrayList<TElement>()));
	}
	
	public void phase9() {
		phase = 10;
		this.kernel.requestResource(this, ResourceClass.SHUTDOWN, 0); //TODO: request Shutdown resource
	}
	
	public void phase10() throws ShutDownInterrupt {
		if (this.getpCProcesses().size() > 0) {
			kernel.destroyProcess(this.getpCProcesses().poll());
		} else {
			throw new ShutDownInterrupt();
		}
	}
	
}
