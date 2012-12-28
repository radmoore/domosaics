package domosaics.ui.wizards.dialogs;

import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.wizards.pages.SelectNamePage;


/**
 * Wizard dialog asking the user for a name to assign to a specified 
 * object. This object can be a project within the workspace or a view.
 * 
 * @author Andreas Held
 *
 */
public class SelectNameDialog {

	/** the default name */
	protected String defaultName = null;
	
	/** the object to name */
	protected String objectName = null;
	
	protected ProjectElement project;
	
	protected boolean allowProjectSelection; 
	
	/**
	 * Constructor for a new SelectViewNameDialog
	 * 
	 * @param defaultName
	 * 		the default name
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public SelectNameDialog(String defaultName, String objectName, ProjectElement project, boolean allowProjectSelection) {
		this.defaultName = defaultName;
		this.objectName = objectName;
		this.project = project;
		this.allowProjectSelection = allowProjectSelection;
	}
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the chosen name
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new SelectNamePage(defaultName, objectName, project, allowProjectSelection)}, new SelectViewNameProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class SelectViewNameProgress implements WizardResultProducer{
	
	public boolean cancel(Map m) {
		return true;
	}
	
	public Object finish(Map m) throws WizardException {
		return m;
	}	
}


