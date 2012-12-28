package angstd.ui.wizards;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import angstd.model.DataType;
import angstd.model.workspace.WorkspaceElement;



/**
 * Class to render entries within a JComponent such as JComboBox. 
 * Its just ensured to draw icons (if present) next to their title.
 * 
 * @author Andreas Held
 *
 */
public class WizardListCellRenderer extends DefaultListCellRenderer {	
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if(value instanceof WorkspaceElement){			
			setIcon(((WorkspaceElement)value).getIcon());
			value = ((WorkspaceElement)value).getTitle();
		} else if(value instanceof DataType) {
			setIcon(((DataType)value).getIcon());
			value = ((DataType)value).getTitle();
		}
		if(value != null)
			setText(value.toString());
		return this;
	}
}
