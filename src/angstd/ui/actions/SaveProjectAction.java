package angstd.ui.actions;

import java.awt.event.ActionEvent;

import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.wizards.WizardManager;

/**
 * Action which allows the user to export a project
 * 
 * @author Andreas Held
 *
 */
public class SaveProjectAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
		    
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startSaveProjectWizard();
	}
}
