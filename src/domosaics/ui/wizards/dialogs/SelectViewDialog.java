package domosaics.ui.wizards.dialogs;

import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.wizards.pages.SelectViewPage;




/**
 * Wizard dialog allowing user to choose a domain-view
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SelectViewDialog {

	protected ProjectElement project; 
	protected int selectedElements;
	
	/**
	 * Constructor for a new SelectViewDialog
	 * 
	 */
	public SelectViewDialog(ProjectElement project, int selectedElements) {
		this.selectedElements = selectedElements;
		this.project = project;
	}
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the chosen name
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new SelectViewPage(project, selectedElements)}, new SelectViewProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class SelectViewProgress implements WizardResultProducer{
	
	@Override
	public boolean cancel(Map m) {
		return true;
	}
	
	@Override
	public Object finish(Map m) throws WizardException {
		return m;
	}	
}


