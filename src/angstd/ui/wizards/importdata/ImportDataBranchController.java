package angstd.ui.wizards.importdata;

import java.util.Map;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPage;

import angstd.model.DataType;
import angstd.model.workspace.ProjectElement;

/**
 * This is the controller which starts the import data wizard. Because
 * there are multiple choices (depending on the chosen data type) its 
 * necessary to give the user the correct wizard pages based on his choices.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */

public class ImportDataBranchController extends WizardBranchController {

	/** the key used to access the project after the wizard finished */
	public static final String PROJECT_KEY = "project";
	
	/** the key used to access the data type after the wizard finished */
	public static final String DATATYPE_KEY = "datatype";
	
	/** the key used to access the file path after the wizard finished */
	public static final String FILEPATH_KEY = "filepath";
	
	/** the key used to access the tree view after the wizard finished */
	public static final String DOMVIEW_KEY = "domview";
	
	/** the key used to access the tree view after the wizard finished */
	public static final String TREEVIEW_KEY = "treeview";
	
	/** the key used to access the tree view after the wizard finished */
	public static final String SEQVIEW_KEY = "seqview";
	
	/** the key used to access the tree view after the wizard finished */
	public static final String VIEWNAME_KEY = "viewname";
	
	/** the TreeWizard showing up only if the tree data type was selected */
    protected Wizard treeWiz;
    
    /** the DomainWizard showing up only if the domain data type was selected */
    protected Wizard domWiz;
    
    /** the SequenceWizard showing up only if the sequence data type was selected */
    protected Wizard seqWiz; 
    
    /** the actually chosen wizard (tree, domain or sequence) */
    protected Wizard choosedWiz = null;
    
    private SelectArrangementDataPage sadp;
    
    private ProjectElement project;
    
    /**
     * Constructor for a new Import data wizard defining the base pages.
     */
    public ImportDataBranchController() {
    	//create the base pages
    	super(new WizardPage[]{new ChooseProjectPage(null), new ChooseDataTypePage()});

    	
        // optional pages
        treeWiz = WizardPage.createWizard( new Class[]{SelectTreeDataPage.class}, new ImportDataResultProducer() );
//        domWiz = WizardPage.createWizard ( new Class[]{sadp.getClass()}, new ImportDataResultProducer());
        domWiz = WizardPage.createWizard (new WizardPage[]{new SelectArrangementDataPage()}, new ImportDataResultProducer());
//        domWiz = WizardPage.createWizard ( new Class[]{SelectArrangementDataPage.class}, new ImportDataResultProducer());

        seqWiz = WizardPage.createWizard ( new Class[]{SelectSequenceDataPage.class}, new ImportDataResultProducer());
    }
    
    /**
     * This method chooses the correct wizard after the choose data type step.
     * The class name is the default ID for instantiated WizardPages
     */
	public Wizard getWizardForStep (String step, Map data) {
		
		if (data.containsKey(PROJECT_KEY)) 
			project = (ProjectElement)data.get(PROJECT_KEY);
		
		
    	if ("angstd.ui.wizards.importdata.ChooseDataTypePage".equals(step)) {
    	   if (data.get(DATATYPE_KEY) == DataType.TREE) { 
//    	   	   choosedWiz = WizardPage.createWizard (new WizardPage[]{new SelectTreeDataPage(project)}, new ImportDataResultProducer());
		   	   choosedWiz = treeWiz;
    	   }
    	   if (data.get(DATATYPE_KEY) == DataType.DOMAINS) {
    		   choosedWiz = WizardPage.createWizard (new WizardPage[]{new SelectArrangementDataPage(project)}, new ImportDataResultProducer());
//    		   choosedWiz = domWiz;
    	   }
    	   if (data.get(DATATYPE_KEY) == DataType.SEQUENCE) {
    		   choosedWiz = WizardPage.createWizard (new WizardPage[]{new SelectSequenceDataPage(project)}, new ImportDataResultProducer());
//    		   choosedWiz = seqWiz;
    	   }
    		   
        }
       return choosedWiz;
    }
}


