package angstd.ui.wizards.createtree;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.wizards.GUIComponentFactory;

/**
 * WizardPage which allows the user to choose the distance measure
 * (e.g. Jaccard) as well as the algorithm (e.g. NJ) for tree creation
 * based on domain compositions.
 * 
 * @author Andreas Held
 *
 */
public class CreateTreeDomainPage extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** list displaying the available distance measures */
	protected JComboBox selectDistanceMeasure;
	
	/** list displaying the available tree creation algorithms */
	protected JComboBox selectAlgo;

	
	/**
	 * Constructor for a new CreateTreeDomainPage
	 */
	public CreateTreeDomainPage() {
		setLayout(new MigLayout());
		
		selectDistanceMeasure = GUIComponentFactory.createMeasureBox();
		selectAlgo = GUIComponentFactory.createAlgoBox();
		
		selectDistanceMeasure.setName(CreateTreeBranchController.MEASURE_KEY);
		selectAlgo.setName(CreateTreeBranchController.ALGO_KEY);
		
		add(new JXTitledSeparator("Select similarity measure for distance measure"), "growx, span, wrap, gaptop 10");
		add(new JLabel("Select measure:"), 		"gap 10");
		add(selectDistanceMeasure, 			"gap 10, span, growx, wrap");
		
		add(new JXTitledSeparator("Select algorithm used for tree creation"), "growx, span, wrap, gaptop 40");
		add(new JLabel("Select measure:"), 		"gap 10");
		add(selectAlgo, 						"gap 10, span, growx, wrap");
	}

	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Select similarity measure and algorithm";
    }
    
    /**
     * Checks if all necessary input is made.
     */
    protected String validateContents (Component component, Object o) {
    	if (selectDistanceMeasure.getSelectedItem() == null)
			return "Please select a similarity measure";
    	if (selectAlgo.getSelectedItem() == null)
			return "Please select an algorithm for tree creation";
    	
        return null;
    }


}
