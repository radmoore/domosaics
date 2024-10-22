package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.wizards.WizardManager;




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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startImportViewWizard(project);
	}

}
