package angstd.ui.views.treeview.actions;

import java.awt.Font;
import java.awt.event.ActionEvent;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.JFontChooser;
import angstd.ui.views.treeview.TreeViewI;



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
		
		int ret = jfc.showDialog(AngstdUI.getInstance(), "Angstd Font Chooser");
		if(ret == JFontChooser.OK_OPTION){
			Font newFont = jfc.getFont();
			
			// set DEFAULT FONT
			view.getTreeFontManager().setFont(newFont);
		}
	}

}
