import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import machine.MemoryListable;
import machine.MemoryTable;
import machine.OperativeMemoryChangeListener;
import machine.OperativeMemoryTable;
import models.TKernel;

//TODO: VM procedure (stack) call (CL)
//TODO: VM PT to hard drive
//TODO: VM SC from hard drive
//TODO: interrupt handler process
//TODO: show assigned resources for each process

public class OperatingSystem extends JFrame {
	
	private static final long serialVersionUID = 1989176057721328389L;
	
	final TKernel kernel;
	final JButton resumeButton = new JButton("Resume");
	final ProcessesTableModel processesTable;
	final MemoryTable hddTable;
	
	public static void main(String[] args) {
		new OperatingSystem();
	}
	
	public OperatingSystem() {
		getContentPane().setLayout(new GridLayout(1, 3));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Operating System simulator");
		setSize(1024, 640);
		setResizable(false);
		
		kernel = new TKernel(false);
		
		processesTable = new ProcessesTableModel();
		getContentPane().add(new JTable(processesTable));
		
		JPanel memoriesPanel = new JPanel();
		memoriesPanel.setLayout(new GridLayout(2, 1));
		memoriesPanel.add(wrapMemoryTable(initializeMemoryTable(kernel.getRam())));
		hddTable = initializeMemoryTable(kernel.getHdd());
		memoriesPanel.add(wrapMemoryTable(hddTable));
		getContentPane().add(memoriesPanel);
		
		JTextArea printer = new JTextArea();
		JScrollPane printerScroll = new JScrollPane(printer);
		printer.setEditable(false);
		kernel.setPrinter(printer);
		getContentPane().add(printerScroll);
		
		getContentPane().add(setupControlPanel());
		
		kernel.onUpdate(() -> update());
		new Thread(kernel).start();
		
		setVisible(true);
		
		uploadProgram(new File("demofiles/testProcedure.dm"));
	}
	
	private void uploadProgram(File f) {
		FileReader fr = null;
		
		try (BufferedReader br = new BufferedReader(fr = new FileReader(f))) {
		    String line;
		    int available = 100;
		    int address = hddTable.getSelectedRow();
		    while ((line = br.readLine()) != null) {
		       	if (available <= 0) {
		       		break;
		       	}
		       	String ln = line.substring(0, Math.min(5, line.length()));
		       	kernel.getHdd().occupyMemory(address / 10, address % 10, ln);
		       	address++;
		    	available--;
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
		
		final JCheckBox stepRun = new JCheckBox("By step", false);
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
		
		JButton fileUploadBtn = new JButton("Upload file");
		fileUploadBtn.addActionListener(e -> {
			final JFileChooser fileChooser = new JFileChooser(".");
			if (fileChooser.showSaveDialog(controlPanel) == JFileChooser.APPROVE_OPTION) {
				uploadProgram(fileChooser.getSelectedFile());
			}
		});
		
		controlPanel.add(fileUploadBtn);
		
		return controlPanel;
	}
	
	private MemoryTable initializeMemoryTable(MemoryListable memory) {
		String[] columnNames = {"Address", "Content"};
		final OperativeMemoryTable tableModel = new OperativeMemoryTable(columnNames, memory);
		final MemoryTable dataTable = new MemoryTable(tableModel);
		
		memory.addOperativeMemoryChangeListener(new OperativeMemoryChangeListener() {
			
			@Override
			public void memoryChanged(int track, int idx, String value) {
				int i = track * memory.getTrackSize() + idx;
				tableModel.removeRow(i);
				tableModel.insertRow(i, new Object[]{i, value});
			}

			@Override
			public void memoryExecuted(int track, int idx) {
				int row = track * memory.getTrackSize() + idx;
				dataTable.changeSelection(row, 0, false, false);
			}
			
		});
		
		return dataTable;
	}
	
	private JScrollPane wrapMemoryTable(MemoryTable dataTable) {
		JScrollPane scrollPane = new JScrollPane(dataTable);
		
		MemoryListable memory = dataTable.getModel().getMemory();
		
		scrollPane.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                memory.getTitle(),
                TitledBorder.CENTER,
                TitledBorder.TOP));
		
		return scrollPane;
	}
	
	private void update() {
		processesTable.setProcesses(kernel.getOSProcesses());
		processesTable.fireTableDataChanged();
		resumeButton.setEnabled(true);
	}
}
