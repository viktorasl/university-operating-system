package processes;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.locks.Condition;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;
import models.TResource;

public class StartStop extends TProcess {

	public StartStop(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
	}
	
	@Override
	public void run() {
		suspendProcess();
		System.out.println("Create all system resources");
		System.out.println("Create all system processes");
		this.requestResource(new TResource());
		System.out.println("Destroy all system processes");
		System.out.println("Destroy all system resources");
	}
	
}
