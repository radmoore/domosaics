package angstd.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.treeview.TreeViewI;

/**
 * Action which enables the rendering of a tree ruler showing a
 * distance scale.
 * 
 * @author Andreas Held
 *
 */
public class ShowTreeRulerAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().structuralChange();
	}
}