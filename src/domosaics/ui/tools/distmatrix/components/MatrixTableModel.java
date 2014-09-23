package domosaics.ui.tools.distmatrix.components;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * This class is based on the MatrixTableModel implemented
 * by Thasso Griebel and used for the EPos project.
 * <p>
 * This class is used to control the tables data.
 * 
 * @author Thasso Griebel (edited by Andreas Held)
 *
 */
public class MatrixTableModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;
	
	/**
	 *  Constructor for a new MatrixTableModel 
	 */
	public MatrixTableModel(){
		super();
	}
	     
	/**
	 * Constructor for a new MatrixTableModel setting the data and 
	 * column names for the table.
	 * 
	 * @param data
	 * 		field values
	 * @param colNames
	 * 		column names
	 */
	public MatrixTableModel(String[][] data, String[] colNames){
		this();
	    updateData(data, colNames);
	}
	         
	/**
	 * ensures that the user cant edit a cell
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * Method used to set the data for the table
	 * 
	 * @param data
	 * 		field values
	 * @param colNames
	 * 		column names
	 */
	@SuppressWarnings("unchecked")
	public void updateData(String[][] data, String[] colNames) {
		String[][] mat = data;    
	    String[] titles = colNames;            
	        
	   if(dataVector == null)
		   dataVector = new Vector();
	   else
	       dataVector.removeAllElements();
	        
	   if(columnIdentifiers == null)
		   columnIdentifiers = new Vector();
	   else
	       columnIdentifiers.removeAllElements();
	        
	   for (int i = 0; i < mat.length; i++) {		
		   dataVector.add(i, new Vector());
		   columnIdentifiers.add(i, titles[i]);
		   ((Vector) dataVector.get(i)).add(titles[i]);
				
		   for (int j = 0; j < mat[i].length; j++) 			
			   ((Vector) dataVector.get(i)).add( mat[i][j]);
				
			for (int m = i+1; m < mat.length; m++) 
				((Vector) dataVector.get(i)).add(mat[m][i]);				
		}		
		columnIdentifiers.add(0, "");		
		fireTableDataChanged();
		fireTableStructureChanged();
	}
}
