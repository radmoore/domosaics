package angstd.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import angstd.model.workspace.ProjectElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.wizards.WizardManager;



public class ImportViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	private ProjectElement project = null;
	
	public ImportViewAction (){
		super();
		putValue(Action.NAME, "Import view");
		putValue(Action.SHORT_DESCRIPTION, "Imports a view into current project");
	}
	
	public void setProject(ProjectElement project) {
		this.project = project;
	}
	
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startImportViewWizard(project);
	}

}
