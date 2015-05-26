import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import models.TElement;
import models.TProcess;


public class ProcessesTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5565391086322521610L;
	
	final List<String[]> entries = new ArrayList<String[]>();
	
	public void setProcesses(TProcess[] processes) {
		entries.clear();
		for (TProcess process : processes) {
			String[] processEntry = new String[]{process.getExternalName(), String.valueOf(process.getpID()), process.getpState().toString()};
			entries.add(processEntry);
			for (TElement element : process.getpORElements()) {
				String[] elementEntry = new String[]{"", "", element.getResource().getResourceClass().toString()};
				entries.add(elementEntry);
			}
		}
	}

	@Override
	public int getRowCount() {
		return entries.size();
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
		return entries.get(rowIndex)[columnIndex];
	}

}
