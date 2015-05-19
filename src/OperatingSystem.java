import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import models.TKernel;

public class OperatingSystem extends JFrame {
	
	private static final long serialVersionUID = 1989176057721328389L;
	
	final TKernel kernel;
	final JButton resumeButton = new JButton("Resume");
	final ProcessesTableModel processesTable;
	
	public static void main(String[] args) {
		new OperatingSystem();
	}
	
	public OperatingSystem() {
		getContentPane().setLayout(new GridLayout(1, 3));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Operating System simulator");
		setSize(600, 400);
		setResizable(false);
		
		kernel = new TKernel(true);
		
		processesTable = new ProcessesTableModel();
		getContentPane().add(new JTable(processesTable));
		
		JTextArea printer = new JTextArea();
		JScrollPane printerScroll = new JScrollPane(printer);
		printer.setEditable(false);
		kernel.setPrinter(printer);
		getContentPane().add(printerScroll);
		
		getContentPane().add(setupControlPanel());
		
		kernel.onUpdate(() -> update());
		new Thread(kernel).start();
		
		setVisible(true);
	}
	
	private JPanel setupControlPanel() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		resumeButton.setEnabled(false);
		resumeButton.addActionListener((e) -> {
			kernel.getLock().lock();
			kernel.getCond().signalAll();
			kernel.getLock().unlock();
		});
		controlPanel.add(resumeButton);
		
		final JCheckBox stepRun = new JCheckBox("By step", true);
		stepRun.addActionListener((e) -> {
			kernel.setStepRun(stepRun.isSelected());
		});
		controlPanel.add(stepRun);
		
		final JTextField input = new JTextField();
		input.addActionListener((e) -> {
			kernel.setInputedLine(input.getText());
			input.setText("");
		});
		controlPanel.add(input);
		
		return controlPanel;
	}
	
	private void update() {
		processesTable.setProcesses(kernel.getOSProcesses());
		processesTable.fireTableDataChanged();
		resumeButton.setEnabled(true);
	}
}
