package angstd.ui.wizards.createtree;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.wizards.GUIComponentFactory;



/**
 * WizardPage which allows the user to choose the substitution matrix
 * (e.g. Blosum) as well as the algorithm (e.g. NJ) for tree creation
 * based on sequences.
 * 
 * @author Andreas Held
 *
 */
public class CreateTreeSequencePage extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** list displaying the available substitution matrices supported by the external PAL library */
	protected JComboBox selectSubstitution;
	
	/** list displaying the available tree creation algorithms */
	protected JComboBox selectAlgo;

	/**
	 * Constructor for a new CreateTreeSequencePage
	 */
	public CreateTreeSequencePage() {
		setLayout(new MigLayout());
		
		selectSubstitution = GUIComponentFactory.createSubstitutionBox();
		selectAlgo = GUIComponentFactory.createAlgoBox();
		
		selectSubstitution.setName(CreateTreeBranchController.SUBSTITUTION_KEY);
		selectAlgo.setName(CreateTreeBranchController.ALGO_KEY);

		add(new JXTitledSeparator("Select Substitution Matrix"), "growx, span, wrap, gaptop 10");
		add(new JLabel("Select measure:"), 	"gap 10");
		add(selectSubstitution, "w 150!, gap 10");
		add(new JLabel(""), "w 101!, gap 10, wrap");
		
		add(new JXTitledSeparator("Select algorithm used for tree creation"), "growx, span, wrap, gaptop 25");
		add(new JLabel("Select measure:"), 		"gap 10");
		add(selectAlgo, "w 150!, gap 10, wrap");
	}

	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Select substitution matrix and algorithm";
    }
    
    /**
     * Checks if all necessary input is made.
     */
    protected String validateContents (Component component, Object o) {
    	if (selectSubstitution.getSelectedItem() == null)
			return "Please select a substitution matrix";
    	if (selectAlgo.getSelectedItem() == null)
			return "Please select an algorithm for tree creation";
    	
        return null;
    }


}
