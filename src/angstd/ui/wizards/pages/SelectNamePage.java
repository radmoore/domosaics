package angstd.ui.wizards.pages;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

/**
 * WizardPage shown within the SelectViewNameDialog.
 * 
 * @author Andreas Held
 *
 */
public class SelectNamePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	/** the key used to access the view name after the wizard finished */
	public static final String ELEMENT_KEY = "viewName";
	
	/** text field to enter the objects name */
	protected JTextField name;
	
	
	/**
	 * Constructor for a new SelectViewNamePage
	 * 
	 * @param defaultName
	 * 		the default name for the object
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public SelectNamePage(String defaultName, String objectName) {
		super(objectName+" name selection");
		
		setLayout(new MigLayout());
		
		name = new JTextField(20);
		name.setEditable(true);
		name.setText(defaultName);
		name.setName(ELEMENT_KEY);

		add(new JLabel("Select "+objectName+" name. "), "gap 10");
		add(name, "gap 10");
	}

    /**
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
		if(name.getText().trim().isEmpty())
			return "Please select a view Name";
        return null;
    }
 
}
