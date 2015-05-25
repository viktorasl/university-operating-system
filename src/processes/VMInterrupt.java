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
		String ar = interruptInfo[3];
		
		switch (Integer.valueOf(interruptInfo[0])) {
			case 1: releaseInfo(interrupt.getCreator().getpParent(), InterruptType.HALT, null); return;
			case 2: releaseInfo(interrupt.getCreator().getpParent(), InterruptType.PRINT, ar); return;
			case 3: releaseInfo(interrupt.getCreator().getpParent(), InterruptType.SCAN, ar); return;
		}
		switch (Integer.valueOf(interruptInfo[1])) {
			case 1: releaseInfo(interrupt.getCreator().getpParent(), InterruptType.OUTOFVIRTUALMEMORY, null); return;
			case 2: releaseInfo(interrupt.getCreator().getpParent(), InterruptType.BADCOMMAND, null); return;
			case 3: releaseInfo(interrupt.getCreator().getpParent(), InterruptType.REQUESTMEM, ar); return;
			case 4: releaseInfo(interrupt.getCreator().getpParent(), InterruptType.FREEMEM, ar); return;
		}
		if (Integer.valueOf(interruptInfo[2]) == 0) {
			releaseInfo(interrupt.getCreator(), InterruptType.TIMER, null);
		}
		throw new Exception("Unexpected interrupt type");
	}
	
	private void releaseInfo(TProcess proc, InterruptType type, String ar) {
		String info = type.toString();
		if (ar != null) {
			info += ":" + ar;
		}
		kernel.releaseResource(ResourceClass.INTERRUPTINFO, new TElement(proc, this, info));
	}
	
}
