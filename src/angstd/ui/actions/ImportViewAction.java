package angstd.ui.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.ProjectElement;
import angstd.ui.AngstdUI;
import angstd.ui.WorkspaceManager;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.view.io.ViewImporter;
import angstd.ui.wizards.WizardManager;

public class ImportViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
    
	public void actionPerformed(ActionEvent e) {
		WizardManager.getInstance().startImportViewWizard(null);
	}

}
