package domosaics.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.model.configuration.Configuration;
import domosaics.model.tree.TreeI;
import domosaics.model.tree.io.NewickTreeReader;
import domosaics.model.tree.io.NexusTreeReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.treeview.TreeView;
import domosaics.ui.views.view.io.ViewImporter;
import domosaics.ui.wizards.pages.ImportViewPage;
import domosaics.ui.wizards.pages.LoadTreePage;




/**
 * 
 * @author N. Terrapon 
 *
 */
public class LoadTreeDialog {

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
	public LoadTreeDialog() {
	}
	
	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the chosen name
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new LoadTreePage()}, new LoadTreeProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class LoadTreeProgress implements WizardResultProducer{
	
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
	}
	
	@Override
	public boolean cancel(Map m) {
		return true;
	}
	
	@Override
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
			
			TreeView treeView = ViewHandler.getInstance().createView(ViewType.TREE, viewName);
			
			// Adapted by Nico
			TreeI tree;
			BufferedReader in = null;
			String firstLine = "";
			try {
				in = new BufferedReader(new FileReader(viewFile));
				firstLine=in.readLine();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if(firstLine.toUpperCase().equals("#NEXUS"))
				tree = new NexusTreeReader().getTreeFromFile(viewFile);
			else
				tree = new NewickTreeReader().getTreeFromFile(viewFile);
			if(tree!=null) {
				treeView.setTree(tree);
			
				ViewHandler.getInstance().addView(treeView	, project, false);
				//	ViewImporter.readSequenceView(viewFile, project, viewName);
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


