package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * Action which enables the rendering of a tree ruler showing a
 * distance scale.
 * 
 * @author Andreas Held
 *
 */
public class ShowTreeRulerAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		view.getTreeLayoutManager().structuralChange();
	}
}