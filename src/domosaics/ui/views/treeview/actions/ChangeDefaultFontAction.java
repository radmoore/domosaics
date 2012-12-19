package domosaics.ui.views.treeview.actions;

import java.awt.Font;
import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.JFontChooser;
import domosaics.ui.views.treeview.TreeViewI;


/**
 * Changes the font of a node
 * 
 * @author Andreas Held
 *
 */
public class ChangeDefaultFontAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
    
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		// open change font dialog
		JFontChooser jfc = new JFontChooser(view.getTreeFontManager().getFont());
		
		int ret = jfc.showDialog(DoMosaicsUI.getInstance(), "Angstd Font Chooser");
		if(ret == JFontChooser.OK_OPTION){
			Font newFont = jfc.getFont();
			
			// set DEFAULT FONT
			view.getTreeFontManager().setFont(newFont);
		}
	}

}
