package angstd.webservices.RADS;

import javax.swing.table.AbstractTableModel;

import angstd.model.arrangement.ArrangementManager;

public class RADSResultsTableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"", "Import", "Score", "Subject ID", "Arrangement String"};
	private Object[][] tableData = null;
	private ArrangementManager arrSet = null; // ArrangementManager of arrangements in this table TODO: convert on the fly?
	
	public String[] getColumnNames() {
		return this.columnNames;
	}
	
	public void setColumnNames(String [] names) {
		this.columnNames = names;
	}
	
    public String getColumnName(int col) {
        return columnNames[col].toString();
    }
	
	public Object[][] getTableData() {
		return this.tableData;
	}

	public void setTableData(Object[][] tableData) {
		this.tableData = tableData;
	}
	
	public int getRowCount() {
		return tableData.length;
	}

	// TODO: set column names
	public int getColumnCount() {
		return columnNames.length;
	}

	public Object getValueAt(int row, int col) {
		return tableData[row][col];
	}
	
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public boolean isCellEditable(int row, int col) {
    	return col == 1;
    }
    
    public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = value;
        fireTableCellUpdated(row, col);
    }
	
}
