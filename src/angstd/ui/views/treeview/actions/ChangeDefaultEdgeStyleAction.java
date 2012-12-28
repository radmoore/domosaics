package angstd.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.EdgeStyleChooser;



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
		esc.showDialog(AngstdUI.getInstance(), "Angstd Edge Style Chooser");
	}

}
