package domosaics.ui.workspace.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.view.View;
import domosaics.ui.wizards.WizardManager;




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
//		File file = FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), "view");
//		if (file == null)
//			return;
//		
//		View view = ViewHandler.getInstance().getView(((ViewElement) selected).getViewInfo());
//		view.export(file);
	}

}
