package domosaics.ui.workspace.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.wizards.WizardManager;


/**
 * Creates a new project
 * 
 * @author Andreas Held
 *
 */
public class CreateProjectAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public CreateProjectAction (){
		super();
		putValue(Action.NAME, "Create Project");
		putValue(Action.SHORT_DESCRIPTION, "Creates a new project");
	}

	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().showCreateProjectWizard(null);
	}

}
