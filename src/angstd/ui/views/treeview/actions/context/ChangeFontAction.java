package angstd.ui.views.treeview.actions.context;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.util.JFontChooser;
import angstd.ui.views.treeview.TreeViewI;

/**
 * Changes the font for a node
 * 
 * @author Andreas Held
 *
 */
public class ChangeFontAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	/** mode that the font is changed for the selected node */
	public static final int NODE = 0;
	
	/** mode that the default font is changed */
	public static final int DEFAULT = 1;
	
	/** the change type */
	protected int type;
	
	
	public ChangeFontAction (int type) {
		super();
		this.type = type;
		putValue(Action.NAME, "Change Font");
		putValue(Action.SHORT_DESCRIPTION, "Changes the Font");
	}

	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();

		// open change font dialog
		JFontChooser jfc = new JFontChooser(view.getTreeFontManager().getFont(view.getTreeSelectionManager().getClickedComp()));
		
		int ret = jfc.showDialog(AngstdUI.getInstance(), "Angstd Font Chooser");
		if(ret == JFontChooser.OK_OPTION){
			Font newFont = jfc.getFont();
			
			if (type == NODE)
				view.getTreeFontManager().setFont(view.getTreeSelectionManager().getClickedComp(), newFont);
			
			if (type == DEFAULT)
				view.getTreeFontManager().setFont(newFont);
		}	

		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null);
	}
}