package domosaics.ui.workspace.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.wizards.WizardManager;




/**
 * Creates a new project
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ExportProjectAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	ProjectElement project = null;
	
	public ExportProjectAction (ProjectElement project){
		super();
		this.project = project;
		putValue(Action.NAME, "Export project");
		putValue(Action.SHORT_DESCRIPTION, "Exports selected project");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startSaveProjectWizard(project);
	}

}
