package domosaics.ui.workspace.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;




/**
 * Workspace action which shows a view within the main frame.
 * 
 * @author Andreas Held
 *
 */
public class ShowViewAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ShowViewAction (){
		super();
		putValue(Action.NAME, "Show");
		putValue(Action.SHORT_DESCRIPTION, "Shows the view within the mainframe");
	}

	public void actionPerformed(ActionEvent e) {
		// get selected Workspace element
		WorkspaceElement selected = WorkspaceManager.getInstance().getSelectionManager().getSelectedElement();
		
		// if its not a view cancel this action
		if (selected == null || !selected.isView()) {
			MessageUtil.showWarning("Please select a view element");
			return;
		}
		
		// else show it
		WorkspaceManager.getInstance().showViewInMainFrame((ViewElement) selected);
	}

}
