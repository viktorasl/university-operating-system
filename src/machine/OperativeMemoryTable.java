package machine;

import javax.swing.table.DefaultTableModel;

public class OperativeMemoryTable extends DefaultTableModel {

	private static final long serialVersionUID = -8052788013480634187L;

	public OperativeMemoryTable(String[] columnNames, MemoryListable memory){
		super(columnNames, 0);
		
		for (int i = 0; i < memory.getTracksCount(); i++) {
			for (int j = 0; j < memory.getTrackSize(); j++) {
				this.addRow(new Object[]{i * memory.getTrackSize() + j, memory.getMemory(i, j)});
			}
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {                
		return false;
	}
	
}
