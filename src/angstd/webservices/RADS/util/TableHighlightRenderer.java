package angstd.webservices.RADS.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class TableHighlightRenderer extends DefaultTableCellRenderer{
	
	private final Color SELECTION_COLOR = Color.GREEN;
	private final Color DEFAULT_COLOR = UIManager.getColor("Table.selectionBackground");

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if((Boolean)table.getValueAt(row, column)) {
			
		}
		
		return null;
	}
	
}
