package models;

public class TWaitingProc {
	TProcess receiver;
	int amount;
	String target;
	
	public TWaitingProc(TProcess receiver, int amount, String target) {
		this.receiver = receiver;
		this.target = target;
		this.amount = amount;
	}
	
	public TProcess getReceiver() {
		return receiver;
	}
	
	public int getAmount() {
		return amount;
	}
}
