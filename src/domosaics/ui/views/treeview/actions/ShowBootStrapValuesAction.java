package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import domosaics.model.tree.TreeNodeI;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * Action which displays the bootstrap values instead of the edge weights.
 * 
 * @author Andreas Held
 *
 */
public class ShowBootStrapValuesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		
		if (!view.getTreeLayoutManager().isTreatLabelAsBootstrap()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please mark use \"label as bootstrap value\" first");
			setState(!getState());
			return;
		}
		
		// check if there are bootstrapvalues assigned
		boolean bootstrapOK = false;
		Iterator<TreeNodeI> iter = view.getTree().getNodeIterator();
		while (iter.hasNext()) 
			if(iter.next().getBootstrap() != -1) {
				bootstrapOK = true;
				break;
			}
		
		if (!bootstrapOK) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No bootstrap values assigned as labels within the Newick format");
			setState(!getState());
			return;
		}
		
		view.getTreeLayoutManager().toggleShowBootstrap();
	}
}