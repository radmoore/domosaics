package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.EdgeStyleChooser;




/**
 * Action to change the default edge stroke.
 * 
 * @author Andreas Held
 *
 */
public class ChangeDefaultEdgeStyleAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
    
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		// clear selection so that all edges will be altered
		view.getTreeSelectionManager().clearSelection();
		view.getParentPane().repaint();
		
		// open change edge style dialog
		EdgeStyleChooser esc = new EdgeStyleChooser(view);
		esc.showDialog(DoMosaicsUI.getInstance(), "DoMosaicS Edge Style Chooser");
	}

}
