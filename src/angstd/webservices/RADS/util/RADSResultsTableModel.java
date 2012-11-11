package angstd.webservices.RADS.util;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import angstd.model.arrangement.DomainArrangement;

public class RADSResultsTableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int RADS_MODE = 0;
	public static final int RAMPAGE_MODE = 1;
	
	private int mode = RADS_MODE;
	
	private static final Map<Integer, String[]> HEADERS = new HashMap<Integer, String[]>();
	private static final String[] radsColumnNames = {"", "Import", "Subject ID", "RADS Score", "Arrangement String"};
	private static final String[] rampageColumnNames = {"", "Import", "Subject ID", "RADS Score", "RAMPAGE Score", "Arrangement String"};
	
	static {
		HEADERS.put(RADS_MODE, radsColumnNames);
		HEADERS.put(RAMPAGE_MODE, rampageColumnNames);
	}
	
	private Object[][] tableData = null;
	private HashMap<String, DomainArrangement> arrangementData = null;
	
	
	public String[] getColumnNames() {
		return HEADERS.get(mode);
	}
	
	public void setTableMode(int scanMode) {
		this.mode = scanMode;
	}
	
	public int getTableMode() {
		return mode;
	}
	
//	public void setColumnNames(String [] names) {
//		this.columnNames = names;
//	}
	
    public String getColumnName(int col) {
    	return HEADERS.get(mode)[col].toString();
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
		return HEADERS.get(mode).length;
	}

	public Object getValueAt(int row, int col) {
		return tableData[row][col];
	}
	
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public boolean isCellEditable(int row, int col) {
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
