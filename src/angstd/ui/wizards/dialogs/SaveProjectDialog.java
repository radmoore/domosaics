package angstd.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.File;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.io.ProjectExporter;
import angstd.ui.wizards.pages.ChooseProjectToSavePage;
import angstd.ui.wizards.pages.SaveProjectFilePage;

/**
 * Wizard dialog asking the user for the project he wants
 * to export as well as for the destination file.
 * 
 * @author Andreas Held
 *
 */
public class SaveProjectDialog {

	public static Object show() {
//		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new ChooseProjectToSavePage()}, new SaveProjectProgress());

		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new ChooseProjectToSavePage(), new SaveProjectFilePage()}, new SaveProjectProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

/**
 * ResultProducer processing the wizards information and exporting the 
 * selected project.
 * 
 * @author Andreas Held
 *
 */
@SuppressWarnings("unchecked")
class SaveProjectProgress extends DeferredWizardResult implements WizardResultProducer{
	
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		p.setBusy("Saving Project");
		
		try{
			// get selected project and destination file
			ProjectElement project = (ProjectElement) m.get(ChooseProjectToSavePage.PROJECT_KEY);
			File file = new File((String) m.get(SaveProjectFilePage.FILE_KEY));
		
			//write project file
			ProjectExporter.write(file, project);
		
			p.finished(null);
			return;
		}catch(Exception e){
			p.failed("Error while saving project, please try again.", false);
		}

		p.finished(null);
	}
	
	public boolean cancel(Map m) {
		return true;
	}

	public Object finish(Map m) throws WizardException {
		return this;
	}	

}
