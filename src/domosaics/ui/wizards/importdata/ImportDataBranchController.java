package domosaics.ui.wizards.importdata;

import java.util.Map;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.model.DataType;
import domosaics.model.workspace.ProjectElement;




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
    
    /** the previously chosen project **/
    private ProjectElement project;
    
    private static WizardPage projectPage;
    
    private static ChooseDataTypePage dataTypePage;
    
    /**
     * Constructor for a new Import data wizard defining the base pages.
     */
    public ImportDataBranchController() {
    	//create the base pages
    	super(new WizardPage[]{ new ChooseProjectPage(), new ChooseDataTypePage()});
    }    
    
    /**
     * This method chooses the correct wizard after the choose data type step.
     * The class name is the default ID for instantiated WizardPages
     */
	public Wizard getWizardForStep (String step, Map data) {
		
		if (data.containsKey(PROJECT_KEY)) 
			project = (ProjectElement)data.get(PROJECT_KEY);
		
		if ("domosaics.ui.wizards.importdata.ChooseDataTypePage".equals(step)) {
			if(data.get(DATATYPE_KEY)!=null)
				choosedWiz = WizardPage.createWizard(new WizardPage[]{ new SelectDataPage(project,(DataType)(data.get(DATATYPE_KEY)))}, new ImportDataResultProducer());
		} else {
			if ("domosaics.ui.wizards.importdata.SelectDataPage".equals(step)) {
				choosedWiz = WizardPage.createWizard(new WizardPage[]{ new ChooseDataTypePage(), new SelectDataPage(project,(DataType)(data.get(DATATYPE_KEY)))}, new ImportDataResultProducer());			  
			} else {
				if ("domosaics.ui.wizards.importdata.ChooseProjectPage".equals(step)) {
					if(data.get(PROJECT_KEY)!=null)
						choosedWiz = WizardPage.createWizard(new WizardPage[]{ new ChooseDataTypePage(), new SelectDataPage(project,(DataType)(data.get(DATATYPE_KEY)))}, new ImportDataResultProducer());
				}
					
			}
		}
		return choosedWiz;
    }

}


