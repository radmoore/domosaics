package angstd.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.treeview.TreeViewI;

/**
 * Action used to align the leaves of a tree at the right edge of the view.
 * 
 * @author Andreas Held
 *
 */
public class ExpandLeavesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().structuralChange();
	}
	
}