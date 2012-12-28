package angstd.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.treeview.TreeViewI;



/**
 * Action used to toggle the UseDistances flag for edges.
 * Depending on the flags state constant distance or the edge weights are 
 * used for the edges length.
 * 
 * @author Andreas Held
 *
 */
public class UseDistancesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().structuralChange();
	}
}
