package domosaics.ui.workspace.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.workspace.WorkspaceView;




/**
 * Class which handles mouse events on the {@link WorkspaceView}.
 * Basically the context menu opens on right click and on left double click on
 * a view it opens within the mainframe.
 * 
 * @author Andreas Held
 *
 */
public class WorkspaceMouseController extends MouseAdapter {

	/** the workspace view which is controlled */
	protected WorkspaceView view;
	
	/** 
	 * Constructor for the mouse event listener controlling a Workspace view
	 * 
	 * @param view
	 * 		the view which should be listened for mouse activities
	 */
	public WorkspaceMouseController(WorkspaceView view) {
		this.view = view;
	}
	
	public void mouseClicked(MouseEvent me) {
		
		// on right click open the context menu
		if (me.getButton() == MouseEvent.BUTTON3) {	
			int loc = view.getRowForLocation(me.getX(), me.getY());
			if (loc < 0) 
				new WorkspacePopupMenu().show(view, me.getX(), me.getY());
			else if (!view.isRowSelected(loc)) {
				view.setSelectionRow(loc);
				new WorkspacePopupMenu().show(view, me.getX(), me.getY());
			} else if (view.isRowSelected(loc)) 
				new WorkspacePopupMenu().show(view, me.getX(), me.getY());
			return;
		} 
		
		// on double left click show the view within the mainframe
		if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2) {	
			final WorkspaceElement selected = WorkspaceManager.getInstance().getSelectionManager().getSelectedElement();
			
			if (selected == null || !selected.isView()) 
				return;
			
			WorkspaceManager.getInstance().showViewInMainFrame((ViewElement) selected);
			
			me.consume();
			view.clearSelection();
		}
	}
}
