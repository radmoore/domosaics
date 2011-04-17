package angstd.ui.wizards.pages;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.model.workspace.CategoryElement;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.ViewType;

/**
 * WizardPage shown within the SelectViewNameDialog.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SelectRenamePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String ELEMENT_KEY = "viewName";
	public static final String PROJECT_KEY = "projectName";

	protected JTextField name;
	private String objectName;
	private WorkspaceElement elem;
	
	
	/**
	 * Constructor for a new SelectViewNamePage
	 * 
	 * @param defaultName
	 * 		the default name for the object
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public SelectRenamePage(String defaultName, String objectName, WorkspaceElement elem) {
		super(objectName+" name selection");
		this.objectName = objectName;
		this.elem = elem;
		setLayout(new MigLayout());
		
		name = new JTextField(20);
		name.setEditable(true);
		name.setText(defaultName);
		name.setName(ELEMENT_KEY);
		
		add(new JLabel("Select "+objectName+" name "), "gap 10");
		add(name, "h 25!, gap 10, wrap");

	}

    /**
     * 
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
		
		String newName = name.getText().trim();
		ProjectElement project = elem.getProject();
		String categoryType = elem.getParent().getTitle();
		CategoryElement category;
		
		// in any case, a name is required
		if (newName.isEmpty())
			return "Select a name";
		
		if  (categoryType.equals("Sequences")) {
			category = project.getCategory(ViewType.SEQUENCE);
			if (project.viewExists(newName, category))
				return "Name taken - choose new name";
		}	
		else if (categoryType.equals("Arrangements")) {
			category = project.getCategory(ViewType.DOMAINS);
			if (project.viewExists(newName, category))
				return "Name taken - choose new name";
		}
		else {
			category = project.getCategory(ViewType.DOMAINTREE);
			if (project.viewExists(newName, category))
				return "Name taken - choose new name";
		}
		
		return null;
    }
	
 
}
