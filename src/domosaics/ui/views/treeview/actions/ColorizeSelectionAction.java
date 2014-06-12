package domosaics.ui.views.treeview.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import domosaics.model.tree.TreeEdgeI;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.DoMosaicsColorPicker;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * Action which colorizes the selected edges.
 * 
 * @author Andreas Held
 *
 */
public class ColorizeSelectionAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
    
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();
		
		// if no edges are selected, we better return
		if (view.getTreeSelectionManager().getSelection().isEmpty()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please select edges first");
			return;
		}
		
		// if the user cancels the selection we want to restore all edge colors so we need to make a backup of them
		Map<TreeEdgeI, Color> oldColors = new HashMap<TreeEdgeI, Color>();
		Color startColor = view.getTreeColorManager().getDefaultEdgeColor();
	
		Collection<TreeEdgeI> selected = view.getTreeSelectionManager().getSelectedEdges();
		for (TreeEdgeI edge : selected) {
			oldColors.put(edge, view.getTreeColorManager().getEdgeColor(edge));
			startColor = view.getTreeColorManager().getEdgeColor(edge);
		}
		
		// let the user pick now a color
		Color newColor = new DoMosaicsColorPicker(DoMosaicsColorPicker.SELECTION, view, startColor).show();

		// if color choosing was canceled restore the old colors 
		if (newColor == null) 
			for (TreeEdgeI actEdge : oldColors.keySet()) 
				view.getTreeColorManager().setEdgeColor(actEdge, oldColors.get(actEdge));
		
		// clear selection
		view.getTreeColorManager().setSelectionColor(view.getTreeColorManager().getDefaultSelectionColor());
		view.getTreeSelectionManager().clearSelection();
	}

}
