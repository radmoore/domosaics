package angstd.webservices.RADS.util;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class RADSResultsTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RADSResultsTable(TableModel model) {
		super(model);
	}
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
       
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            if (column == 1) {
            	boolean cValue = (Boolean) getValueAt(row, column);
            	if (cValue)
            		jc.setToolTipText("Deselect hit");
            	else
            		jc.setToolTipText("Select hit for import");
            }
        }
        return c;
	 }
	
}
