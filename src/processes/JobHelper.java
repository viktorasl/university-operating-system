package processes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import machine.Processor;
import machine.interrupts.MachineInterrupt;
import machine.interrupts.MachineInterrupt.InterruptType;
import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class JobHelper extends TProcess {
	
	int needPages;
	List<TElement> vmMemory;
	int requestedPageAddr;
	
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
			kernel.requestResource(this, ResourceClass.PAGES, 0, needPages + 1);
		} else {
			phase = 10;
			kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Too low memory to run a program"));
		}
	}
	
	// Initializing virtual machine
	public void phase2() throws Exception {
		phase = 5;
		vmMemory = new LinkedList<TElement>(Arrays.asList(getElements(ResourceClass.PAGES, needPages + 1)));
		TElement pageTable = vmMemory.get(0); // First element is always a page table
		
		int pageTableTrack = Integer.parseInt(pageTable.getInfo());
		// Clear VM page table
		for (int i = 0; i < kernel.getRam().getTrackSize(); i++) {
			kernel.getRam().occupyMemory(pageTableTrack, i, String.valueOf(0));
		}
		// Fill VM page table
		for (int i = 0; i < needPages; i++) {
			kernel.getRam().occupyMemory(pageTableTrack, i, vmMemory.get(i + 1).getInfo());
		}
		
		// Creating virtual machine from program in general memory
		String[] generalMemory = kernel.getGeneralMemory();
		int i = 1;
		int page = 1;
		int trackIdx = 0;
		int idxInTrack = 10;
		int pc = 0;
		
		while (! generalMemory[i].equalsIgnoreCase("$END")) {
			if (idxInTrack > 9) {
				// Getting new page
				trackIdx = Integer.parseInt(vmMemory.get(page++).getInfo());
				idxInTrack = 0;
			}
			if (i == 1) {
				pc = trackIdx * kernel.getRam().getTrackSize() + idxInTrack;
			}
			kernel.getRam().occupyMemory(trackIdx, idxInTrack, generalMemory[i]);
			idxInTrack++;
			i++;
		}
		
		kernel.releaseResource(ResourceClass.GENERALMEMORY, new TElement(null, this, null));
		// TODO: remove
		kernel.print("Page table info: " + pageTable.getInfo() + "; start pc = " + pc);
		
		kernel.getProcessor().setPtr(Integer.valueOf(vmMemory.get(0).getInfo()));
		kernel.getProcessor().setPc(pc);
		kernel.getProcessor().clearInterruptFlags();
		
		pCPUState = kernel.getProcessor().getCPUState();
	}
	
	public void phase5() throws Exception {
		phase = 9;
		
		kernel.getProcessor().setCPUState(pCPUState);
		kernel.getProcessor().setMode(1);
		
		try {
			while (true) {
				kernel.getProcessor().step();
			}
		} catch (MachineInterrupt interrupt) {
			Processor prcs = kernel.getProcessor();
			String info = prcs.getSi() + ":" + prcs.getPi() + ":" + prcs.getTi();
			kernel.getProcessor().clearInterruptFlags();
			kernel.releaseResource(ResourceClass.INTERRUPT, new TElement(null, this, info));
			pCPUState = kernel.getProcessor().getCPUState();
		}
	}
	
	private void requestMemory() throws Exception {
		phase = 5;
		requestedPageAddr = pCPUState.ar;
		String val = kernel.getRam().getMemory(requestedPageAddr / 10, requestedPageAddr % 10);
		if (val.equalsIgnoreCase("0")) {
			if (kernel.availableResourceElementsFor(this, ResourceClass.PAGES) > 0) {
				phase = 7;
				kernel.requestResource(this, ResourceClass.PAGES, 0, 1);
			} else {
				phase = 4;
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Too low memory to allocate"));
			}
		}
	}
	
	private void freeMemory() throws InterruptedException {
		phase = 5;
		
		int ar = pCPUState.ar;
		String val = kernel.getRam().getMemory(ar / 10, ar % 10);
		if (!val.equalsIgnoreCase("0")) {
			for (TElement page : vmMemory) {
				System.out.println(val + ":" + page.getInfo());
				if (page.getInfo().equalsIgnoreCase(val)) {
					vmMemory.remove(page);
					kernel.getRam().occupyMemory(ar / 10, ar % 10, "0");
					kernel.releaseResource(ResourceClass.PAGES, page);
					break;
				}
			}
		}
	}
	
	public void phase7() throws Exception {
		phase = 5;
		TElement requestedPage = getElement(ResourceClass.PAGES);
		kernel.getRam().occupyMemory(requestedPageAddr / 10, requestedPageAddr % 10, requestedPage.getInfo());
		vmMemory.add(requestedPage);
	}
	
	public void phase6() throws Exception {
		phase = 5;
		TElement inputedLine = getElement(ResourceClass.INPUTEDLINE);
		int addr = inputedLine.getTarget();
		String info = inputedLine.getInfo().substring(0, Math.min(5, inputedLine.getInfo().length()));
		kernel.getRam().occupyMemory(addr / 10, addr % 10, info);
	}
	
	public void phase9() {
		phase = 8;
		kernel.requestResource(this, ResourceClass.INTERRUPTINFO, 0);
	}
	
	public void phase8() throws Exception {
		phase = 4;
		TElement interruptInfo = getElement(ResourceClass.INTERRUPTINFO);
		
		switch (InterruptType.valueOf(interruptInfo.getInfo())) {
			case BADCOMMAND: {
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Invalid command"));
				break;
			}
			case FREEMEM: {
				freeMemory();
				break;
			}
			case REQUESTMEM: {
				requestMemory();
				break;
			}
			case HALT: {
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Task successfuly finished"));
				break;
			}
			case OUTOFVIRTUALMEMORY: {
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Invalid address, out of memory"));
				break;
			}
			case PRINT: {
				phase = 5;
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, kernel.getProcessor().getValueInAddress(pCPUState.ar)));
				break;
			}
			case SCAN: {
				phase = 6;
				kernel.requestResource(this, ResourceClass.INPUTEDLINE, pCPUState.ar);
				break;
			}
			case TIMER: {
				phase = 5;
				break;
			}
		}
	}
	
	public void phase3() {
		phase = 10;
		kernel.releaseResource(ResourceClass.GENERALMEMORY, new TElement(null, this, null));
	}
	
	// Destroying virtual machine
	public void phase4() throws Exception {
		phase = 10;
		kernel.releaseResource(ResourceClass.PAGES, vmMemory.toArray(new TElement[vmMemory.size()]));
	}
	
	public void phase10() {
		phase = 1;
		kernel.releaseResource(ResourceClass.PROGRAMVALID, new TElement(null, this, null));;
	}
	
}
