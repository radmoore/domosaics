package domosaics.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.view.AbstractView;




/**
 * WizardPage shown within the SelectViewNameDialog.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ImportViewPage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String FILEPATH_KEY = "filePath";
	public static final String VIEWNAME_KEY = "viewName";
	public static final String PROJECTNAME_KEY = "projectName";
	public static final String VIEWTYPE_KEY = "category";

	private JTextField name, path, typeField;
	private JComboBox selectProject;
	private JButton browse;

    /* alphanum chars, at least one (TODO: unicode?) */
	private String alphaNumPattern = "^[a-zA-Z0-9_-]*$";
	
	private ViewType type=null;
	
	
	/**
	 * Constructor for a new SelectViewNamePage
	 * 
	 * @param defaultName
	 * 		the default name for the object
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public ImportViewPage(ProjectElement project) {
		super("Import view to project");
		setLayout(new MigLayout());
		
		browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*type=null;
				typeField.setText("");*/
				File file = FileDialogs.showOpenDialog(DoMosaicsUI.getInstance());
				if(file != null) {
					if (file.canRead()) {
						path.setText(file.getAbsolutePath());
						File viewFile = new File(path.getText());
						//type = ViewImporter.detectViewType(viewFile);
						// Nic import
						String nameAndType = AbstractView.detectViewType(viewFile);
						if(nameAndType==null) {
							MessageUtil.showWarning(DoMosaicsUI.getInstance(), file.getName()+ " does not appear to be a DoMosaics file.");
							path.setText("");
							name.setName("");
						} else
						{
							if (nameAndType.substring(nameAndType.indexOf("###")+3).equals("SEQUENCES"))
								type=ViewType.SEQUENCE;

							if (nameAndType.substring(nameAndType.indexOf("###")+3).equals("DOMAIN_TREE"))
								type=ViewType.DOMAINTREE;

							if (nameAndType.substring(nameAndType.indexOf("###")+3).equals("TREE"))
								type=ViewType.TREE;

							if (nameAndType.substring(nameAndType.indexOf("###")+3).equals("ARRANGEMENTS"))
								type=ViewType.DOMAINS;
							if(type==null) {
								MessageUtil.showWarning(DoMosaicsUI.getInstance(), file.getName()+ " does not appear to be a DoMosaics file.");
								path.setText("");
								name.setName("");
							} else
							{
								name.setText(nameAndType.substring(0, nameAndType.indexOf("###")));
								typeField.setText(type.toString());
							}
						}
					}
					else {
						MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Cannot read "+file.getName());
						path.setText("");
					}
				}
			}
		});
		
		path = new JTextField();
		path.setEditable(false);
		path.setName(FILEPATH_KEY);
		
		name = new JTextField();
		name.setEditable(true);
		name.setName(VIEWNAME_KEY);
		
		typeField = new JTextField();
		typeField.setEditable(false);
		typeField.setName(VIEWTYPE_KEY);
		
		// use GUIFactory?
		ProjectElement[] projectList = WorkspaceManager.getInstance().getProjects();
		String[] projectNames = new String[projectList.length];
		for (int i = 0; i < projectList.length; i++) {
			projectNames[i] = projectList[i].getTitle();
		}
		selectProject = new JComboBox(projectNames);
		selectProject.setName(PROJECTNAME_KEY);
		if (project != null)
			selectProject.setSelectedItem(project.getTitle());
		else
			selectProject.setSelectedItem(null);
		
		add(new JXTitledSeparator("Select view file"), "growx, span, wrap");
		add(browse, "gapleft 5");
		add(path, "w 165!, h 25!, span 2, gapright 10, wrap");
		add(new JLabel("View name:"), "gapleft 5");
		add(name, "h 25!, w 165!, gapright 10, wrap");
		add(new JLabel("View type:"), "gapleft 5");
		add(typeField, "h 25!, w 165!, gapright 10, wrap");

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
		
		if ( !newName.matches(alphaNumPattern) )
			return "Invalid name (numbers, - or _ allowed)";
		
		if (newName.length() > 50)
			return "View name should not exceed 50 characters";
		
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

