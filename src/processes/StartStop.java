package processes;

import java.util.List;
import java.util.concurrent.locks.Condition;

import models.TElement;
import models.TKernel;
import models.TPState;
import models.TProcess;

public class StartStop extends TProcess {

	enum State { PART1, PART2 };
	
	State current = State.PART1;
	
	public StartStop(TKernel kernel, TPState pState, TProcess pParent,
			int pPriority, List<TElement> pORElements) {
		super(kernel, pState, pParent, pPriority, pORElements);
		resume();
	}
	
	private void part1() {
		System.out.println("Create all reusable resources/processes");
	}
	
	private void part2() {
		System.out.println("Destroy all");
	}

	@Override
	public void resume() {
		switch(current) {
			case PART1:
				part1();
				break;
			case PART2:
				part2();
				break;
		}
	}

}
