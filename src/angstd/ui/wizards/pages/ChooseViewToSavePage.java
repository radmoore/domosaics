package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.wizards.GUIComponentFactory;
import angstd.ui.wizards.WizardListCellRenderer;

/**
 * WizardPage allowing user to select view to export
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ChooseViewToSavePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String PROJECT_KEY = "project";
	public static final String VIEW_KEY = "view";
	public static final String EXPORT_NAME = "exportname";
	
	private JComboBox projectSelection, viewSelection;
	private JTextField exportName;
	private ProjectElement project;
	

	/**
	 * Constructor for a new ChooseProjectToSavePage
	 */
	public ChooseViewToSavePage() {
		setLayout(new MigLayout());
		
		// set up the project list
		projectSelection = GUIComponentFactory.createSelectProjectBox(null);
		projectSelection.setName(PROJECT_KEY);
		
		viewSelection = new JComboBox();
		viewSelection.setName(VIEW_KEY);
		
		exportName = new JTextField();
		exportName.setName(EXPORT_NAME);
		
		
		projectSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewSelection.removeAllItems();
				project = (ProjectElement)projectSelection.getSelectedItem();
				List<WorkspaceElement> allViews = new ArrayList<WorkspaceElement>();
				allViews = project.getViews();
				if ( allViews == null || allViews.isEmpty() )
					viewSelection.setEnabled(false);
				else {
					for ( WorkspaceElement elem : allViews )
						viewSelection.addItem(elem);
					
					viewSelection.setSelectedItem(null);
					viewSelection.setRenderer(new WizardListCellRenderer());
					viewSelection.setEnabled(true);
				}
			}
		});
		
		viewSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (viewSelection.getSelectedItem() == null)
					exportName.setText("");
				else {
					ViewElement view = (ViewElement) viewSelection.getSelectedItem();
					exportName.setText(view.getTitle());
				}
			}
		});
		
		
		add(new JLabel("Select a project"), "gap 10, wrap");
		add(projectSelection, "w 270!, gap 10, span, growx, gapright 10, wrap");
		add(new JLabel("Select a view to export"), "gap 10, wrap");
		add(viewSelection, "w 270!, gap 10, span, growx, gapright 10, wrap");
		add(new JLabel("Select a name for export"), "gaptop 10, gap 10, wrap");
		add(exportName, "h 25!, w 270!, gap 10, span, growx, gapright 10, wrap");
		
	}
	
	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Select view for export";
    }
    
    /**
     * Checks if all necessary choices are made.
     */
    protected String validateContents (Component component, Object o) {
    	
    	if (projectSelection.getSelectedItem() == null)
    		return "Please select a project";
    	
    	if (viewSelection.getSelectedItem() == null)
    		return "Please select a view for export";
    	
    	if (exportName.getText().equals(""))
    		return "Please select a name for export";
    	
        return null;
    }

}
