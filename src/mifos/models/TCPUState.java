package mifos.models;

public class TCPUState {
	
	public final int mode; // Machine mode
	public final int ptr; // Pages table register
	public final int gr; // General register
	public final int pc; // Program counter
	public final int sp; // Stack pointer
	public final int cf; // Carry flag
	public final int pi; // Programming interrupt
	public final int si; // Supervisor interrupt
	public final int ti; // Timer interrupt
	public final int ar; // I/O address
	
	public TCPUState(int mode, int ptr, int gr, int pc, int sp, int cf, int pi, int si, int ti, int ar) {
		this.mode = mode;
		this.ptr = ptr;
		this.gr = gr;
		this.pc = pc;
		this.sp = sp;
		this.cf = cf;
		this.pi = pi;
		this.si = si;
		this.ti = ti;
		this.ar = ar;
	}
	
}
