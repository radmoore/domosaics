package domosaics.ui.wizards.createtree;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.algos.distance.DistanceMeasureType;
import domosaics.algos.treecreation.PALAdapter;
import domosaics.algos.treecreation.TreeCreationAlgoType;
import domosaics.algos.treecreation.TreeCreationUtil;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.tree.TreeI;
import domosaics.model.workspace.CategoryElement;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.view.View;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.ClustalW2Page;
import domosaics.ui.wizards.pages.SelectNamePage;
import domosaics.webservices.clustalw.ClustalW2ResultParser;

import pal.alignment.Alignment;
import pal.alignment.AlignmentUtils;
import pal.datatype.DataType;
import pal.datatype.DataTypeTool;
import pal.distance.DistanceMatrix;
import pal.distance.DistanceParseException;
import pal.distance.DistanceTool;
import pal.distance.ReadDistanceMatrix;
import pal.substmodel.BLOSUM62;
import pal.substmodel.CPREV;
import pal.substmodel.Dayhoff;
import pal.substmodel.JTT;
import pal.substmodel.MTREV24;
import pal.substmodel.RateMatrix;
import pal.substmodel.SubstitutionModel;
import pal.substmodel.SubstitutionTool;
import pal.substmodel.VT;
import pal.substmodel.WAG;


/**
 * Class producing the resulting tree and the corresponding tree view based
 * on the CreateTree wizard.
 * 
 * @author Andreas Held
 *
 */
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
			if (seqView != null) {
				p.finished(createBasedOnSeqs(seqView, subst, algo, clustalOutput, p));
//				p.finished(createBasedOnSeqs(seqView, subst, algo, p ));
			}
			else if (useUnderlyingSeqs) {
				p.finished(createBasedOnSeqs(domView, subst, algo, clustalOutput, p));
//				p.finished(createBasedOnSeqs(domView, subst, algo, p ));
			}
			else {
				p.finished(createBasedOnDomains(domView, measure, algo, p));
			}

		}
		catch(Exception e){
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			p.failed("Error while creating Project", false);
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

		// create PAL distance matrix with DoMosaicS and adapt it to the PAL library
		p.setProgress ("Calculate distances", 1, 3);
		DistanceMatrix dm = PALAdapter.createMatrix(daSet, measure);

		// create unrooted tree
		p.setProgress ("Create tree (this may take some time)", 2, 3);
		TreeI tree = TreeCreationUtil.createTree(dm, algo);

		// name the views which are going to be created
		p.setProgress ("Creating resulting views", 3, 3);
		String treeViewName = null;
		String defaultTreeViewName = domView.getViewInfo().getName()+"_"+algo.name();
		String domTreeViewName = domView.getViewInfo().getName()+"_tree";
		
		ViewElement elem = WorkspaceManager.getInstance().getViewElement(domView.getViewInfo());
		ProjectElement activeProject = elem.getProject();
		
		while(treeViewName == null) {
			Map results = WizardManager.getInstance().selectNameWizard(defaultTreeViewName, "tree view", activeProject, true);
			treeViewName = (String) results.get(SelectNamePage.VIEWNAME_KEY);
			String projectName = (String) results.get(SelectNamePage.PROJECTNAME_KEY);
			activeProject = WorkspaceManager.getInstance().getProject(projectName);
		}
		
		// and create the tree view
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, activeProject);

		// as well as the domain tree view
		DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, domTreeViewName);
		domTreeView.setBackendViews(treeView, domView);
		ViewHandler.getInstance().addView(domTreeView, activeProject);
		
		return true;
	}

	
	/**
	 * TODO this is still questionable
	 * 
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
			
			if (view instanceof DomainViewI) {
				((DomainViewI)view).loadSequencesIntoDas(seqs, ((DomainViewI)view).getDaSet());
			}
		}
		
		
		// NOTE: this does not actually align sequences
		Alignment alignment = PALAdapter.createAlignment(seqs);
		
//		DataType dt = DataTypeTool.getNucleotides();
//		SubstitutionModel sm = SubstitutionTool.createJC69Model();
				
//		try {
//			// TODO: write to logfile
//			PrintWriter out = new PrintWriter(new File("/home/radmoore/Desktop/test/alntest.aln"));
//			PrintWriter out2 = new PrintWriter(new File("/home/radmoore/Desktop/test/alntest2.aln"));
//			AlignmentUtils.printCLUSTALW(alignment, out);
//			AlignmentUtils.printInterleaved(alignment, out2);
//			out.flush();
//			out.close();
//			out2.flush();
//			out2.close();
//		}
//		catch (Exception e) {
//			
//		}

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
			subMatrix = new Dayhoff(freqs);
		else if(subMatrixStr.equals("JTT"))
			subMatrix = new JTT(freqs);
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

		// ADM: HERE: dm looks pretty wierd
		DistanceMatrix dm = DistanceTool.constructEvolutionaryDistances(alignment, model);

		// dm2 now has rates (after call above) 
//		DistanceMatrix dm2 = DistanceTool.constructEvolutionaryDistances(alignment, model);
		
//		ReadDistanceMatrix rdm = null;
//		try {
//			rdm = new ReadDistanceMatrix("/home/radmoore/Desktop/test/dmel_test.dist");
//			System.out.println("This is the read dm: "+rdm);
//			PrintWriter pw = new PrintWriter("/home/radmoore/Desktop/test/smatrix_report.txt");
//			subMatrix.report(pw);
//			pw.flush();
//			pw.close();
//			
//		} catch (DistanceParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		double distance = dm2.getDistance(1, 2);
//		System.out.println("This is the distance between 1 and 2: "+distance);

		// Nico tests
		//System.out.println(dm.toString());
		
		// create unrooted tree
		p.setProgress ("Create tree (this may take some time)", 4, 5);
		TreeI tree = TreeCreationUtil.createTree(dm, algo);
		//TreeI tree = TreeCreationUtil.createTree(rdm, algo);

		// get currently active view
		//View activeView = ViewHandler.getInstance().getActiveView();
		//System.out.println(activeView.toString());
		ViewElement elem = WorkspaceManager.getInstance().getViewElement(view.getViewInfo());
		ProjectElement activeProject = elem.getProject();
		//CategoryElement category = null;
		
		// name the views which are going to be created
		p.setProgress ("Creating resulting views", 5, 5);
		String treeViewName = null;
		String defaultTreeViewName = view.getViewInfo().getName()+"_"+algo.name();
		String domTreeViewName = view.getViewInfo().getName()+"_tree"; /////// ACHTUNG /////
		
		/* // Verify that the view does not exist already
		if (activeProject.viewExists(treeViewName, activeProject.getCategory(ViewType.TREE)))
			MessageUtil.showInformation(null, "The view "+ treeViewName + " already exists. Please rename.");*/
		
		while(treeViewName == null) {
			Map results = WizardManager.getInstance().selectNameWizard(defaultTreeViewName, "tree view", activeProject, true);
			treeViewName = (String) results.get(SelectNamePage.VIEWNAME_KEY);
			String projectName = (String) results.get(SelectNamePage.PROJECTNAME_KEY);
			activeProject = WorkspaceManager.getInstance().getProject(projectName);
		}
		
		// and create the tree view
		TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
		treeView.setTree(tree);
		ViewHandler.getInstance().addView(treeView, activeProject);
		
		// and eventually the domain tree view
		if (view instanceof DomainViewI) {
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, domTreeViewName);
			domTreeView.setBackendViews(treeView, (DomainViewI)view);
			ViewHandler.getInstance().addView(domTreeView, activeProject);
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

