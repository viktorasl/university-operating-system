import javax.swing.table.AbstractTableModel;

import models.TProcess;


public class ProcessesTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5565391086322521610L;
	
	TProcess[] processes = new TProcess[]{};
	
	public void setProcesses(TProcess[] processes) {
		this.processes = processes;
	}

	@Override
	public int getRowCount() {
		return processes.length;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int columnIndex) {
		String[] columnNames = new String[]{"Name", "ID", "Status"};
		return columnNames[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TProcess process = processes[rowIndex];
		switch (columnIndex) {
			case 0: return process.getExternalName();
			case 1: return process.getpID();
			case 2: return process.getpState().toString();
		}
		return null;
	}

}
