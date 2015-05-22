package machine;

public class OperativeMemory extends MemoryListable {
	
	public OperativeMemory(int tracksCount, int trackSize) {
		super(tracksCount, trackSize);
	}
	
	public void markMemory(int track, int idx) {
		for (OperativeMemoryChangeListener l : memChangeListeners) {
			l.memoryExecuted(track, idx);
		}
	}

	@Override
	public String getTitle() {
		return "Operative Memory";
	}
	
}
