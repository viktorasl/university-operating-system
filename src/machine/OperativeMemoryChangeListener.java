package machine;

public interface OperativeMemoryChangeListener {
	
	public void memoryChanged(int track, int idx, String value);
	public void memoryExecuted(int track, int idx);
	
}
