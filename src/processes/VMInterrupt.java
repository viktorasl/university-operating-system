package processes;

import java.util.List;

import machine.interrupts.MachineInterrupt.InterruptType;
import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class VMInterrupt extends TProcess {

	public VMInterrupt(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.INTERRUPT, 0);
	}
	
	public void phase2() throws Exception {
		phase = 1;
		TElement interrupt = getElement(ResourceClass.INTERRUPT);
		String[] interruptInfo = interrupt.getInfo().split(":");
		
		switch (Integer.valueOf(interruptInfo[0])) {
			case 1: releaseInfo(interrupt.getCreator(), InterruptType.HALT); return;
			case 2: releaseInfo(interrupt.getCreator(), InterruptType.PRINT); return;
			case 3: releaseInfo(interrupt.getCreator(), InterruptType.SCAN); return;
		}
		switch (Integer.valueOf(interruptInfo[1])) {
			case 1: releaseInfo(interrupt.getCreator(), InterruptType.OUTOFVIRTUALMEMORY); return;
			case 2: releaseInfo(interrupt.getCreator(), InterruptType.BADCOMMAND); return;
			case 3: releaseInfo(interrupt.getCreator(), InterruptType.REQUESTMEM); return;
			case 4: releaseInfo(interrupt.getCreator(), InterruptType.FREEMEM); return;
		}
		if (Integer.valueOf(interruptInfo[2]) == 0) {
			releaseInfo(interrupt.getCreator(), InterruptType.TIMER);
		}
		throw new Exception("Unexpected interrupt type");
	}
	
	private void releaseInfo(TProcess proc, InterruptType type) {
		kernel.releaseResource(ResourceClass.INTERRUPTINFO, new TElement(proc, this, type.toString()));
	}
	
}
