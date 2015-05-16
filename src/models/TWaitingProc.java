package models;

public class TWaitingProc {
	TProcess receiver;
	int amount;
	String target;
	
	public TWaitingProc(TProcess receiver, String target) {
		this.receiver = receiver;
		this.target = target;
	}
}
