package domosaics.ui.views.treeview.actions.notUsed;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.EdgeStyleChooser;


/**
 * Action allowing the user to choose a new edge style for the selected edges.
 * 
 * @author Andreas Held
 *
 */
public class ChangeEdgeStyleAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ChangeEdgeStyleAction (){
		super();
		putValue(Action.NAME, "Change Edge Style");
		putValue(Action.SHORT_DESCRIPTION, "Changes the edge style of branches");
	}

	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		// open change edge style dialog
		EdgeStyleChooser esc = new EdgeStyleChooser(view);
		esc.showDialog(DoMosaicsUI.getInstance(), "Angstd Edge Style Chooser");
	}
}