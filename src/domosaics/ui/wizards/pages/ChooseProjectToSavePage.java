package domosaics.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.wizards.GUIComponentFactory;




/**
 * WizardPage allowing the user to choose a project which he wants to 
 * export.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ChooseProjectToSavePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String PROJECT_KEY = "project";
	public static final String FILE_NAME = "filename";
	
	private JList list;
	private JTextField name;
	private ProjectElement elem;
	

	/**
	 * Constructor for a new ChooseProjectToSavePage
	 */
	public ChooseProjectToSavePage(ProjectElement project) {
		setLayout(new MigLayout());
		
		this.elem = project;
		
		// set up the project list
		list = GUIComponentFactory.createProjectList();
		
		list.setName(PROJECT_KEY);
		
		name = new JTextField(25);
		name.setName(FILE_NAME);
		
		// call has been made from context menu of a project node
		// (in workspace)
		if (elem != null) {
			list.setSelectedValue(project, true);
			name.setText(elem.getTitle());
		}
		
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting()) {
					elem = (ProjectElement)list.getSelectedValue();
		          	name.setText(elem.getTitle());   	
		          }
		        }
		      }
		);

		add(new JLabel("Select a project for export"), "gap 10, wrap");
		add(new JScrollPane(list), "gap 10, span, growx, gapright 10, wrap");
		add(new JLabel("Select a name for export"), "gap 10, wrap");
		add(name, "gap 10, h 25!, gapright 10, span, growx");
	}
	
	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Project Selection";
    }
    
    /**
     * Checks if all necessary choices are made.
     */
    protected String validateContents (Component component, Object o) {
    	
    	elem = (ProjectElement)list.getSelectedValue();

    	if (elem == null)
			return "Please select a project";	

		if (name.getText().length() > 25)
			return "Name must not exceed 25 characters";
    		
    	if (name.getText().trim().equals("Default Project"))
    		return "Please choose a different name for export";
    	
    	if (name.getText().isEmpty())
    		return "Please choose a name for the export";
    	
        return null;
    }

}
