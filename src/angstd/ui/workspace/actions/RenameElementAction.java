package angstd.ui.workspace.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.view.View;
import angstd.ui.wizards.WizardManager;



/**
 * Workspace action which triggers a rename event on a workspace element.
 * 
 * @author Andreas Held
 *
 */
public class RenameElementAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public RenameElementAction (){
		super();
		putValue(Action.NAME, "Rename");
		putValue(Action.SHORT_DESCRIPTION, "Renames the selected component");
	}

	public void actionPerformed(ActionEvent e) {
		// get selected Workspace element
		WorkspaceElement selected = WorkspaceManager.getInstance().getSelectionManager().getSelectedElement();

		if (selected.isCategory()) {
			MessageUtil.showWarning("Categories cannot be renamed");
			return;
		}
		
		// let the user choose the new name
		String newTitle = WizardManager.getInstance().renameWizard(selected.getTitle(), selected.getTypeName(), selected);
		
		if(newTitle == null)  // canceled
			return;
		
		// change the name then
		WorkspaceManager.getInstance().changeElementName(selected, newTitle);
		
		// check if it is a view
		if (selected.isView()) {
			// therefore the viewinfo has to be updated as well (also the panels name)
			View view = ViewHandler.getInstance().getView(((ViewElement) selected).getViewInfo());
			view.getViewInfo().setName(newTitle);
			view.getParentPane().setName(newTitle);
			
			// if it is also the active view within the AngstdDektop the dockable name has to be updated as well
			View activeView = ViewHandler.getInstance().getActiveView();
			if ( (activeView != null) && (ViewHandler.getInstance().getActiveView().equals(view)) )
				AngstdUI.getInstance().changeViewName(newTitle);
		}
	}

}