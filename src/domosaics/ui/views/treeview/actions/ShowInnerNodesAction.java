package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponentDetector;




/**
 * Action used to show inner nodes as circles if no label is set for them.
 * <p>
 * This comes handy if the user wants to manipulate the tree, because the 
 * {@link NodeComponentDetector} uses the circular shape to identify whether
 * or not the user clicked on a inner node.
 * 
 * @author Andreas Held
 *
 */
public class ShowInnerNodesAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	    
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().visualChange();
	}

}
