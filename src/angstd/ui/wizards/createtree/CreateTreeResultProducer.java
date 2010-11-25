package angstd.ui.wizards.createtree;

import java.awt.EventQueue;
import java.util.Map;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import pal.alignment.Alignment;
import pal.alignment.AlignmentUtils;
import pal.distance.DistanceMatrix;
import pal.distance.DistanceTool;
import pal.substmodel.BLOSUM62;
import pal.substmodel.CPREV;
import pal.substmodel.Dayhoff;
import pal.substmodel.MTREV24;
import pal.substmodel.RateMatrix;
import pal.substmodel.SubstitutionModel;
import pal.substmodel.VT;
import pal.substmodel.WAG;
import angstd.algos.distance.DistanceMeasureType;
import angstd.algos.treecreation.PALAdapter;
import angstd.algos.treecreation.TreeCreationAlgoType;
import angstd.algos.treecreation.TreeCreationUtil;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.sequence.SequenceI;
import angstd.model.tree.TreeI;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.views.ViewType;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.view.View;
import angstd.ui.wizards.pages.ClustalW2Page;
import angstd.webservices.clustalw.ClustalW2ResultParser;

/**
 * Class producing the resulting tree and the corresponding tree view based
 * on the CreateTree wizard.
 * 
 * @author Andreas Held
 *
 */
@SuppressWarnings("unchecked")
public class CreateTreeResultProducer extends DeferredWizardResult  implements WizardResultProducer{
	
	/**
	 * Method triggered when the wizard finished to process the 
	 * user input and create a tree as well as the corresponding view.
	 */
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
				
		try {
			ViewElement domView = (ViewElement) m.get(CreateTreeBranchController.DOMVIEW_KEY);
			ViewElement seqView = (ViewElement) m.get(CreateTreeBranchController.SEQUENCEVIEW_KEY);
			TreeCreationAlgoType algo = (TreeCreationAlgoType) m.get(CreateTreeBranchController.ALGO_KEY);
			String subst = (String) m.get(CreateTreeBranchController.SUBSTITUTION_KEY);
			DistanceMeasureType measure = (DistanceMeasureType) m.get(CreateTreeBranchController.MEASURE_KEY);
			Boolean useUnderlyingSeqs = (Boolean) m.get(CreateTreeBranchController.USEUNDERLYINGSEQS_KEY);
			String clustalOutput = (String) m.get(ClustalW2Page.ALIGNMENT_KEY);

	   		// create tree and view
			if (seqView != null)
				p.finished(createBasedOnSeqs(seqView, subst, algo, clustalOutput, p ));
			else if (useUnderlyingSeqs)
				p.finished(createBasedOnSeqs(domView, subst, algo, clustalOutput, p ));
			else
				p.finished(createBasedOnDomains(domView, measure, algo, p ));

		}catch(Exception e){
			p.failed("Error while creating Project, please try again.", false);
			p.finished(null);
		}	
	}
	
	/**
	 * Helper method creating the tree based on domain arrangements
	 * 
	 * @param viewElt
	 * 		the chosen backend view
	 * @param measure
	 * 		the chosen distancem easure for domains
	 * @param algo
	 * 		the chosen tree construction algorithm
	 * @param p
	 * 		the progress handler
	 * @return
	 * 		whether or not the tree could be created successfully
	 */
	private boolean createBasedOnDomains(ViewElement viewElt, DistanceMeasureType measure, TreeCreationAlgoType algo, ResultProgressHandle p) {
		// first of all get the selected view and its dataSet
		p.setProgress ("Inititializing tree creation", 0, 3);
		DomainViewI domView =  ViewHandler.getInstance().getView(viewElt.getViewInfo());
		DomainArrangement[] daSet = domView.getDaSet();

		// create PAL distance matrix with Angstd and adapt it to the PAL library
		p.setProgress ("Calculate distances", 1, 3);
		DistanceMatrix dm = PALAdapter.createMatrix(daSet, measure);

		// create unrooted tree
		p.setProgress ("Create tree (this may take some time)", 2, 3);
		TreeI tree = TreeCreationUtil.createTree(dm, algo);

		// name the views which are going to be created
		p.setProgress ("Creating resulting views", 3, 3);
		String treeViewName = domView.getViewInfo().getName()+"_"+algo.name();
		String domTreeViewName = domView.getViewInfo().getName()+"_tree";
		
		// and create the tree view
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, null);

		// as well as the domain tree view
		DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, domTreeViewName);
		domTreeView.setBackendViews(treeView, domView);
		ViewHandler.getInstance().addView(domTreeView, null);
		
		return true;
	}

	/**
	 * Helper method creating the tree based on sequences
	 * 
	 * @param viewElt
	 * 		the chosen backend view
	 * @param subMatrixStr
	 * 		the chosen substitution matrix
	 * @param algo
	 * 		the chosen tree construction algorithm
	 * @param clustalOutput
	 * 		the clustalw2 output if the sequences were aligned
	 * @param p
	 * 		the progress handler
	 * @return
	 * 		whether or not the tree could be created successfully
	 */
	private boolean createBasedOnSeqs(ViewElement viewElt, String subMatrixStr, TreeCreationAlgoType algo, String clustalOutput, ResultProgressHandle p) {
		// first of all get the selected view and its sequences
		p.setProgress ("Inititializing tree creation", 0, 5);
		View view =  ViewHandler.getInstance().getView(viewElt.getViewInfo());
	
		// get the sequences depending on the view type (can be sequence or domain view)
		SequenceI[] seqs = (view instanceof SequenceView) ? ((SequenceView) view).getSeqs() : ((DomainViewI) view).getSequences();

		// Check if the sequences are aligned already
		p.setProgress ("Process clustal output", 1, 5);
		if (clustalOutput != null) {
			seqs = new ClustalW2ResultParser().parseResult(clustalOutput);
	
			if (view instanceof DomainViewI)
				((DomainViewI)view).loadSequencesIntoDas(seqs, ((DomainViewI)view).getDaSet());
		}
		
		Alignment alignment = PALAdapter.createAlignment(seqs);

		// get number of different states
		double[] freqs = AlignmentUtils.estimateFrequencies( alignment );

		// create a substitutionmatrix for amino acid
		p.setProgress ("Create substitution matrix", 2, 5);
		RateMatrix subMatrix;
		if (subMatrixStr.equals("BLOSUM62"))
			subMatrix = new BLOSUM62(freqs);
		else if(subMatrixStr.equals("CPREV"))
			subMatrix = new CPREV(freqs);
		else if(subMatrixStr.equals("Dayhoff"))
			subMatrix = new BLOSUM62(freqs);
		else if(subMatrixStr.equals("JTT"))
			subMatrix = new Dayhoff(freqs);
		else if(subMatrixStr.equals("MTREV24"))
			subMatrix = new MTREV24(freqs);
		else if(subMatrixStr.equals("VT"))
			subMatrix = new VT(freqs);
		else if(subMatrixStr.equals("WAG"))
			subMatrix = new WAG(freqs);
		else
			subMatrix = new BLOSUM62(freqs);
		
		// create the substituion model
		SubstitutionModel model = SubstitutionModel.Utils.createSubstitutionModel(subMatrix);
		
		// create the distance matrix
		p.setProgress ("Create distance matrix (this may take some time)", 3, 5);
		DistanceMatrix dm = DistanceTool.constructEvolutionaryDistances(alignment, model);
		
		// create unrooted tree
		p.setProgress ("Create tree (this may take some time)", 4, 5);
		TreeI tree = TreeCreationUtil.createTree(dm, algo);
		
		// name the views which are going to be created
		p.setProgress ("Creating resulting views", 5, 5);
		String treeViewName = view.getViewInfo().getName()+"_"+algo.name();
		String domTreeViewName = view.getViewInfo().getName()+"_tree";
		
		// and create the tree view
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, null);
		
		// and eventually the domain tree view
		if (view instanceof DomainViewI) {
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, domTreeViewName);
			domTreeView.setBackendViews(treeView, (DomainViewI)view);
			ViewHandler.getInstance().addView(domTreeView, null);
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
