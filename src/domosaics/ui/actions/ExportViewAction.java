package domosaics.ui.actions;

import java.awt.event.ActionEvent;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.wizards.WizardManager;




public class ExportViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
		    
	@Override
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startSaveViewWizard(null);
	}

}
