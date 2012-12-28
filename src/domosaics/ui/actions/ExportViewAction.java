package domosaics.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.views.view.View;
import domosaics.ui.wizards.WizardManager;




public class ExportViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
		    
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startSaveViewWizard(null);
	}

}
