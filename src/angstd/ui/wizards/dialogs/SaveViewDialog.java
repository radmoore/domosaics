package angstd.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.io.File;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.view.View;
import angstd.ui.wizards.pages.ChooseViewToSavePage;
import angstd.ui.wizards.pages.SaveProjectFilePage;

/**
 * Wizard dialog asking the user for the project he wants to export as well as
 * for the destination file.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * 
 */
public class SaveViewDialog {

	public static Object show(WorkspaceElement view) {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new ChooseViewToSavePage(view), new SaveProjectFilePage()}, new SaveViewProgress());
		return WizardDisplayer.showWizard(wiz);
	}
}

/**
 * ResultProducer processing the wizards information and exporting the selected
 * project.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * 
 */
class SaveViewProgress extends DeferredWizardResult implements WizardResultProducer {

	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();

		p.setBusy("Saving view");
		try {
			// get selected project and destination file
			ViewElement viewElem = (ViewElement) m.get(ChooseViewToSavePage.VIEW_KEY);
			String exportName = (String) m.get(ChooseViewToSavePage.EXPORT_NAME);
			File fileLocation = new File((String) m.get(SaveProjectFilePage.FILE_KEY));

			// just double checking
			if (!fileLocation.canWrite())
				MessageUtil.showWarning("No permission to write in "+fileLocation.getName());
			
			// use exportName and location to create file
			File exportFile = new File(fileLocation.getAbsolutePath()+"/"+exportName);
			
			if (exportFile.exists())
				if (!MessageUtil.showDialog(exportFile.getName()+" exists. Overwrite?"))
					return;

			
			// write project file
			ViewHandler.getInstance().getView(viewElem.getViewInfo()).export(exportFile);
			p.finished(null);
			return;
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			Configuration.getLogger().debug(e.toString());
			p.failed("Error while saving project", false);
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
