package processes;

import java.util.List;

import machine.Processor;
import machine.interrupts.MachineInterrupt;
import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class VMRunner extends TProcess {

	public VMRunner(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
		// TODO Auto-generated constructor stub
	}
	
	public void phase1() throws Exception {
		kernel.getProcessor().setCPUState(pCPUState);
		kernel.getProcessor().setMode(1);
		
		try {
			while (true) {
				kernel.getProcessor().step();
			}
		} catch (MachineInterrupt interrupt) {
			Processor prcs = kernel.getProcessor();
			String info = prcs.getSi() + ":" + prcs.getPi() + ":" + prcs.getTi() + ":" + prcs.getAr();
			kernel.getProcessor().clearInterruptFlags();
			pCPUState = kernel.getProcessor().getCPUState();
			kernel.releaseResource(ResourceClass.INTERRUPT, new TElement(null, this, info));
		}
	}

}
