package domosaics.ui.wizards.importdata;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.model.DataType;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.io.ArrangementImporterUtil;
import domosaics.model.arrangement.io.HmmOutReader;
import domosaics.model.arrangement.io.XdomReader;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.tree.Tree;
import domosaics.model.tree.TreeI;
import domosaics.model.tree.io.NewickTreeReader;
import domosaics.model.workspace.CategoryElement;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.SelectNamePage;

/**
 * Class producing the resulting view based on the ImportData wizard.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */

public class ImportDataResultProducer extends DeferredWizardResult  implements WizardResultProducer{

	private String validatedNewName = null;
	/**
	 * Method triggered when the wizard finished to process the 
	 * user input and create a new view.
	 */
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		boolean noError = false;
		try {
			// read wizard data
			File file = new File((String) m.get(ImportDataBranchController.FILEPATH_KEY));
			ProjectElement project = (ProjectElement) m.get(ImportDataBranchController.PROJECT_KEY);
			ViewElement domAssocView = (ViewElement) m.get(ImportDataBranchController.DOMVIEW_KEY);
			ViewElement treeAssocView = (ViewElement) m.get(ImportDataBranchController.TREEVIEW_KEY);
			ViewElement seqAssocView = (ViewElement) m.get(ImportDataBranchController.SEQVIEW_KEY);
			String viewName = (String) m.get(ImportDataBranchController.VIEWNAME_KEY);

			// create view
			switch((DataType) m.get(ImportDataBranchController.DATATYPE_KEY)) {
			case TREE: 		noError = importTree(project, file, viewName, domAssocView); break;
			case DOMAINS: 	noError = importArrangements(project, file, viewName, treeAssocView, seqAssocView); break;
			case SEQUENCE: 	noError = importSequences(project, file, viewName, domAssocView); break;
			}
			
		}
		catch(Exception e){
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			p.failed("Error while editing the project", false);
		}
		p.finished(null);
	}
	
	/**
	 * Helper method to actually create the tree view
	 * 
	 * @param project
	 * 		the chosen project the view is being add to
	 * @param file
	 * 		the file in which the view data is stored
	 * @param viewName
	 * 		the chosen name for the new view
	 * @param assocView
	 * 		the view which is going to be associated with the created one
	 * @return 
	 * 		whether or not the view could be created
	 */
	private boolean importTree(ProjectElement project, File file, String viewName, ViewElement assocView) {
		
		// parse the tree file
		TreeI tree = new NewickTreeReader().getTreeFromFile(file);
		
		//Nico: now checked just after the browsing
		//if (tree == null)
		//	return false;
		
		// create view
		
		/*if (project.viewExists(viewName, project.getCategory(ViewType.TREE)))
			MessageUtil.showInformation(null, "The view "+ viewName + " already exists. Please rename.");*/
		while(validatedNewName == null) {
			Map results = WizardManager.getInstance().selectNameWizard(viewName, "tree view", project, false);
			if(results!=null) {
				validatedNewName = (String) results.get(SelectNamePage.VIEWNAME_KEY);
				String projectName = (String) results.get(SelectNamePage.PROJECTNAME_KEY);
				project = WorkspaceManager.getInstance().getProject(projectName);
			} else{
				return false;
			}			
		}
		
		//Nico cannot happen?
		//if (viewName == null)
		//	return false;
		
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, validatedNewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, project);
		
		// create domain tree
		if (assocView != null) {
			DomainViewI domView = ViewHandler.getInstance().getView(assocView.getViewInfo());
			String name = domView.getViewInfo().getName()+"_tree";
			
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, name);
			domTreeView.setBackendViews(treeView, domView);
			ViewHandler.getInstance().addView(domTreeView, project);
		}
		return true;
	}

	/**
	 * Helper method to actually create the domain view
	 * 
	 * @param project
	 * 		the chosen project the view is being add to
	 * @param file
	 * 		the file in which the view data is stored
	 * @param viewName
	 * 		the chosen name for the new view
	 * @param assocView
	 * 		the view which is going to be associated with the created one
	 * @return 
	 * 		whether or not the view could be created
	 */
	private boolean importArrangements(ProjectElement project, File file, String viewName, ViewElement assocView, ViewElement seqAssocView) {
		// check if its xdom format, else parse hmmer2
		DomainArrangement[] daSet = ArrangementImporterUtil.importData(file);
		
		//Nico: now checked just after the browsing
		//if (daSet == null)
		//	return false;
		
		int importedProts = daSet.length;
		if (importedProts < 1) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No proteins found in file");
			return false;	
		}
			

		// create view
		/*if (project.viewExists(viewName, project.getCategory(ViewType.DOMAINS)))
			MessageUtil.showInformation(null, "The view "+ viewName + " already exists. Please rename.");*/

		while(validatedNewName == null) {
			Map results = WizardManager.getInstance().selectNameWizard(viewName, "domain view", project, false);
			if(results!=null) {
				validatedNewName = (String) results.get(SelectNamePage.VIEWNAME_KEY);
				String projectName = (String) results.get(SelectNamePage.PROJECTNAME_KEY);
				project = WorkspaceManager.getInstance().getProject(projectName);
			} else {
				return false;
			}
		}

		//Nico: cannot happen?
		//if (viewName == null)
		//	return false;
		

		DomainViewI domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, validatedNewName);
		domView.setDaSet(daSet);
		ViewHandler.getInstance().addView(domView, project);
		
		if (seqAssocView != null) {
			SequenceView seqView = ViewHandler.getInstance().getView(seqAssocView.getViewInfo());
			SequenceI[] seqs = seqView.getSequences();
			domView.loadSequencesIntoDas(seqs, daSet, true);
		}
		
		// create domain tree
		if (assocView != null) {
			TreeViewI treeView = ViewHandler.getInstance().getView(assocView.getViewInfo());
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, validatedNewName+"_tree");
			domTreeView.setBackendViews(treeView, domView);
			ViewHandler.getInstance().addView(domTreeView, project);
		}
		MessageUtil.showInformation(null, importedProts+" proteins successfully imported.");
		return true;
	}
	
	/**
	 * Helper method to actually create the sequence view
	 * 
	 * @param project
	 * 		the chosen project the view is being add to
	 * @param file
	 * 		the file in which the view data is stored
	 * @param viewName
	 * 		the chosen name for the new view
	 * @param assocView
	 * 		the view which is going to be associated with the created one
	 * @return 
	 * 		whether or not the view could be created
	 */
	private boolean importSequences(ProjectElement project, File file, String viewName, ViewElement assocView) { 

		// parse the sequence file
		SequenceI[] seqs = new FastaReader().getDataFromFile(file);
		//Nico: now checked just after the browsing
		//if (seqs == null)
		//	return false;
		
		// ensure that view to be added is not already present, rename if it is
		/*if (project.viewExists(viewName, project.getCategory(ViewType.SEQUENCE)))
			MessageUtil.showInformation(null, "The view "+ viewName + " already exists. Please rename.");*/

		while(validatedNewName == null) {
			Map results = WizardManager.getInstance().selectNameWizard(viewName, "sequence view", project, false);
			if(results!=null) {
				validatedNewName = (String) results.get(SelectNamePage.VIEWNAME_KEY);
				String projectName = (String) results.get(SelectNamePage.PROJECTNAME_KEY);
				project = WorkspaceManager.getInstance().getProject(projectName);
			} else{
				return false;
			}		
		}

		//Nico: cannot happen?
		//if (viewName == null)
		//	return false;
		
		SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, validatedNewName);
		seqView.setSeqs(seqs);
		ViewHandler.getInstance().addView(seqView, project);
		
		// associate sequences with selected domain view
		if (assocView != null) {
			
			DomainViewI domView = ViewHandler.getInstance().getView(assocView.getViewInfo());
			domView.loadSequencesIntoDas(seqs, domView.getDaSet(), true);
		}
		return true;
	}
	
	/**
	 * @see WizardResultProducer
	 */
	public boolean cancel(Map m) {
		return true;
	}
	
	/**
	 * @see WizardResultProducer
	 */
	public Object finish(Map m) throws WizardException {
		return this;
	}	
}
