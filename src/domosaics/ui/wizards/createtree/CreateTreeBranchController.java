package domosaics.ui.wizards.createtree;

import java.util.Map;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.algos.treecreation.PALAdapter;
import domosaics.model.sequence.SequenceI;
import domosaics.model.workspace.ViewElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.view.View;
import domosaics.ui.wizards.pages.ClustalW2Page;




/**
 * This is the controller which starts the create tree wizard. Because
 * there are multiple choices (depending on the used data structure 
 * (sequences or domains)) its necessary to give the user the correct 
 * wizard pages based on the users choices.
 * 
 * 
 * @author Andreas Held
 *
 */

public class CreateTreeBranchController extends WizardBranchController {
	
	/** the key used to access the domain view after the wizard finished */
	public static final String DOMVIEW_KEY = "domview";
	
	/** the key used to access the sequence view after the wizard finished */
	public static final String SEQUENCEVIEW_KEY = "sequenceview";
	
	/** the key used to access the flag if underlying sequences should be used after the wizard finished */
	public static final String USEUNDERLYINGSEQS_KEY = "useUnderlyingSeqs";
	
	/** the key used to access the chosen distance measure after the wizard finished */
	public static final String MEASURE_KEY = "distancemeasure";
	
	/** the key used to access the chosen algorithm after the wizard finished */
	public static final String ALGO_KEY = "algorithm";
	
	/** the key used to access the substitution matrix after the wizard finished */
	public static final String SUBSTITUTION_KEY = "substitution";
	
	
	 /** the DomainWizard showing up only if the tree should be created based on domain distances */
    protected Wizard domWiz;
    
    /** the SequenceWizard showing up only if the tree should be created based on sequences */
    protected Wizard seqWiz; 
    
    /** the actually chosen wizard (domain or sequence) */
    protected Wizard choosedWiz = null;
    
    
    /**
     * Constructor for a new Import data wizard defining the base pages.
     */
    public CreateTreeBranchController() {
        //create the base pages
        super(new WizardPage[]{new ChooseViewDataPage()});
       
        // optional pages
        seqWiz = WizardPage.createWizard ( new Class[]{CreateTreeSequencePage.class}, new CreateTreeResultProducer());
        domWiz = WizardPage.createWizard ( new Class[]{CreateTreeDomainPage.class}, new CreateTreeResultProducer());
    }
    
    /**
     * This method chooses the correct wizard after the choose data type step.
     * The class name is the default ID for instantiated WizardPages
     */
    public Wizard getWizardForStep (String step, Map data) {
    	if ("domosaics.ui.wizards.createtree.ChooseViewDataPage".equals(step)) {
    		if (data.get(DOMVIEW_KEY) == null) {
    			if (PALAdapter.isAligned(getSeqs(data)))
    				choosedWiz = seqWiz;
    			else {
    				WizardPage[] pages = new WizardPage[]{new ClustalW2Page(getSeqs(data)), new CreateTreeSequencePage()};
    				//WizardPage[] pages = new WizardPage[]{new CreateTreeSequencePage()};
    				choosedWiz = WizardPage.createWizard(pages, new CreateTreeResultProducer());
    			}
    		} else {
    			if ((Boolean) data.get(USEUNDERLYINGSEQS_KEY)) {
    				if (PALAdapter.isAligned(getSeqs(data))){
        				choosedWiz = seqWiz;
    				}
        			else{
        				//WizardPage[] pages = new WizardPage[]{new CreateTreeSequencePage()};
        				WizardPage[] pages = new WizardPage[]{new ClustalW2Page(getSeqs(data)), new CreateTreeSequencePage()};
        				choosedWiz = WizardPage.createWizard(pages, new CreateTreeResultProducer());
        			}
    			} else
    				choosedWiz = domWiz;
    		}
    	}

       return choosedWiz;
    }
    
    /**
     * Helper method to get the sequences from the chosen view
     * 
     * @param data
     * 		the wizard data
     * @return
     * 		the sequences of the chosen view
     */
    private SequenceI[] getSeqs(Map data) {
    	// get a grip on the sequences
    	ViewElement viewElt = null;
    	
    	if (data.get(SEQUENCEVIEW_KEY) != null)
    		viewElt = (ViewElement) data.get(SEQUENCEVIEW_KEY);
    	else if (data.get(DOMVIEW_KEY) != null)
    		viewElt = (ViewElement) data.get(DOMVIEW_KEY);
    		
    	if (viewElt == null)
    		return null;
    		
		View view =  ViewHandler.getInstance().getView(viewElt.getViewInfo());
		SequenceI[] seqs = (view instanceof SequenceView) ? ((SequenceView) view).getSeqs() : ((DomainViewI) view).getSequences();
		return seqs;
    }
    
   
}
