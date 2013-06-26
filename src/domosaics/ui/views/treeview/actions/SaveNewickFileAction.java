package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import domosaics.model.tree.TreeI;
import domosaics.model.tree.io.NewickWriter;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.view.components.ZoomCompatible;




/**
 * Action exporting a tree view into a newick file
 * 
 * @author Andreas Held
 *
 */
public class SaveNewickFileAction extends AbstractMenuAction implements ZoomCompatible {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		File file = FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), "NWK");
		if (file == null)
			return;

		TreeI tree = view.getTree();
		
		NewickWriter.write(file, tree);
	}

}
