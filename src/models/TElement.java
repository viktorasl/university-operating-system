package models;

public class TElement {
	final TProcess proc;
	final TProcess creator;
	final String info;
	TResource resource;
	
	public TElement (TProcess proc, TProcess creator, String info) {
		this.proc = proc;
		this.creator = creator;
		this.info = info;
	}
	
	public void assignToResource(TResource resource) {
		this.resource = resource;
	}
}
