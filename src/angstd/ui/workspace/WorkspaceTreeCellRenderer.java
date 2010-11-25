package angstd.ui.workspace;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import angstd.model.workspace.WorkspaceElement;

/**
 * Class to render the workspace tree entries. Its just ensured to draw the
 * workspace elements icon next to its title.
 * 
 * @author Andreas Held
 *
 */
public class WorkspaceTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Ensure the drawing of the workspace elements icon next to its title.
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if(value instanceof WorkspaceElement){			
			setText(((WorkspaceElement)value).getTitle());
			setIcon(((WorkspaceElement)value).getIcon());
		}
		
		return this;
	}

	
}
