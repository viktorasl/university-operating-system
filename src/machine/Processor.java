package machine;

import machine.interrupts.MachineInterrupt;
import machine.interrupts.MachineInterrupt.InterruptType;
import models.TCPUState;

public class Processor extends Registerable {
	
	int mode; // Machine mode
	int ptr; // Pages table register
	int gr; // General register
	int pc; // Program counter
	int sp; // Stack pointer
	int cf; // Carry flag
	int pi; // Programming interrupt
	int si; // Supervisor interrupt
	int ti; // Timer interrupt
	int ar; // I/O address
	
	OperativeMemory ram;
	
	public Processor(OperativeMemory ram) {	
		this.ram = ram;
	}
	
	public void setMode(int mode) {
		if (this.mode != mode) {
			changes.firePropertyChange(ProcessorRegister.Mode.name(), this.mode, mode);
			this.mode = mode;
		}
	}
	
	private void setGr(int gr) {
		if (this.gr != gr) {
			changes.firePropertyChange(ProcessorRegister.GR.name(), this.gr, gr);
			this.gr = gr;
		}
	}
	
	private void setCf(int cf) {
		if (this.cf != cf) {
			changes.firePropertyChange(ProcessorRegister.CF.name(), this.cf, cf);
			this.cf = cf;
		}
	}
	
	private void setSi(int si) {
		if (this.si != si) {
			changes.firePropertyChange(ProcessorRegister.SI.name(), this.si, si);
			this.si = si;
		}
	}
	
	public int getSi() {
		return si;
	}
	
	public int getPtr() {
		return ptr;
	}

	private void setPi(int pi) {
		if (this.pi != pi) {
			changes.firePropertyChange(ProcessorRegister.PI.name(), this.pi, pi);
			this.pi = pi;
		}
	}
	
	public int getPi() {
		return pi;
	}
	
	private void setSp(int sp) {
		if (this.sp != sp) {
			changes.firePropertyChange(ProcessorRegister.SP.name(), this.sp, sp);
			this.sp = sp;
		}
	}
	
	public void setTi(int ti) {
		if (this.ti != ti) {
			changes.firePropertyChange(ProcessorRegister.TI.name(), this.ti, ti);
			this.ti = ti;
		}
	}
	
	public int getTi() {
		return ti;
	}
	
	private void setAr(int ar) {
		if (this.ar != ar) {
			changes.firePropertyChange(ProcessorRegister.AR.name(), this.ar, ar);
			this.ar = ar;
		}
	}
	
	public int getAr() {
		return ar;
	}
	
	public void setPtr(int ptr) {
		if (this.ptr != ptr) {
			changes.firePropertyChange(ProcessorRegister.PTR.name(), this.ptr, ptr);
			this.ptr = ptr;
		}
	}
	
	public void setPc(int pc) {
		if (this.pc != pc) {
			changes.firePropertyChange(ProcessorRegister.PC.name(), this.pc, pc);
			this.pc = pc;
		}
	}
	
	public int getPc() {
		return pc;
	}
	
	private void incPc() {
		changes.firePropertyChange(ProcessorRegister.PC.name(), this.pc, this.pc + 1);
		setPc(this.pc + 1);
	}
	
	private void push(int value) {
		ram.occupyMemory(sp / 10, sp % 10, String.valueOf(value));
		setSp(sp + 1);
	}
	
	private int pop() {
		setSp(sp - 1);
		return Integer.parseInt(ram.getMemory(sp / 10, sp % 10));
	}
	
	private int buildAddress(String addr) throws OutOfVirtualMemoryException {
		int trackNumber = Integer.parseInt(addr);
		if (trackNumber >= ram.getTracksCount()) {
			throw new OutOfVirtualMemoryException();
		}
		int x = Math.floorDiv(trackNumber, 10);
		if (x >= ram.getTrackSize()) {
			throw new OutOfVirtualMemoryException();
		}
		int y = trackNumber % 10;
		int vmTrackNumber = Integer.valueOf(ram.getMemory(ptr, x));
		if (vmTrackNumber == 0) {
			throw new OutOfVirtualMemoryException();
		}
		int readAddress = vmTrackNumber * 10 + y;
		return readAddress;
	}
	
	private int realTrackNum(String num) {
		return ptr * ram.getTrackSize() + Integer.valueOf(num);
	}
	
	public String getValueInAddress(int addr) {
		int track = addr / 10;
		int idx = addr % 10;
		ram.markMemory(track, idx);
		return ram.getMemory(track, idx);
	}
	
	private void test() throws MachineInterrupt, Exception {
		if ((si + pi > 0) || (ti == 0)) {
			throw new MachineInterrupt("Virtual machine was interrupted");
		}
	}
	
	private void interpretCmd(String cmd) throws MachineInterrupt, Exception {
		incPc();
		int cmdLength = 1;
		
		try {
			switch(cmd.substring(0, 2)) {
				case "GO": {
					int addr = buildAddress(cmd.substring(2, 5));
					setPc(addr);
					break;
				}
				case "MG": {
					int addr = buildAddress(cmd.substring(2, 5));
					setGr(Integer.parseInt(getValueInAddress(addr)));
					break;
				}
				case "MM": {
					int addr = buildAddress(cmd.substring(2, 5));
					ram.occupyMemory(addr / 10, addr % 10, String.valueOf(this.gr));
					break;
				}
				case "GV": {
					int value = Integer.valueOf(cmd.substring(2, 5));
					setGr(value);
					break;
				}
				case "AD": {
					int addr = buildAddress(cmd.substring(2, 5));
					int value = Integer.parseInt(ram.getMemory(addr / 10, addr % 10));
					setGr(this.gr + value);
					break;
				}
				case "CP": {
					int addr = buildAddress(cmd.substring(2, 5));
					int value = Integer.parseInt(ram.getMemory(addr / 10, addr % 10));
					if (this.gr == value) {
						setCf(0);
					} else if (this.gr > value) {
						setCf(1);
					} else {
						setCf(2);
					}
					break;
				}
				case "JE": {
					int addr = buildAddress(cmd.substring(2, 5));
					if (this.cf == 0) {
						setPc(addr);
					}
					break;
				}
				case "JL": {
					int addr = buildAddress(cmd.substring(2, 5));
					if (this.cf == 2) {
						setPc(addr);
					}
					break;
				}
				case "JG": {
					int addr = buildAddress(cmd.substring(2, 5));
					if (this.cf == 1) {
						setPc(addr);
					}
					break;
				}
				case "CL": {
					int addr = buildAddress(cmd.substring(2, 5));
					push(pc);
					setPc(addr);
					break;
				}
				case "RT": {
					setPc(pop());
					break;
				}
				case "PT": {
					cmdLength = 3;
					setAr(buildAddress(String.valueOf(gr)));
					setSi(2);
					break;
				}
				case "SC": {
					cmdLength = 3;
					setAr(buildAddress(String.valueOf(gr)));
					setSi(3);
					break;
				}
				case "RM": {
					if (mode == 1) {
						int trackNum = realTrackNum(cmd.substring(2, 3));
						setAr(trackNum);
						setPi(3);
						break;
					}
				}
				case "FM": {
					if (mode == 1) {
						int trackNum = realTrackNum(cmd.substring(2, 3));
						setAr(trackNum);
						setPi(4);
						break;
					}
				}
				case "HT": {
					if (mode == 1) {
						setSi(1);
						break;
					}
				}
				default: {
					throw new Exception("Unknown command");
				}
			}
			
		} catch (OutOfVirtualMemoryException e) {
			setPi(1);
		} catch (Exception e) {
			System.out.println(((mode == 0)? "Supervisor" : "User") + ": Invalid command");
			if (mode == 1) {
				setPi(2);
			}
		}
		
		if (mode == 1) {
			setTi(Math.max(ti - cmdLength, 0));
			test();
		}
	}
	
	public void step() throws MachineInterrupt, Exception {
		int track = pc / 10;
		int idx = pc % 10;
		String cmd = getValueInAddress(pc);
		System.out.println(track / 10 + "" + track % 10 + ":" + idx + "\t" + cmd);
//		Thread.sleep(1000);
		interpretCmd(cmd);
	}

	public void clearInterruptFlags() {
		setSi(0);
		setPi(0);
		setTi(10);
	}
	
	public TCPUState getCPUState() {
		return new TCPUState(mode, ptr, gr, pc, sp, cf, pi, si, ti, ar);
	}
	
	public void setCPUState(TCPUState state) {
		setMode(state.mode);
		setPtr(state.ptr);
		setGr(state.gr);
		setPc(state.pc);
		setSp(state.sp);
		setCf(state.cf);
		setPi(state.pi);
		setSi(state.si);
		setTi(state.ti);
		setAr(state.ar);
	}
}
