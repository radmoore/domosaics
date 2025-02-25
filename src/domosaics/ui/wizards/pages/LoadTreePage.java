package domosaics.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.model.tree.TreeI;
import domosaics.model.tree.io.NewickTreeReader;
import domosaics.model.tree.io.NexusTreeReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;




/**
 * WizardPage shown within the SelectViewNameDialog.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class LoadTreePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String FILEPATH_KEY = "filePath";
	public static final String VIEWNAME_KEY = "viewName";
	public static final String PROJECTNAME_KEY = "projectName";

	private JTextField name, path;
	private JComboBox selectProject;
	private JButton browse;
	
	private ViewType type=ViewType.TREE;

    /* alphanum chars, at least one (TODO: unicode?) */
	private String alphaNumPattern = "^[a-zA-Z0-9_-]*$";
	
	/** the check for correct fasta format */
	protected TreeI tree = null;
	
	/**
	 * Constructor for a new SelectViewNamePage
	 * 
	 * @param defaultName
	 * 		the default name for the object
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public LoadTreePage() {
		super("Load tree to project");
		setLayout(new MigLayout());
		
		browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = FileDialogs.showOpenDialog(DoMosaicsUI.getInstance());
				if(file != null) {
					if (file.canRead()) {
						File test = new File(file.getAbsolutePath());
						BufferedReader in = null;
						String firstLine = "";
						try {
							in = new BufferedReader(new FileReader(test));
							firstLine=in.readLine();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						if(firstLine.toUpperCase().equals("#NEXUS"))
							tree = new NexusTreeReader().getTreeFromFile(test);
						else
							tree = new NewickTreeReader().getTreeFromFile(test);
					}
					if(tree != null) {
						path.setText(file.getAbsolutePath());
						name.setText(file.getName().split("\\.")[0]);
					} else {
						MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Cannot read tree in "+file.getName());
					}
				}
				else {
					MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Cannot read "+file.getName());
					path.setText("");
				}
			}
		});
		
		path = new JTextField();
		path.setEditable(false);
		path.setName(FILEPATH_KEY);
		
		name = new JTextField();
		name.setEditable(true);
		name.setName(VIEWNAME_KEY);
		
		// use GUIFactory?
		ProjectElement[] projectList = WorkspaceManager.getInstance().getProjects();
		String[] projectNames = new String[projectList.length];
		for (int i = 0; i < projectList.length; i++) {
			projectNames[i] = projectList[i].getTitle();
		}
		selectProject = new JComboBox(projectNames);
		selectProject.setName(PROJECTNAME_KEY);
		selectProject.setSelectedItem(null);
		
		add(new JXTitledSeparator("Select view file"), "growx, span, wrap");
		add(browse, "gapleft 5");
		add(path, "w 165!, h 25!, span 2, gapright 10, wrap");
		add(new JLabel("View name:"), "gapleft 5");
		add(name, "h 25!, w 165!, gapright 10, wrap");

		add(new JXTitledSeparator("Associate with project"), "growx, span, wrap, gaptop 15");
		add(selectProject, "h 25!, w 265!, gapright 10, span, wrap");
		
	}
	
	
    /**
     * 
     * Checks if all necessary inputs are made.
     */
    @Override
	protected String validateContents (Component component, Object o) {
		
		String newName = name.getText().trim();
		
		// check for chosen view path
		if (path.getText().equals(""))
			return "Select a view for import";
		
		if (newName.equals(""))
			return "Please choose a name for view";
		
		if (newName.length() > 50)
			return "View name should not exceed 50 characters";
		
		if ( !newName.matches(alphaNumPattern) )
			return "Invalid name (numbers, - or _ allowed)";
		
		// ensure that project is selected
		if (selectProject.getSelectedItem() == null)
			return "Please choose a project for association";
		
		// get project
		String projectName = (String) selectProject.getSelectedItem();
		ProjectElement project = WorkspaceManager.getInstance().getProject(projectName);
			
		// ensure that name does not exist
		if (project.viewExists(newName, project.getCategory(type)))
			return "Name taken - choose new name";

		return null;
	}


}

