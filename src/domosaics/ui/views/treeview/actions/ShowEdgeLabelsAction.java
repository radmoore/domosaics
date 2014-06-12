package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * Action used to toggle the draw edge labels flag for a treeView.
 * 
 * @author Andreas Held
 *
 */
public class ShowEdgeLabelsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().toggleDrawEdgeWeights();
	}
	
}