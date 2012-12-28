package angstd.ui.wizards.dialogs;

import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.wizards.pages.SelectRenamePage;



/**
 * Wizard dialog asking the user for a name to assign to a specified 
 * object. This object can be a project within the workspace or a view.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SelectRenameDialog {

	/** the default name */
	protected String defaultName = null;
	
	/** the object to name */
	protected String objectName = null;
	
	protected WorkspaceElement elem;
	
	/**
	 * Constructor for a new SelectViewNameDialog
	 * 
	 * @param defaultName
	 * 		the default name
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public SelectRenameDialog(String defaultName, String objectName, WorkspaceElement elem) {
		this.defaultName = defaultName;
		this.objectName = objectName;
		this.elem = elem;
	}
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the chosen name
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new SelectRenamePage(defaultName, objectName, elem)}, new SelectViewRenameProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class SelectViewRenameProgress implements WizardResultProducer{
	
	public boolean cancel(Map m) {
		return true;
	}
	
	public Object finish(Map m) throws WizardException {
		return m.get(SelectRenamePage.ELEMENT_KEY);
	}	
}


