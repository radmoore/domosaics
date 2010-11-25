package angstd.ui.actions;

import java.awt.event.ActionEvent;

import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.wizards.WizardManager;

/**
 * CreateDomainTreeAction opens the wizard for creating domain trees
 * 
 * @author Andreas Held
 *
 */
public class CreateDomainTreeAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startCreateDomTreeWizard();
	}

}
