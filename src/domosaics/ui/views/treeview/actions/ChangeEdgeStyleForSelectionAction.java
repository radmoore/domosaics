package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.EdgeStyleChooser;




/**
 * Action allowing the user to choose a new edge style for the selected 
 * edges.
 * 
 * @author Andreas Held
 *
 */
public class ChangeEdgeStyleForSelectionAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
    
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		if (view.getTreeSelectionManager().getSelection().isEmpty()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please select edges first");
			return;
		}
		
		// open change edge style dialog
		EdgeStyleChooser esc = new EdgeStyleChooser(view);
		esc.showDialog(DoMosaicsUI.getInstance(), "DoMosaics Edge Style Chooser");
	}

}
