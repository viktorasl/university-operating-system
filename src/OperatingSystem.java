import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;

import models.TKernel;
import models.TPState;
import models.TElement;

public class OperatingSystem extends JFrame {
	
	private static final long serialVersionUID = 1989176057721328389L;

	boolean stopped = false;
	final Lock lock = new ReentrantLock();
	final Condition os = lock.newCondition();
	
	public static void main(String[] args) {
		new OperatingSystem();
	}
	
	public OperatingSystem() {
		getContentPane().setLayout(new GridLayout(1, 3));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Operating System simulator");
		setSize(600, 400);
		setResizable(false);
		
		new Thread(() -> {
			try {
				startOS();
			} catch (InterruptedException e) {
				System.out.println("OS was interrupted");
			}
		}).start();
		
		JButton button = new JButton("Resume");
		button.addActionListener((e) -> {
			lock.lock();
			os.signalAll();
			lock.unlock();
		});
		getContentPane().add(button);
		
		setVisible(true);
	}
	
	private void startOS() throws InterruptedException {
		TKernel kernel = new TKernel();
		kernel.onUpdate(() -> update());
		while (true){
			lock.lock();
			try {
				kernel.createProcess(null, TPState.READY, 0, new ArrayList<TElement>());
				os.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                lock.unlock();
            }
		}
	}
	
	private void update() {
		System.out.println("Update GUI list");
	}
}
