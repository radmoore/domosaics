package domosaics.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.io.File;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domaintreeview.DomainTreeView;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.treeview.TreeView;
import domosaics.ui.views.view.io.ViewImporter;
import domosaics.ui.wizards.pages.ImportViewPage;
import domosaics.ui.wizards.pages.LoadFastaPage;




/**
 * Wizard dialog asking the user for a name to assign to a specified 
 * object. This object can be a project within the workspace or a view.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class LoadFastaDialog {

	/** the default name */
	protected String defaultName = null;
	
	/** the object to name */
	protected String objectName = null;
	
	protected boolean allowProjectSelection; 
	
	/**
	 * Constructor for a new SelectViewNameDialog
	 * 
	 * @param defaultName
	 * 		the default name
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public LoadFastaDialog() {
	}
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the chosen name
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new LoadFastaPage()}, new LoadFastaProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class LoadFastaProgress implements WizardResultProducer{
	
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
	}
	
	public boolean cancel(Map m) {
		return true;
	}
	
	public Object finish(Map m) throws WizardException {

		// display view after import
		//ViewImporter.displayViewAfterImport(true);
		
		
		// create a new view
		try {
			
			String projectName = (String) m.get(ImportViewPage.PROJECTNAME_KEY);
			String viewFilePath = (String) m.get(ImportViewPage.FILEPATH_KEY);
			String viewName = (String) m.get(ImportViewPage.VIEWNAME_KEY);

			//System.out.println("Importing viewtype: "+viewType);
			
			ProjectElement project = WorkspaceManager.getInstance().getProject(projectName);
			File viewFile = new File(viewFilePath);
			
			SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, viewName);
			
			// Adapted by Nico
			SequenceI[] seqs = new FastaReader().getDataFromFile(viewFile);
			if(seqs!=null)
			{
				seqView.setSeqs(seqs);
				ViewHandler.getInstance().addView(seqView, project, false);
				//ViewImporter.readSequenceView(viewFile, project, viewName);
			}
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
			e.printStackTrace();
		}
		
		// set back to default
		ViewImporter.displayViewAfterImport(false);
		
		return null;
	}	
}


