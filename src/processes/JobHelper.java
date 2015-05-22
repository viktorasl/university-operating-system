package processes;

import java.util.List;

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
			kernel.requestResource(this, ResourceClass.PAGES, null, 10);
		} else {
			phase = 10;
			kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Too low memory to run a program"));
		}
	}
	
	// Initializing virtual machine
	public void phase2() throws Exception {
		phase = 5;
		vmMemory = getElements(ResourceClass.PAGES, needPages);
		TElement pageTable = vmMemory[0]; // First element is always a page table
		
		int pageTableTrack = Integer.parseInt(pageTable.getInfo());
		for (int i = 0; i < needPages; i++) {
			kernel.getRam().occupyMemory(pageTableTrack, i, String.valueOf(i));
		}
		kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Page table info: " + pageTable.getInfo()));
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
