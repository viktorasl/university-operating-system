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
	
	TKernel kernel = new TKernel();
	final JButton resumeButton = new JButton("Resume");
	
	public static void main(String[] args) {
		new OperatingSystem();
	}
	
	public OperatingSystem() {
		getContentPane().setLayout(new GridLayout(1, 3));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Operating System simulator");
		setSize(600, 400);
		setResizable(false);
		
		kernel.onUpdate(() -> update());
		
		new Thread(kernel).start();
		
		resumeButton.setEnabled(false);
		resumeButton.addActionListener((e) -> {
			kernel.getLock().lock();
			kernel.getCond().signalAll();
			kernel.getLock().unlock();
		});
		getContentPane().add(resumeButton);
		
		setVisible(true);
	}
	
	private void update() {
		System.out.println("Update GUI list");
		resumeButton.setEnabled(true);
	}
}
