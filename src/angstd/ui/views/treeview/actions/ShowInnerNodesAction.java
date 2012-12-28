package angstd.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponentDetector;



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
	    
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().visualChange();
	}

}
