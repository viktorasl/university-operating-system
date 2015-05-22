package processes;

import java.util.List;

import machine.interrupts.MachineInterrupt;
import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class JobHelper extends TProcess {
	
	int needPages;
	TElement[] vmMemory;
	
	public JobHelper(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
		// TODO Auto-generated constructor stub
	}
	
	public void phase1() throws Exception {
		phase = 2;
		TElement programValid = getElement(ResourceClass.PROGRAMVALID);
		needPages = Integer.parseInt(programValid.getInfo());
		int available = kernel.availableResourceElementsFor(this, ResourceClass.PAGES);
		if (available >= needPages + 1) { // +1 because of page table
			kernel.requestResource(this, ResourceClass.PAGES, null, needPages + 1);
		} else {
			phase = 10;
			kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Too low memory to run a program"));
		}
	}
	
	// Initializing virtual machine
	public void phase2() throws Exception {
		phase = 5;
		vmMemory = getElements(ResourceClass.PAGES, needPages + 1);
		TElement pageTable = vmMemory[0]; // First element is always a page table
		
		int pageTableTrack = Integer.parseInt(pageTable.getInfo());
		// Clear VM page table
		for (int i = 0; i < kernel.getRam().getTrackSize(); i++) {
			kernel.getRam().occupyMemory(pageTableTrack, i, String.valueOf(0));
		}
		// Fill VM page table
		for (int i = 0; i < needPages; i++) {
			kernel.getRam().occupyMemory(pageTableTrack, i, vmMemory[i + 1].getInfo());
		}
		
		// Creating virtual machine from program in general memory
		String[] generalMemory = kernel.getGeneralMemory();
		int i = 1;
		int page = 1;
		int trackIdx = 0;
		int idxInTrack = 10;
		while (! generalMemory[i].equalsIgnoreCase("$END")) {
			if (idxInTrack > 9) {
				// Getting new page
				trackIdx = Integer.parseInt(vmMemory[page++].getInfo());
				idxInTrack = 0;
			}
			kernel.getRam().occupyMemory(trackIdx, idxInTrack, generalMemory[i]);
			idxInTrack++;
			i++;
		}
		
		// TODO: remove
		kernel.print("Page table info: " + pageTable.getInfo());
		
		kernel.getProcessor().setPc(0);
		kernel.getProcessor().setMode(1);
		kernel.getProcessor().setTi(10);
		try {
			while (true) {
				kernel.getProcessor().step();
			}
		} catch (MachineInterrupt interrupt) {
			// TODO: interrupt handling
			kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Received interrupt running virtual machine"));
		}
	}
	
	public void phase5() {
		phase = 4;
		kernel.requestResource(this, ResourceClass.INPUTEDLINE, null);
	}
	
	public void phase3() {
		phase = 10;
		kernel.releaseResource(ResourceClass.GENERALMEMORY, new TElement(null, this, null));
	}
	
	// Destroying virtual machine
	public void phase4() throws Exception {
		phase = 10;
		kernel.releaseResource(ResourceClass.PAGES, vmMemory);
	}
	
	public void phase10() {
		phase = 1;
		kernel.releaseResource(ResourceClass.PROGRAMVALID, new TElement(null, this, null));;
	}
	
}
