package angstd.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.workspace.ProjectElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.wizards.pages.CreateProjectPage;

/**
 * Wizard dialog asking the user for a project name which is 
 * then going to be created and added to the workspace.
 * 
 * @author Andreas Held
 *
 */
public class CreateProjectDialog { 
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the created project element
	 */
	public static Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new CreateProjectPage()}, new ProjectProgress());
		return WizardDisplayer.showWizard(wiz);	
	}
}

/**
 * ResultProducer processing the wizards information and creating a new 
 * project which is added to the workspace.
 * 
 * @author Andreas Held
 *
 */
@SuppressWarnings("unchecked")
class ProjectProgress extends DeferredWizardResult implements WizardResultProducer{
	
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		// create a new project
		try{
			p.setBusy("Creating Project");
			String name = (String) m.get(CreateProjectPage.ELEMENT_KEY);
			
			ProjectElement project = new ProjectElement(name.trim());
			WorkspaceManager.getInstance().addProject(project, true);
			p.finished(project);
		}catch(Exception e){
			p.failed("Error while creating Project, please try again.", false);
			p.finished(null);
		}	
	}
	
	public boolean cancel(Map m) {
		return true;
	}
	
	public Object finish(Map m) throws WizardException {
		return this;
	}	
}
