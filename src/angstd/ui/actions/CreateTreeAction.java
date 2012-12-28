package angstd.ui.actions;

import java.awt.event.ActionEvent;

import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.createtree.CreateTreeResultProducer;



/**
 * CreateTreeAction opens a wizard to create a tree based on sequences or domains. 
 * The informations given by the user using the wizard are processed in 
 * {@link CreateTreeResultProducer}. In there also the new view is created.
 * 
 * @author Andreas Held
 *
 */
public class CreateTreeAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent event) {
		WizardManager.getInstance().startCreateTreeWizard();
	}
}
