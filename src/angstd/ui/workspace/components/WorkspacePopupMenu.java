package angstd.ui.workspace.components;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import angstd.model.workspace.WorkspaceElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.workspace.WorkspaceSelectionManager;
import angstd.ui.workspace.actions.CreateProjectAction;
import angstd.ui.workspace.actions.DeleteElementAction;
import angstd.ui.workspace.actions.ExportProjectAction;
import angstd.ui.workspace.actions.ExportViewAction;
import angstd.ui.workspace.actions.RenameElementAction;
import angstd.ui.workspace.actions.ShowViewAction;

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
		
		// do nothing on categories
		if (! elem.isCategory()) {
			
			// setup top of context menu (always the same)
			String str = "<html><b>"+elem.getTitle();
			JLabel title = new JLabel(str);
			title.setHorizontalAlignment(JLabel.CENTER);
			title.setVerticalAlignment(JLabel.CENTER);
			add(title);
			add(new JSeparator());
			
			if (elem.isProject())
				addProjectNodeMenu();
			
			else if (elem.isView())
				addViewNodeMenu();
			
		}	

	}
	
	
	/**
	 * Constructes the context menu for project nodes
	 */
	private void addProjectNodeMenu() {
		
		RenameElementAction rea = new RenameElementAction();
		DeleteElementAction dea = new DeleteElementAction();
		ExportProjectAction epa = new ExportProjectAction();
		
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
