package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;




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

	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().structuralChange();
	}
}
