package domosaics.ui.io;

import java.io.IOException;
import java.io.InputStream;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.io.XdomReader;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.tree.TreeI;
import domosaics.model.tree.io.NewickTreeReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * Class for loading the example dataset into DoMosaics.
 * 
 * @author <a href="http://radm.info">Andrew Moore</a>
 *
 */
public class ExampleDatasetLoader {

	private static String TREE = "/domosaics/ui/resources/examples/example_data/example.tree";
	
	/** file location for the small example domains file */
	private static String DOMS = "/domosaics/ui/resources/examples/example_data/example.xdom";
	
	/** file location for the small example sequence file */
	private static String SEQS = "/domosaics/ui/resources/examples/example_data/example.fasta";
	
	/** the input stream for the tree file */
	private static InputStream treeFile = null;
	
	/** the input stream for the xdom file */
	private static InputStream xdomFile = null;
	
	/** the input stream for the sequence file */
	private static InputStream seqFile = null;
	
	
	/**
	 * Loads the small example data set from within the Jar file, 
	 * creates all views and displays them.
	 */
	public void loadExample() {
		
		if (WorkspaceManager.getInstance().projectExists("example_project")) {
			MessageUtil.showInformation(null, "The example project is already loaded");
			return;
		}
		
		try {

			treeFile = this.getClass().getResourceAsStream(TREE);
			xdomFile = this.getClass().getResourceAsStream(DOMS);
			seqFile  = this.getClass().getResourceAsStream(SEQS);

			loadViews();
			
			treeFile.close();
			xdomFile.close();
			seqFile.close();
			
		} 
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Failed to load example dataset");
		}
	}
	
	/**
	 * Helper method to do the actual loading and view creation.
	 */
	private static void loadViews() {
		
		// parse the files
		TreeI tree = new NewickTreeReader().getTreeFromStream(treeFile);
		DomainArrangement[] daSet = new XdomReader().getDataFromStream(xdomFile);
		SequenceI[] seqs = new FastaReader().getDataFromStream(seqFile);
		
		ProjectElement project = null;

		String seqViewName = null;
		String treeViewName = null;
		String domViewName = null;
//		String domTreeViewName = null;
		
		seqViewName = "example_sequences";
		treeViewName = "example_tree";
		domViewName = "example_domains";
//		domTreeViewName = "small.x.domtree";
		project = new ProjectElement("example_project");
		WorkspaceManager.getInstance().addProject(project, true);
		
		
//		if (small) {
//			seqViewName = "small.x.seq";
//			treeViewName = "small.x.tree";
//			domViewName = "small.x.doms";
//			domTreeViewName = "small.x.domtree";
//			project = new ProjectElement("small.x.dataset");
//			WorkspaceManager.getInstance().addProject(project, true);
//		} 
//		else {
//			seqViewName = "large.x.seq";
//			treeViewName = "large.x.tree";
//			domViewName = "large.x.doms";
//			domTreeViewName = "large.x.domtree";
//			project = new ProjectElement("large.x.dataset");
//			WorkspaceManager.getInstance().addProject(project, true);
//		}
		
		
		
		SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, seqViewName);
		seqView.setSeqs(seqs);
		ViewHandler.getInstance().addView(seqView, project, true);
		
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, project, true);
		
		DomainViewI domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, domViewName);
		domView.setDaSet(daSet);
		domView.loadSequencesIntoDas(seqs, daSet, false);
		ViewHandler.getInstance().addView(domView, project);

//		DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, domTreeViewName);
//		domTreeView.setBackendViews(treeView, domView);
//		ViewHandler.getInstance().addView(domTreeView, null, true);
	}
}
