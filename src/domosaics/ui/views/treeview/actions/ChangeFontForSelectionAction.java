package domosaics.ui.views.treeview.actions;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.JFontChooser;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;




/**
 * Changes the font for the selected nodes 
 * 
 * @author Andreas Held
 *
 */
public class ChangeFontForSelectionAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
    
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		if (view.getTreeSelectionManager().getSelection().isEmpty()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please select nodes first");
			return;
		}
		
		// open change font dialog
		JFontChooser jfc = new JFontChooser(view.getTreeFontManager().getFont());
		
		int ret = jfc.showDialog(DoMosaicsUI.getInstance(), "DoMosaics Font Chooser");
		if(ret == JFontChooser.OK_OPTION){
			Font newFont = jfc.getFont();
			
			// for all selected nodes set the font
			Iterator<NodeComponent> selection = view.getTreeSelectionManager().getSelectionIterator();
			while(selection.hasNext()) 
				view.getTreeFontManager().setFont(selection.next(), newFont);
		}	
	}

}
