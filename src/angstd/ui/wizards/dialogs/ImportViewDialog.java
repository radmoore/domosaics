package angstd.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.io.File;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.ProjectElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.ViewType;
import angstd.ui.views.domaintreeview.DomainTreeView;
import angstd.ui.views.domainview.DomainView;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.views.treeview.TreeView;
import angstd.ui.views.view.io.ViewImporter;
import angstd.ui.wizards.pages.ImportViewPage;



/**
 * Wizard dialog asking the user for a name to assign to a specified 
 * object. This object can be a project within the workspace or a view.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ImportViewDialog {

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
	public ImportViewDialog(ProjectElement project) {
		this.project = project;
	}
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the chosen name
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new ImportViewPage(project)}, new ViewImportProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class ViewImportProgress implements WizardResultProducer{
	
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
			
			String viewType = (String) m.get(ImportViewPage.VIEWTYPE_KEY);
			String projectName = (String) m.get(ImportViewPage.PROJECTNAME_KEY);
			String viewFilePath = (String) m.get(ImportViewPage.FILEPATH_KEY);
			String viewName = (String) m.get(ImportViewPage.VIEWNAME_KEY);

			System.out.println("Importing viewtype: "+viewType);
			
			ProjectElement project = WorkspaceManager.getInstance().getProject(projectName);
			File viewFile = new File(viewFilePath);
			
			if ( viewType.equals("SEQUENCE") ) {
				SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, viewName);
				seqView.importXML(viewFile);
				ViewHandler.getInstance().addView(seqView, project, false);
				//ViewImporter.readSequenceView(viewFile, project, viewName);
			}
			
			else if ( viewType.equals("DOMAINS") ) {
			//	ViewImporter.readDomainView(viewFile, project, viewName);
				DomainView domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
				domView.importXML(viewFile);
				ViewHandler.getInstance().addView(domView, project, false);
			}
			
			else if ( viewType.equals("TREE") ) {
			//	ViewImporter.readTreeView(viewFile, project, viewName);
				TreeView treeView = ViewHandler.getInstance().createView(ViewType.TREE, viewName);
				treeView.importXML(viewFile);
				ViewHandler.getInstance().addView(treeView, project, false);
			}
				
			else if ( viewType.equals("DOMAINTREE") ) {
				DomainTreeView domTreeView = ViewHandler.getInstance().createView(ViewType.DOMAINTREE, viewName);
				domTreeView.importXML(viewFile);
				ViewHandler.getInstance().addView(domTreeView, project, false);
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


