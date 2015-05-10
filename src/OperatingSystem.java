import java.util.ArrayList;

public class OperatingSystem {
	
	public static void main(String[] args) {
		final Thread t = new Thread(() -> {
			TKernel kernel = new TKernel();
			kernel.onUpdate(() -> System.out.println("Update GUI list"));
			kernel.createProcess(null, TPState.READY, 0, new ArrayList<TElement>());
		});
		t.start();
	}
}
