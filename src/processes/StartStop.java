package processes;

import interrupts.ShutDownInterrupt;

import java.util.ArrayList;
import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class StartStop extends TProcess {

	private enum Phase { PHASE1, PHASE2, PHASE3, PHASE4, PHASE10, PHASE9 }	
	
	private Phase phase = Phase.PHASE1;
	
	public StartStop(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	@Override
	public void resume() throws Exception {
//		System.out.println(getExternalName() + ":" + phase.toString());
//		Method method = this.getClass().getDeclaredMethod( phase.toString().toLowerCase() );
//		method.invoke(this);
		switch (phase) {
			case PHASE1: phase1(); break;
			case PHASE2: phase2(); break;
			case PHASE3: phase3(); break;
			case PHASE4: phase4(); break;
			case PHASE9: phase9(); break;
			case PHASE10: phase10(); break;
		}
	}
	
	@Override
	public String getExternalName() {
		return "StartStop";
	}
	
	private void phase1() {
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
		
		phase = Phase.PHASE2;
		kernel.createProcess(new Idle(kernel, TPState.NEW, this, -1, new ArrayList<TElement>()));
	}
	
	private void phase2() {
		phase = Phase.PHASE3;
		kernel.createProcess(new CommandPrompt(kernel, TPState.NEW, this, 1, new ArrayList<TElement>()));
	}
	
	private void phase3() {
		phase = Phase.PHASE4;
		kernel.createProcess(new PrintLine(kernel, TPState.NEW, this, 1, new ArrayList<TElement>()));
	}
	
	private void phase4() {
		phase = Phase.PHASE9;
		kernel.createProcess(new UploadProgram(kernel, TPState.NEW, this, 1, new ArrayList<TElement>()));
	}
	
	private void phase9() {
		phase = Phase.PHASE10;
		this.kernel.requestResource(this, ResourceClass.SHUTDOWN, null); //TODO: request Shutdown resource
	}
	
	private void phase10() throws ShutDownInterrupt {
		System.out.println("Destroy all system processes");
		System.out.println("Destroy all system resources");
		throw new ShutDownInterrupt();
	}
	
}
