package angstd.webservices.RADS.util;

import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.DomainArrangement;

public class RADSResultsTableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"", "Import", "Subject ID", "Score", "Arrangement String"};
	private Object[][] tableData = null;
	private HashMap<String, DomainArrangement> arrangementData = null;
	
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
    	System.out.println("Checking if editable!");
    	//return col == 1;
    	return false;
    }
    
    public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
    public void setArrangementData(HashMap<String, DomainArrangement> arrData) {
    	this.arrangementData = arrData;
    }
    
    public HashMap<String, DomainArrangement> getArrangementData() {
    	return this.arrangementData;
    }
	
}
