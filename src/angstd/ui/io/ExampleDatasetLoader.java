package angstd.ui.io;

import java.io.IOException;
import java.io.InputStream;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.io.XdomReader;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.model.tree.TreeI;
import angstd.model.tree.io.NewickTreeReader;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.views.treeview.TreeViewI;

/**
 * Class which gives access to example files within the JAR package.
 * <p>
 * A small as well as a big data set can be loaded, so the user is
 * able to explore the program features without the need to have
 * an own data set.
 * 
 * @author Andreas Held
 *
 */
public class ExampleDatasetLoader {
	
	/** file location for the small example tree file */
	private static String TREE_SMALL = "../resources/examples/small/data.tree";
	
	/** file location for the small example domains file */
	private static String DOMS_SMALL = "../resources/examples/small/data.xdom";
	
	/** file location for the small example sequence file */
	private static String SEQS_SMALL = "../resources/examples/small/data.fasta";
	
	/** file location for the big example tree file */
	private static String TREE_BIG = "../resources/examples/big/lectins.tree";
	
	/** file location for the big example domains file */
	private static String DOMS_BIG = "../resources/examples/big/lectins.xdom";
	
	/** file location for the big example sequence file */
	private static String SEQS_BIG = "../resources/examples/big/lectins.fasta";
	
	/** the input stream for the tree file */
	private static InputStream treeFile = null;
	
	/** the input stream for the xdom file */
	private static InputStream xdomFile = null;
	
	/** the input stream for the sequence file */
	private static InputStream seqFile = null;
	
	/** flag indicating whether or not the small example set is to be loaded */
	private static boolean small;
	
	
	/**
	 * Loads the small example data set from within the Jar file, 
	 * creates all views and displays them.
	 */
	public void loadSmallSet() {	
		try {
			treeFile = getClass().getResourceAsStream(TREE_SMALL);
			xdomFile = getClass().getResourceAsStream(DOMS_SMALL);
			seqFile  = getClass().getResourceAsStream(SEQS_SMALL);
		
			small = true;
		
			loadViews();
			
			treeFile.close();
			xdomFile.close();
			seqFile.close();
		} catch (IOException e) {
			MessageUtil.showWarning("Failed to load example dataset");
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the big example data set from within the Jar file, 
	 * creates all views and displays them.
	 */
	public void loadBigSet() {
		try {
			treeFile = getClass().getResourceAsStream(TREE_BIG);
			xdomFile = getClass().getResourceAsStream(DOMS_BIG);
			seqFile  = getClass().getResourceAsStream(SEQS_BIG);
		
			small = false;
		
			loadViews();
			
			treeFile.close();
			xdomFile.close();
			seqFile.close();
		} catch (IOException e) {
			MessageUtil.showWarning("Failed to load example dataset");
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper method to do the actually loading and view creation.
	 */
	private static void loadViews() {
		// parse the files
		TreeI tree = new NewickTreeReader().getTreeFromStream(treeFile);
		DomainArrangement[] daSet = new XdomReader().getDataFromStream(xdomFile);
		SequenceI[] seqs = new FastaReader().getDataFromStream(seqFile);

		String seqViewName = null;
		String treeViewName = null;
		String domViewName = null;
		String domTreeViewName = null;
		
		if (small) {
			seqViewName = "SequenceView 1";
			treeViewName = "TreeView 1";
			domViewName = "DomainView 1";
			domTreeViewName = "DomainTreeView 1";
		} else {
			seqViewName = "SequenceView 2";
			treeViewName = "TreeView 2";
			domViewName = "DomainView 2";
			domTreeViewName = "DomainTreeView 2";
		}
		
		SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, seqViewName);
		seqView.setSeqs(seqs);
		ViewHandler.getInstance().addView(seqView, null, true);
		
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, null, true);
		
		DomainViewI domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, domViewName);
		domView.setDaSet(daSet);
		domView.loadSequencesIntoDas(seqs, daSet);
		ViewHandler.getInstance().addView(domView, null);

//		DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, domTreeViewName);
//		domTreeView.setBackendViews(treeView, domView);
//		ViewHandler.getInstance().addView(domTreeView, null, true);
	}
}
