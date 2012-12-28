package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * Action which tells the program to use numeric labels as bootstrap values.
 * To actually show the labels the action AhowBootstrapValues has to be
 * triggered as well.
 * 
 * @author Andreas Held
 *
 */
public class UseLabelAsBootstrapAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();

		// delete the numeric labels from the nodes
		view.getTreeComponentManager().useLabelAsBootstrap(getState());
		
		// show the result
		view.getTreeLayoutManager().visualChange();
	}
}