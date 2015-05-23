package machine;

public class HardDrive extends MemoryListable {

	public HardDrive(int tracksCount, int trackSize) {
		super(tracksCount, trackSize);
	}

	@Override
	public String getTitle() {
		return "Hard Drive";
	}

}
