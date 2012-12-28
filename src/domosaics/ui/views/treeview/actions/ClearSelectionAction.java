package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;


/**
 * Action to deselect all selected nodes
 * 
 * @author Andreas Held
 *
 */
public class ClearSelectionAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();
		
		// use selection manager to deselect all nodes
		view.getTreeSelectionManager().setSelection(null);
	}
}
