package angstd.ui.views.treeview.actions;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.JFontChooser;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;

/**
 * Changes the font for the selected nodes 
 * 
 * @author Andreas Held
 *
 */
public class ChangeFontForSelectionAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
    
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		if (view.getTreeSelectionManager().getSelection().isEmpty()) {
			MessageUtil.showWarning("Please select nodes first");
			return;
		}
		
		// open change font dialog
		JFontChooser jfc = new JFontChooser(view.getTreeFontManager().getFont());
		
		int ret = jfc.showDialog(AngstdUI.getInstance(), "Angstd Font Chooser");
		if(ret == JFontChooser.OK_OPTION){
			Font newFont = jfc.getFont();
			
			// for all selected nodes set the font
			Iterator<NodeComponent> selection = view.getTreeSelectionManager().getSelectionIterator();
			while(selection.hasNext()) 
				view.getTreeFontManager().setFont(selection.next(), newFont);
		}	
	}

}
