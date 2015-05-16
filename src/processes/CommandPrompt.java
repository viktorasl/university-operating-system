package processes;

import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class CommandPrompt extends TProcess {
	
	private enum Phase { PHASE1, PHASE2 }
	private Phase phase = Phase.PHASE1;
	
	public CommandPrompt(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	@Override
	public String getExternalName() {
		return "CommandPrompt";
	}

	@Override
	public void resume() throws Exception {
		System.out.println(getExternalName() + ":" + phase.toString());
		switch (phase) {
		case PHASE1: phase1(); break;
		case PHASE2: phase2(); break;
		}
	}
	
	private void phase1() {
		phase = Phase.PHASE2;
		kernel.requestResource(this, ResourceClass.INPUTEDLINE, null);
	}
	
	private void phase2() throws Exception {
		TElement inputedLine = getElement(ResourceClass.INPUTEDLINE);
		System.out.println(inputedLine.getInfo());
		phase1();
	}

}
