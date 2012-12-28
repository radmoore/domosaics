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
import angstd.ui.views.view.View;
import angstd.ui.wizards.GUIComponentFactory;
import angstd.ui.wizards.WizardListCellRenderer;



/**
 * WizardPage shown within the SelectViewDialog.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SelectViewPage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW_KEY = "view";
	public static final String PROJECT_KEY = "projectName";

	protected JTextField name;
	private JComboBox projectSelection, viewSelection;
	private List<WorkspaceElement> elems;
	private View currentView;
	
	/**
	 * Constructor for a new SelectViewPage
	 * 
	 * @param project
	 * 		the currently active project
	 * @param selectedElems
	 * 		the number of elements to be merged into an existing view
	 */
	public SelectViewPage(ProjectElement project, int selectedElems) {
		super("Export "+ selectedElems + " items to existing view");
		setLayout(new MigLayout());
		
		currentView = ViewHandler.getInstance().getActiveView();
		elems = project.getCategory(currentView.getViewInfo().getType()).getChildren();
		
		projectSelection = GUIComponentFactory.createSelectProjectBox(project);
		projectSelection.setName(PROJECT_KEY);
		
		viewSelection = new JComboBox();
		viewSelection.setName(VIEW_KEY);
		
		if ( elems == null )
			viewSelection.setEnabled(false);
		
		else {
			for ( WorkspaceElement elem : elems ) {
				if ( elem.getTitle().equals(currentView.getViewInfo().getName()) )
					continue;
				viewSelection.addItem(elem);
			}
		}
		if (viewSelection.getItemCount() == 0) 
			viewSelection.setEnabled(false);
		
		viewSelection.setSelectedItem(null);
		viewSelection.setRenderer(new WizardListCellRenderer());
		
		projectSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewSelection.removeAllItems();
				viewSelection.setEnabled(true);
				ProjectElement selectedProject = (ProjectElement) projectSelection.getSelectedItem();
				
				CategoryElement cat = selectedProject.getCategory(currentView.getViewInfo().getType());
				// project may not have category
				if (cat == null)
					viewSelection.setEnabled(false);
				else {
					elems = cat.getChildren();
					if ( elems == null || elems.isEmpty() )
						viewSelection.setEnabled(false);
					else {
						for ( WorkspaceElement elem : elems) {
							// skip adding to current view
							if ( elem.getTitle().equals(currentView.getViewInfo().getName()) )
								continue;
							viewSelection.addItem(elem);
						}
						if (viewSelection.getItemCount() == 0) 
							viewSelection.setEnabled(false);
						
						viewSelection.setSelectedItem(null);
						viewSelection.setRenderer(new WizardListCellRenderer());
					}
				}
			}
		});
		
		add(new JLabel("Choose a project"), "gap 10");
		add(projectSelection, "h 25!, w 250!, gap 5, gapright 10, wrap");
		add(new JLabel("Choose a view"), "gap 10");
		add(viewSelection, "h 25!, w 250!, gap 5, gapright 10, span");
		
	}
	

    /**
     * 
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
		
    	ViewElement selectedView = (ViewElement) viewSelection.getSelectedItem();
    	
    	if (selectedView == null)
    		return "Please select a view to add selection to";
		
		return null;
    }
	
 
}
