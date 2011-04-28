package angstd.ui.wizards.pages;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.model.workspace.CategoryElement;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.ViewType;
import angstd.ui.views.view.View;
import angstd.ui.views.view.ViewInfo;

/**
 * WizardPage shown within the SelectViewNameDialog.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SelectNamePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String VIEWNAME_KEY = "viewName";
	public static final String PROJECTNAME_KEY = "projectName";

	protected JTextField name;
	private JComboBox selectProject;
	private String objectName;
	
	
	/**
	 * Constructor for a new SelectViewNamePage
	 * 
	 * @param defaultName
	 * 		the default name for the object
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public SelectNamePage(String defaultName, String objectName, ProjectElement project, boolean allowSelection) {
		super(objectName+" name selection");
		this.objectName = objectName;
		setLayout(new MigLayout());
		
		name = new JTextField(20);
		name.setEditable(true);
		name.setText(defaultName);
		name.setName(VIEWNAME_KEY);
		
		ProjectElement[] projectList = WorkspaceManager.getInstance().getProjects();
		String[] projectNames = new String[projectList.length];
		for (int i = 0; i < projectList.length; i++) {
			projectNames[i] = projectList[i].getTitle();
		}
		selectProject = new JComboBox(projectNames);
		selectProject.setName(PROJECTNAME_KEY);
		if (project != null)
			selectProject.setSelectedItem(project.getTitle());
		
		// if we are comming from the data import wizard, 
		// we have already selected a project. To ensure that the association
		// with other views (if selected in wizard) works, we disable the 
		// project selection
		selectProject.setEnabled(allowSelection);
		
		add(new JLabel("Select "+objectName+" name "), "gap 10");
		add(name, "h 25!, gap 10, wrap");
		add(new JLabel("Associate with project"), "gap 10");
		add(selectProject, "h 25!, gap 10, span");

	}

    /**
     * 
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
		
		String newName = name.getText().trim();
		String projectName = (String) selectProject.getSelectedItem();
		ProjectElement project = WorkspaceManager.getInstance().getProject(projectName);
		CategoryElement category;
		
		// in any case, a name is required
		if (newName.isEmpty())
			return "Select a name";
					
		if  (objectName.equals("sequence view")) {
			category = project.getCategory(ViewType.SEQUENCE);
			if (project.viewExists(newName, category))
				return "Name taken - choose new name";
		}	
		else if (objectName.equals("domain view")) {
			category = project.getCategory(ViewType.DOMAINS);
			if (project.viewExists(newName, category))
				return "Name taken - choose new name";
			
		}
		else if (objectName.equals("tree view")) {
			category = project.getCategory(ViewType.TREE);
			if (project.viewExists(newName, category))
				return "Name taken - choose new name";
		}
		
		return null;
    }
	
 
}
