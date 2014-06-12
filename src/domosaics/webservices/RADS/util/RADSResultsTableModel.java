package domosaics.webservices.RADS.util;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import domosaics.model.arrangement.DomainArrangement;




/**
 * RADSResultsTableModel is the back-end data model for the
 * RADSResultsTable, which displays the results of RADS/RAMPAGE
 * scans. It extends AbstractTableModel.
 * The table headers and number of columns used is set
 * dynamically, depending on whether a RADS or a RADS/RAMPAGE
 * scan was conducted. Results are sorted by best score;
 * the sorting is defined in the radscan lib. 
 *  
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResultsTableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int RADS_MODE = 0;
	public static final int RAMPAGE_MODE = 1;
	private int mode = RADS_MODE;
	private static final Map<Integer, String[]> HEADERS = new HashMap<Integer, String[]>();
	private static final String[] radsColumnNames = {"", "Import", "Subject ID", "RADS", "Subject Arrangement String"};
	private static final String[] rampageColumnNames = {"", "Import", "Subject ID", "RADS", "RAMPAGE", "Subject Arrangement String"};
	private Object[][] tableData = null;
	private HashMap<String, DomainArrangement> arrangementData = null;
	
	// used to switch between the supported scan modes
	static {
		HEADERS.put(RADS_MODE, radsColumnNames);
		HEADERS.put(RAMPAGE_MODE, rampageColumnNames);
	}
	
	/**
	 * Get all column names from this table
	 * @return an array containg column names
	 */
	public String[] getColumnNames() {
		return HEADERS.get(mode);
	}
	
	/**
	 * Used to set the scan mode (RADS or RAMPAGE).
	 * The number of table columns and their respective headers
	 * are set according to the value of scanMode.
	 * 
	 * @param scanMode - An integer representing the scan mode
	 */
	public void setTableMode(int scanMode) {
		this.mode = scanMode;
	}
	
	/**
	 * Retreive the scan mode for the results table model
	 * 
	 * @return - An integer representing the used scan mode
	 */
	public int getTableMode() {
		return mode;
	}
	
	/**
	 * see {@link AbstractTableModel#getColumnName(int)}
	 */
    @Override
	public String getColumnName(int col) {
    	return HEADERS.get(mode)[col].toString();
    }
	
    /**
     * Returns the table data for this model
     * 
     * @return - the table data
     */
	public Object[][] getTableData() {
		return this.tableData;
	}

	/**
	 * Set the data for this table model
	 * 
	 * @param tableData
	 */
	public void setTableData(Object[][] tableData) {
		this.tableData = tableData;
	}
	
	/**
	 * see {@link AbstractTableModel#getRowCount()}
	 */
	@Override
	public int getRowCount() {
		return tableData.length;
	}

	/**
	 * see {@link AbstractTableModel#getColumnCount()}
	 */
	@Override
	public int getColumnCount() {
		return HEADERS.get(mode).length;
	}

	/**
	 * see {@link AbstractTableModel#getValueAt(int, int)}
	 */
	@Override
	public Object getValueAt(int row, int col) {
		return tableData[row][col];
	}
	
	/**
	 * see {@link TableModel#getColumnClass(int)}
	 */
    @Override
	public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    /**
     * see {@link TableModel#isCellEditable(int, int)}
     */
    @Override
	public boolean isCellEditable(int row, int col) {
    	//return col == 1;
    	return false;
    }
    
    /**
     * see {@link TableModel#setValueAt(Object, int, int)}
     */
    @Override
	public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
    /**
     * setArrangementData is used to manage the DomainArrangements which are
     * described in each row of the table described by this model
     * 
     * @param arrData - Hit ID to arrangement data mapping
     */
    public void setArrangementData(HashMap<String, DomainArrangement> arrData) {
    	this.arrangementData = arrData;
    }
    
    /**
     * get the arrangement data represented in this table
     * 
     * @return - Hit ID to arrangement data mapping
     */
    public HashMap<String, DomainArrangement> getArrangementData() {
    	return this.arrangementData;
    }
	
}
