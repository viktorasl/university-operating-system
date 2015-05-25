package machine.interrupts;

public class MachineInterrupt extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5200130498662999430L;

	public enum InterruptType {
		HALT,
		PRINT,
		SCAN,
		OUTOFVIRTUALMEMORY,
		BADCOMMAND,
		REQUESTMEM,
		FREEMEM,
		TIMER
	}
	
	public MachineInterrupt(String string) {
		super(string);
	}

}
