package angstd.ui.workspace.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.view.View;
import angstd.ui.wizards.WizardManager;



public class ExportViewAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ExportViewAction (){
		super();
		putValue(Action.NAME, "Export view");
		putValue(Action.SHORT_DESCRIPTION, "Exports a view to a file");
	}

	public void actionPerformed(ActionEvent e) {
		
		WorkspaceElement selected = WorkspaceManager.getInstance().getSelectionManager().getSelectedElement();
		
		if (! selected.isView())
			selected = null;
		
		WizardManager.getInstance().startSaveViewWizard(selected);
		
//		// get selected Workspace element
//		WorkspaceElement selected = WorkspaceManager.getInstance().getSelectionManager().getSelectedElement();
//		
//		if (selected == null || !selected.isView()) {
//			MessageUtil.showWarning("Please select a view element first");
//			return;
//		}
//		
//		File file = FileDialogs.showSaveDialog(AngstdUI.getInstance(), "view");
//		if (file == null)
//			return;
//		
//		View view = ViewHandler.getInstance().getView(((ViewElement) selected).getViewInfo());
//		view.export(file);
	}

}
