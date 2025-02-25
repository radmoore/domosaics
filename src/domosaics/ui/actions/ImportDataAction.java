package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.importdata.ImportDataResultProducer;




/**
 * ImportDataAction opens a wizard to import data like sequences, 
 * trees and domain arrangements. The informations given by the
 * user using the wizard are processed in {@link ImportDataResultProducer}.
 * In there also the new view is created.
 * 
 * 
 * @author Andreas Held
 *
 */
public class ImportDataAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	@Override
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startImportDataWizard();
	}
}	

