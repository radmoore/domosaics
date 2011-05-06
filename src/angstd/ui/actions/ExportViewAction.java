package angstd.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.FileDialogs;
import angstd.ui.views.view.View;
import angstd.ui.wizards.WizardManager;

public class ExportViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
		    
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startSaveViewWizard();
	}

}
