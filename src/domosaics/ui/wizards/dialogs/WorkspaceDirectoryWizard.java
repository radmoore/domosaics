package domosaics.ui.wizards.dialogs;

import java.awt.Component;
import java.io.File;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.ui.wizards.pages.WorkspaceDirectoryPage;




public class WorkspaceDirectoryWizard {
	
	protected String defaultDir;
	protected Component parent;
	
	public WorkspaceDirectoryWizard (Component parent, String defaultDir) {
		this.defaultDir = defaultDir;
		this.parent = parent;
	}
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the created project element
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new WorkspaceDirectoryPage(parent, defaultDir)}, new WorkspaceDirectoryProgress());
		return WizardDisplayer.showWizard(wiz);	
	}
}

class WorkspaceDirectoryProgress implements WizardResultProducer{
	
	@Override
	public boolean cancel(Map settings) {
		return true;
	}

	@Override
	public Object finish(Map wizardData) throws WizardException {
		File res = new File((String) wizardData.get(WorkspaceDirectoryPage.FILE_KEY));
		return res;
	}
	
}

