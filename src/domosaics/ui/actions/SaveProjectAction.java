package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.wizards.WizardManager;


/**
 * Action which allows the user to export a project
 * 
 * @author Andreas Held
 *
 */
public class SaveProjectAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
		    
	public void actionPerformed(ActionEvent e) {
		//ProjectElement is null, as nothing is selected
		WizardManager.getInstance().startSaveProjectWizard(null);
	}
}
