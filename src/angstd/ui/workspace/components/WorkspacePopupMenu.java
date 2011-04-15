package angstd.ui.workspace.components;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import angstd.model.workspace.WorkspaceElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.workspace.WorkspaceSelectionManager;
import angstd.ui.workspace.actions.CreateProjectAction;
import angstd.ui.workspace.actions.DeleteElementAction;
import angstd.ui.workspace.actions.ExportViewAction;
import angstd.ui.workspace.actions.RenameElementAction;
import angstd.ui.workspace.actions.ShowViewAction;

/**
 * The workspace context menu filled with actions defined in the 
 * action subpackage. Its triggered on a mouse event caught by 
 * {@link WorkspaceMouseController}
 * 
 * @author Andreas Held
 * 
 *
 */
public class WorkspacePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Basic constructor for the workspace context menu responsible
	 * for initialization of the supported action.
	 */
	public WorkspacePopupMenu() {
		super("Workspace Menu");

		WorkspaceSelectionManager wsm = WorkspaceManager.getInstance().getSelectionManager();
		WorkspaceElement elem = wsm.getSelectedElement();
		
		
		// add title
		String str = "<html><b><i>"+elem.getTitle();
		JLabel title = new JLabel(str);
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setVerticalAlignment(JLabel.CENTER);
		add(title);
		
		add(new JSeparator());
		add(new CreateProjectAction());
		
		add(new JSeparator());
		add(new ShowViewAction());
		
		// Renaming and deleting of default not allowed
		if (! (elem.getTitle().equals("Default Project")) ) {
			add(new RenameElementAction());
			add(new DeleteElementAction());
		}
		
		add(new JSeparator());
		add(new ExportViewAction());
	}
	
}
