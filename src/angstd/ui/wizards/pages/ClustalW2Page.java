package angstd.ui.wizards.pages;

import java.awt.Component;

import org.netbeans.spi.wizard.WizardPage;

import angstd.model.sequence.SequenceI;
import angstd.webservices.UiUtil;
import angstd.webservices.clustalw.ClustalW2Panel;

/**
 * WizardPage allowing the use the EBI web service CLustalW2.
 * 
 * @author Andreas Held
 *
 */
public class ClustalW2Page extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** the key used to access the alignment string after the wizard finished */
	public static final String ALIGNMENT_KEY = "alignmentStr";
	
	/** the ClustalW2 panel specified in the corresponding webservice package */
	protected ClustalW2Panel panel;
	
	
	/**
	 * Constructor for a new ClustalW2Page
	 * 
	 * @param seqs
	 * 		the sequences which are going to be aligned
	 */
	public ClustalW2Page(SequenceI[] seqs) {
		panel = new ClustalW2Panel(seqs, this);
		add(panel);
	}
	
	/**
	 * The description for this page used within the wizard
	 * 
	 * @return
	 * 		description for this page used within the wizard
	 */
    public static final String getDescription() {
        return "Align using ClustalW2";
    }
    
    /**
     * Checks if all necessary input is made.
     */
    protected String validateContents (Component component, Object o) {
    	if (!UiUtil.isValidEmail(panel.getEmail()))
			return "Please enter a valid email adress";
    	if (!panel.isJobDone())
			return "Please wait for ClustalW2 to finsih";

        return null;
    }
    
    /**
     * Method triggered when the webservice finished its job
     * 
     * @param result
     * 		the result of webservice
     */
    public void finish(String result) {
    	putWizardData(ALIGNMENT_KEY, result);
    }

}
