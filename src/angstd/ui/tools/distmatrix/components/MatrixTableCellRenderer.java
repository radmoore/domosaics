package angstd.ui.tools.distmatrix.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This class is based on the MatrixTableCellRenderer implemented
 * by Thasso Griebel and used for the EPos project.
 * <p>
 * This class renders a cell within the table using the correct 
 * colorization, e.g. for selected cells. It also sets the tooltip text
 * for cells.
 * 
 * @author Thasso Griebel (edited by Andreas Held)
 *
 */
public class MatrixTableCellRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;
		
	/** the selected column */
	protected int selected_column = -1;
	
	/** the selected row */
	protected int selected_row = -1;
		
	/** the color to mark the row, column to the selected cell */
	protected final static Color C_MARK = 	new Color(84,255,159, 100);
	
	/** the color to mark the selected cell */
	protected final static Color C_SELECTED = new Color(0, 255,  0, 180);
		
	
	/**
	 * Constructor for a new MatrixTableCellRenderer 
	 */
	public MatrixTableCellRenderer(){
		super();
	}
		
	/**
	 * Sets the selected cell by specifying its row and column
	 * 
	 * @param c
	 * 		the column of the selected cell
	 * @param r
	 * 		the row of the selected cell
	 */
	public void setSelection(int c, int r) {
		selected_column = c;
	    selected_row = r;        
	}
	    
	/** 
	 * Renders a cell within the table 
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		    	     
		if(column == 0){			
			JLabel c = (JLabel) table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setHorizontalAlignment(SwingConstants.CENTER);
			return c;
		}else{
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(c != null && value != null){
					    
				if(selected_column >=0 && selected_row >= 0){
						    
					/// mark selected row
					if(row == selected_row && column == selected_column){
						c.setBackground(C_SELECTED);
					}else if(row == selected_row && column <= selected_column){
						c.setBackground(C_MARK);
					}else if(column == selected_column && row <= selected_row){
						c.setBackground(C_MARK);
					}else{
						c.setBackground(new Color(255,255,255,255));
					}
				}else{
						c.setBackground(new Color(255,255,255,255));
				}
			}
            String s1 = table.getModel().getValueAt(row, 0).toString();
            String s2 = table.getModel().getValueAt(column-1, 0).toString();
            String d =  table.getModel().getValueAt(row, column).toString();
            ((JComponent)c).setToolTipText("<html>"+s1+  " vs.<br>"+ s2 +"<br><center>="+d+"</center></html>");
            ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
   
			return c;
		}
	} 		
}
