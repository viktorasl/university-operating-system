package machine;

import java.util.ArrayList;
import java.util.List;

public abstract class MemoryListable {
	
	protected int trackSize;
	protected int tracksCount;
	protected String[] memory;
	protected List<OperativeMemoryChangeListener> memChangeListeners;
	
	public MemoryListable(int tracksCount, int trackSize) {
		this.trackSize = trackSize;
		this.tracksCount = tracksCount;
		this.memory = new String[tracksCount * trackSize];
		this.memChangeListeners = new ArrayList<OperativeMemoryChangeListener>();
		
		for (int i = 0; i < tracksCount; i++) {
			for (int j = 0; j < trackSize; j++) {
				occupyMemory(i, j, "0");
			}
		}
	}
	
	public void occupyMemory(int track, int idx, String value) {
		this.memory[track * this.trackSize + idx] = value;
		for (OperativeMemoryChangeListener l : memChangeListeners) {
			l.memoryChanged(track, idx, value);
		}
	}
	
	public String getMemory(int track, int idx) {
		return memory[track * this.trackSize + idx];
	}
	
	public void addOperativeMemoryChangeListener(OperativeMemoryChangeListener l) {
		memChangeListeners.add(l);
	}
	
	public void removeOperativeMemoryChangeListener(OperativeMemoryChangeListener l) {
		memChangeListeners.remove(l);
	}
	
	public int getTrackSize() {
		return trackSize;
	}
	
	public int getTracksCount() {
		return tracksCount;
	}
	
	public int getTotalSize() {
		return memory.length;
	}
	
	public abstract String getTitle();
	
}
