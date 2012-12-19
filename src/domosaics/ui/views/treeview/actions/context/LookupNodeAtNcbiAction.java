package domosaics.ui.views.treeview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.configuration.Configuration;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.util.BrowserLauncher;


/**
 * Action opening a browser window and performing a NCBI search
 * using the nodes label
 * 
 * @author Andreas Held
 *
 */
public class LookupNodeAtNcbiAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupNodeAtNcbiAction () {
		super();
		putValue(Action.NAME, "Lookup At NCBI");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browserwindow showing the NCBI homepage");
	}
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		// get the nodes label and if its null init it as empty string
		String label = selectedNode.getLabel();
		if(label == null)
			label = "";
		
		BrowserLauncher.openURL(Configuration.getInstance().getNcbiUrl(label)); //"http://www.ncbi.nlm.nih.gov/sites/gquery?term="+label);
		
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null);
	}
}