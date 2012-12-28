package angstd.ui.views.treeview.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import angstd.model.tree.TreeI;
import angstd.model.tree.io.NewickWriter;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.FileDialogs;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.view.components.ZoomCompatible;



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

		File file = FileDialogs.showSaveDialog(view.getParentPane(), "TREE");
		if (file == null)
			return;

		TreeI tree = view.getTree();
		
		NewickWriter.write(file, tree);
	}

}
