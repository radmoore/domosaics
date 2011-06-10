package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
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
import angstd.ui.views.view.ViewInfo;
import angstd.ui.wizards.GUIComponentFactory;
import angstd.ui.wizards.WizardListCellRenderer;

/**
 * WizardPage shown within the SelectViewNameDialog.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SelectViewPage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW_KEY = "viewName";
	public static final String PROJECT_KEY = "projectName";

	protected JTextField name;
	private JComboBox projectSelection, viewSelection;
	private List<WorkspaceElement> elems;
	private View currentView;
	
	/**
	 * Constructor for a new SelectViewNamePage
	 * 
	 * @param defaultName
	 * 		the default name for the object
	 * @param objectName
	 * 		the object to name e.g. view or project
	 */
	public SelectViewPage(ProjectElement project) {
		super("Select view to add selection to");
		setLayout(new MigLayout());
		
		currentView = ViewHandler.getInstance().getActiveView();
		elems = project.getCategory(currentView.getViewInfo().getType()).getChildren();
				
		// set up the project list (project will be null if view was null)
		projectSelection = GUIComponentFactory.createSelectProjectBox(project);
		projectSelection.setName(PROJECT_KEY);
		
		viewSelection = new JComboBox();
		viewSelection.setName(VIEW_KEY);
		
		if ( elems == null || elems.isEmpty() ) {
			viewSelection.setEnabled(false);
		}
		else {
			for ( WorkspaceElement elem : elems) {
				if ( elem.getTitle().equals(currentView.getViewInfo().getName()) )
					continue;
				viewSelection.addItem(elem);
			}
		}
		
		viewSelection.setSelectedItem(null);
		viewSelection.setRenderer(new WizardListCellRenderer());
		viewSelection.setEnabled(true);
		
		projectSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewSelection.removeAllItems();
				
				//allViews = getViews( (ProjectElement)projectSelection.getSelectedItem() );
				if ( elems == null || elems.isEmpty() )
					viewSelection.setEnabled(false);
				else {
					for ( WorkspaceElement elem : elems) {
//						if ( elem.getTitle().equals(currentView.getViewInfo().getName()) )
//							continue;
						viewSelection.addItem(elem);
					}
					viewSelection.setSelectedItem(null);
					viewSelection.setRenderer(new WizardListCellRenderer());
					viewSelection.setEnabled(true);
				}
			}
		});
		
		
		add(new JLabel("Choose a project"), "gap 10");
		add(projectSelection, "h 25!, gap 5, gapright 10, wrap");
		add(new JLabel("Choose a view"), "gap 10");
		add(viewSelection, "h 25!, gap 5, gapright 10, span");

	}
	


    /**
     * 
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
		
    	return "Strill testing"; 
    	
    	
//		String newName = name.getText().trim();
//		String projectName = (String) selectProject.getSelectedItem();
//		ProjectElement project = WorkspaceManager.getInstance().getProject(projectName);
//		CategoryElement category;
//		
//		// in any case, a name is required
//		if (newName.isEmpty())
//			return "Select a name";
//		
//		if (newName.length()>25)
//			return "Name should not exceed 25 characters";
//					
//		if  (objectName.equals("sequence view")) {
//			category = project.getCategory(ViewType.SEQUENCE);
//			if (project.viewExists(newName, category))
//				return "Name taken - choose new name";
//		}	
//		else if (objectName.equals("domain view")) {
//			category = project.getCategory(ViewType.DOMAINS);
//			if (project.viewExists(newName, category))
//				return "Name taken - choose new name";
//			
//		}
//		else if (objectName.equals("tree view")) {
//			category = project.getCategory(ViewType.TREE);
//			if (project.viewExists(newName, category))
//				return "Name taken - choose new name";
//		}
//		else if (objectName.equals("domain tree view")) {
//			category = project.getCategory(ViewType.DOMAINTREE);
//			if (project.viewExists(newName, category))
//				return "Name taken - choose new name";
//		} 
//		// we are comming from the interpro scan
//		// ensure that arrangement and sequence view names do not
//		// already exist
//		else if  (objectName.equals("annotation")) {
//			CategoryElement sequenceCat = project.getCategory(ViewType.SEQUENCE);
//			category = project.getCategory(ViewType.DOMAINS);
//			if (project.viewExists(newName, category))
//				return "Domain view name taken - choose new name";
//			if (project.viewExists(newName+"_seqs", sequenceCat))
//				return "Sequence view name taken - choose new name";
//		}	
		
//		return null;
    }
	
 
}
