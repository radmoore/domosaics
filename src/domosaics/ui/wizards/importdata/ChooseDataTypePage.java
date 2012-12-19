package domosaics.ui.wizards.importdata;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import domosaics.ui.wizards.GUIComponentFactory;


/**
 * WizardPage which allows the user to choose between different
 * data types (e.g. sequence, arrangements, tree). Based on the 
 * users choice for instance a new view of the specified type can be 
 * created.
 * 
 * @author Andreas Held
 *
 */
public class ChooseDataTypePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	/** list displaying the available data types */
	protected JList list;
	
	
	/**
	 * Constructor for a new ChooseDataTypePage
	 */
	public ChooseDataTypePage() {
		super("Data Selection");
		
		setLayout(new MigLayout());
		
		// init the data type list
		list = GUIComponentFactory.createDataTypeList();
		list.setName(ImportDataBranchController.DATATYPE_KEY);
		
		add(new JLabel("Select the data type to import"), "w 300!, gap 10, wrap");
		add(new JScrollPane(list), "gap 10, span, growx");
	}

	/**
     * Checks if all necessary input is made.
     */
    protected String validateContents (Component component, Object o) {
    	if (list.getSelectedValue() == null)
			return "Please select the data type to import";
        return null;
    }

}
