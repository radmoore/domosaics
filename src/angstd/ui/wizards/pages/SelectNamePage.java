package angstd.ui.wizards.pages;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.model.workspace.CategoryElement;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.workspace.WorkspaceSelectionManager;

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
	private String objectName;
	
	
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
		this.objectName = objectName;
		setLayout(new MigLayout());
		
		name = new JTextField(20);
		name.setEditable(true);
		name.setText(defaultName);
		name.setName(ELEMENT_KEY);

		add(new JLabel("Select "+objectName+" name "), "gap 10");
		add(name, "gap 10");
	}

    /**
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
		
		String newName = name.getText().trim();
		if (objectName.equals("project")) {
			if (newName.isEmpty())
				return "Select a name";
			
			if (newName.equals("Default Project"))
				return "Default Project name disallowed";
			
			if (WorkspaceManager.getInstance().projectExists(newName))
				return "Name taken - choose new name";
		}
		
		if (objectName.equals("view")) {
			WorkspaceSelectionManager wsm = WorkspaceManager.getInstance().getSelectionManager();
			ProjectElement project = wsm.getSelectedProject();
			WorkspaceElement wse = wsm.getSelectedElement();
			
			if (project.viewExists(newName, (CategoryElement)wse.getParent()))
				return "Name taken - choose new name";
			
		}

        return null;
    }
 
}
