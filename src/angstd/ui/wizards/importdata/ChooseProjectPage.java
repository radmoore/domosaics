package angstd.ui.wizards.importdata;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.model.workspace.ProjectElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.wizards.GUIComponentFactory;
import angstd.ui.wizards.WizardManager;

/**
 * WizardPage containing all workspace projects and makes the user choose
 * one.
 * 
 * @author Andreas Held
 *
 */
public class ChooseProjectPage extends WizardPage implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	/** the list displaying the available projects */
	protected JList list;
	
	private String projectName;
	
	/**
	 * Constructor for a new ChooseProjectPage
	 */
	public ChooseProjectPage(String projectName) {
		super("Project Selection");
		
		this.projectName = projectName; // can be null 
		
		setLayout(new MigLayout());
		
		// init the button to create new projects
		JButton createNew = new JButton("Create new Project");
		createNew.addActionListener(this);
		
		// set up the project list
		list = GUIComponentFactory.createProjectList();
		list.setName(ImportDataBranchController.PROJECT_KEY);
		
		// set up the page
		add(new JLabel("Select a project contained in the workspace"), 	"w 400!, gap 10, wrap");
		add(new JScrollPane(list), 		"gap 10, gapright 10, span, growx");
		add(createNew, 					"gap 10");
	}
	
	/**
	 * Method triggered when the create new button was pushed.
	 * Starts the CreateProjectDialog and modifies the project
	 * list afterwards.
	 */
	public void actionPerformed(ActionEvent evt) {
		ProjectElement created = WizardManager.getInstance().showCreateProjectWizard(projectName);
		if (created != null) {
			((DefaultListModel)list.getModel()).addElement(created);
			list.setSelectedValue(created, true);
		}
	}
    
    /**
     * Checks if all necessary choices are made.
     */
    protected String validateContents (Component component, Object o) {
    	if (list.getSelectedValue() == null)
			return "Please select a project";
        return null;
    }

}
