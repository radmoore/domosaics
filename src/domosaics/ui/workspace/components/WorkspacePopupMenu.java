package domosaics.ui.workspace.components;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.actions.ImportViewAction;
import domosaics.ui.workspace.WorkspaceSelectionManager;
import domosaics.ui.workspace.actions.DeleteElementAction;
import domosaics.ui.workspace.actions.ExportProjectAction;
import domosaics.ui.workspace.actions.ExportViewAction;
import domosaics.ui.workspace.actions.RenameElementAction;
import domosaics.ui.workspace.actions.ShowViewAction;




/**
 * The workspace context menu containing actions defined in  
 * action subpackage. Its triggered on a mouse event caught by 
 * {@link WorkspaceMouseController}
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * 
 * 
 *
 */
public class WorkspacePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	// the selected element
	private WorkspaceElement elem;
	
	/**
	 * Basic constructor for the workspace context menu responsible
	 * for initialization of supported actions
	 */
	public WorkspacePopupMenu() {
		super("Workspace Menu");

		WorkspaceSelectionManager wsm = WorkspaceManager.getInstance().getSelectionManager();
		this.elem = wsm.getSelectedElement();
		
		
		if (! elem.isCategory()) {
			
			// setup top of context menu (always the same)
			String str = "<html><b>"+elem.getTitle();
			JLabel title = new JLabel(str);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setVerticalAlignment(SwingConstants.CENTER);
			add(title);
			add(new JSeparator());
			
			if (elem.isProject())
				addProjectNodeMenu();
			
			else if (elem.isView())
				addViewNodeMenu();
			
		}
		// only allow deletion on category
		else {
			add(new DeleteElementAction());
		}

	}
	
	
	/**
	 * Constructes the context menu for project nodes
	 */
	private void addProjectNodeMenu() {
		
		ProjectElement project = elem.getProject();
		
		RenameElementAction rea = new RenameElementAction();
		DeleteElementAction dea = new DeleteElementAction();
		ExportProjectAction epa = new ExportProjectAction(project);
		ImportViewAction iva = new ImportViewAction();
		iva.setProject(project);
		
		// Renaming and deleting of default not allowed
		if ( elem.getTitle().equals("Default Project") ) {
			rea.setEnabled(false);
			dea.setEnabled(false);
		}
		
		add(rea);
		add(dea);
		
		// no need to export an empty project
		if (elem.getChildCount() < 1) 
			epa.setEnabled(false);
		
		add(epa);
		add(iva);
	}
	
	/**
	 * Constructes the context menu for view nodes
	 */
	private void addViewNodeMenu() {
		add(new ShowViewAction());
		add(new RenameElementAction());
		add(new DeleteElementAction());
		add(new ExportViewAction());
	}
	
	
}
