package processes;

import java.security.InvalidParameterException;
import java.util.List;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource.ResourceClass;

public class CommandPrompt extends TProcess {
	
	int start = -1;
	
	public CommandPrompt(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}

	public void phase1() {
		phase = 2;
		kernel.requestResource(this, ResourceClass.INPUTEDLINE, 0);
	}
	
	public void phase2() throws Exception {
		TElement inputedLine = getElement(ResourceClass.INPUTEDLINE);
		phase = 1;
		String info = inputedLine.getInfo();
		try {
			if (info.equalsIgnoreCase("SHTDW")) {
				kernel.releaseResource(ResourceClass.SHUTDOWN, new TElement(null, this, null));
			} else if (info.startsWith("LS")) {
				String address = info.substring(2, Math.min(info.length(), 5));
				start = Integer.valueOf(address);
				kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Start:=" + start));
			} else if (start >= 0 && info.startsWith("LE")) {
				String address = info.substring(2, Math.min(info.length(), 5));
				int end = Integer.valueOf(address);
				
				if (end > 1000 || (end - start) > 100 || (end - start) <= 0) {
					throw new InvalidParameterException("End address is invalid. Setting resetted");
				}
				
				kernel.releaseResource(ResourceClass.LOADPROGRAM, new TElement(null, this, start + ":" + end));
			}
		} catch (NumberFormatException e) {
			kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Address should be numeric. Setting resetted"));
			start = -1;
		} catch (InvalidParameterException e) {
			kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, e.getMessage()));
			start = -1;
		}
	}
	
}
