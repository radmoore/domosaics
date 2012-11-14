package angstd.webservices.RADS.util;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * This class is an implementation of JTable. 
 * It overrides {@link JTable#prepareRenderer(TableCellRenderer, int, int)},
 * which is used to query the data model for state of cell selection
 * and value of cell. This class also sets the cell toolTips
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResultsTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new table using a table model
	 * (which should be an instance of #{@link RADSResultsTableModel})
	 * 
	 * @param model - the data model for this table
	 */
	public RADSResultsTable(TableModel model) {
		super(model);
		setColumnWidth();
	}
	
	/**
	 * Overrides #{@link JTable#prepareRenderer(TableCellRenderer, int, int)}
	 */
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        Object value = getValueAt(row, column);
        
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            if (column == 1) {
            	boolean cValue = (Boolean) value; 
            	if (cValue)
            		jc.setToolTipText("Deselect hit");
            	else
            		jc.setToolTipText("Select hit for import");
            }
            else if (column == 2) {
            	jc.setToolTipText("Subject ID: "+value);
            }
            else if (column == 3) {
            	jc.setToolTipText("RADS Score: "+value);
            }
            else if ((column  == 4) && (this.getColumnCount() == 6)) {
            	jc.setToolTipText("RAMPAGE Score: "+value);
            }
            else if (column+1 == this.getColumnCount()) {
            	jc.setToolTipText("Subject Arrangement String: "+value);
            }
            else {
            	jc.setToolTipText("");
            }
        }
        return c;
	 }
	
	/*
	 * Sets the column width in this JTable
	 * (which can be different, depending on scan mode)
	 */
	private void setColumnWidth() { 
		for (int i=0; i < this.getColumnCount(); i++) {
			TableColumn currentColumn = this.getColumnModel().getColumn(i);
			Object value = currentColumn.getHeaderValue();
			TableCellRenderer renderer = currentColumn.getHeaderRenderer();
			if (renderer == null) {
				renderer = this.getTableHeader().getDefaultRenderer();
			}
			Component c = renderer.getTableCellRendererComponent(this, value, false, false, -1, i);
			currentColumn.setPreferredWidth(c.getPreferredSize().width);
		}
	}
	
}
