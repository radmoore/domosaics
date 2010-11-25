package angstd.ui.wizards.importdata;

import java.awt.EventQueue;
import java.io.File;
import java.util.Map;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.DataType;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.io.ArrangementImporterUtil;
import angstd.model.arrangement.io.HmmOutReader;
import angstd.model.arrangement.io.XdomReader;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.model.tree.TreeI;
import angstd.model.tree.io.NewickTreeReader;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.views.ViewType;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.views.treeview.TreeViewI;

/**
 * Class producing the resulting view based on the ImportData wizard.
 * 
 * @author Andreas Held
 *
 */
@SuppressWarnings("unchecked")
public class ImportDataResultProducer extends DeferredWizardResult  implements WizardResultProducer{
	
	/**
	 * Method triggered when the wizard finished to process the 
	 * user input and create a new view.
	 */
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
				
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
				case TREE: 		importTree(project, file, viewName, domAssocView); break;
				case DOMAINS: 	importArrangements(project, file, viewName, treeAssocView, seqAssocView); break;
				case SEQUENCE: 	importSequences(project, file, viewName, domAssocView); break;
			}

			p.finished(null);
			
		}catch(Exception e){
			p.failed("Error while creating Project, please try again.", false);
			p.finished(null);
		}	
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
		if (tree == null)
			return false;
		
		// create view
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, viewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, project);
		
		// create domain tree
		if (assocView != null) {
			DomainViewI domView = ViewHandler.getInstance().getView(assocView.getViewInfo());
			String name = domView.getViewInfo().getName()+"_tree";
			
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, name);
			domTreeView.setBackendViews(treeView, domView);
			ViewHandler.getInstance().addView(domTreeView, null);
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
		
		if (daSet == null)
			return false;
		
		// create view
		DomainViewI domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		domView.setDaSet(daSet);
		ViewHandler.getInstance().addView(domView, project);
		
		if (seqAssocView != null) {
			SequenceView seqView = ViewHandler.getInstance().getView(seqAssocView.getViewInfo());
			SequenceI[] seqs = seqView.getSequences();
			domView.loadSequencesIntoDas(seqs, daSet);
		}
		
		// create domain tree
		if (assocView != null) {
			TreeViewI treeView = ViewHandler.getInstance().getView(assocView.getViewInfo());
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, viewName+"_tree");
			domTreeView.setBackendViews(treeView, domView);
			ViewHandler.getInstance().addView(domTreeView, null);
		}
		
		
		
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
		if (seqs == null)
			return false;
		
		// create the sequence view
		SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, viewName);
		seqView.setSeqs(seqs);
		ViewHandler.getInstance().addView(seqView, project);
		
		// associate sequences with selected domain view
		if (assocView != null) {
			DomainViewI domView = ViewHandler.getInstance().getView(assocView.getViewInfo());
			domView.loadSequencesIntoDas(seqs, domView.getDaSet());
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
