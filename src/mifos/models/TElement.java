package mifos.models;

public class TElement {
	final TProcess proc;
	final TProcess creator;
	final String info;
	int target = -1;
	TResource resource;
	
	public TElement (TProcess proc, TProcess creator, String info) {
		this.proc = proc;
		this.creator = creator;
		this.info = info;
	}
	
	public TElement (TProcess proc, TProcess creator, String info, int target) {
		this(proc, creator, info);
		this.target = target;
	}
	
	public TResource getResource() {
		return resource;
	}
	
	public TProcess getProc() {
		return proc;
	}
	
	public TProcess getCreator() {
		return creator;
	}
	
	public String getInfo() {
		return info;
	}
	
	public int getTarget() {
		return target;
	}
	
	public void assignToResource(TResource resource) {
		this.resource = resource;
	}
}
