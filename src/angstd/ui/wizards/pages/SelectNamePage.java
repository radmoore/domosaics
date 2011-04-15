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
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.ViewType;
import angstd.ui.views.view.View;
import angstd.ui.workspace.WorkspaceSelectionManager;

/**
 * WizardPage shown within the SelectViewNameDialog.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SelectNamePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String ELEMENT_KEY = "viewName";

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
		add(name, "h 25!, gap 10");
	}

    /**
     * TODO
     * Checks if all necessary inputs are made.
     * Selection can be one of the following:
     * 
     * - selection in workspace tree
     * - active view
     * - selection in workspace tree and active view
     * - no selection at all
     * => selection in view overrides selection in workspace tree
     * 
     * Selection can also come from multiple spots
     * - from arrangement (arrangement subselection) (-> view)
     * - from arrangement (select sequences) (-> view)
     * - project creation (-> project)
     * - ...?
     */
    protected String validateContents (Component component, Object o) {
		
		String newName = name.getText().trim();
		
		// in any case, a name is required
		if (newName.isEmpty())
			return "Select a name";
		
		// we are naming or renaming a project element
		if (objectName.equals("project")) {
			if (newName.equals("Default Project"))
				return "Default Project name disallowed";
			if (WorkspaceManager.getInstance().projectExists(newName))
				return "Name taken - choose new name";
		}
		// we are dealing with a view element
		else {

			// get some info about active project and view
//			WorkspaceSelectionManager wsm = WorkspaceManager.getInstance().getSelectionManager();
//			ProjectElement project1 = wsm.getSelectedProject();
//			WorkspaceElement wse = wsm.getSelectedElement();
			View view = ViewHandler.getInstance().getActiveView();
			ViewElement elem = WorkspaceManager.getInstance().getViewElement(view.getViewInfo());
			ProjectElement project = elem.getProject();
			CategoryElement category = null;
			
			if  (objectName.equals("sequence view")) {
		
				category = project.getCategory(ViewType.SEQUENCE);
				if (project.viewExists(newName, category)){
					return "Name taken - choose new name";
				}
			}
			else if (objectName.equals("domain view")) {
				category = project.getCategory(ViewType.DOMAINS);
				if (project.viewExists(newName, category)){
					return "Name taken - choose new name";
				}
			}
		}
		
		return null;
    }
	
 
}
