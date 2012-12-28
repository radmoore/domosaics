package angstd.ui.views.treeview.components;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import angstd.ui.AngstdUI;
import angstd.ui.util.AngstdColorPicker;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.actions.CollapseSameArrangementsAtNodeAction;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.actions.context.ChangeFontAction;
import angstd.ui.views.treeview.actions.context.ChangeLabelAction;
import angstd.ui.views.treeview.actions.context.CollapseTreeAction;
import angstd.ui.views.treeview.actions.context.ColorizeAction;
import angstd.ui.views.treeview.actions.context.LookupNodeAtNcbiAction;
import angstd.ui.views.treeview.actions.context.RotateAction;
import angstd.ui.views.treeview.actions.context.SelectPathToRootAction;
import angstd.ui.views.treeview.actions.context.SelectSubtreeAction;
import angstd.ui.views.treeview.actions.context.TakeAsNewOutgroupAction;



/**
 * Context menu for tree node components defining the structure and the
 * supported actions for this pop up menu.
 * 
 * @author Andreas Held
 *
 */
public class NodePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for a new NodePopupMenu
	 * 
	 * @param view
	 * 		the view on which the context menu was triggered
	 */
	public NodePopupMenu(TreeViewI view) {
		super("Node Menu");

		// add title
		String str = "<html><b><i>Node";
		JLabel title = new JLabel(str);
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setVerticalAlignment(JLabel.CENTER);
		add(title);
		add(new JSeparator());
		
		add(new ChangeLabelAction());
		add(new ChangeFontAction(ChangeFontAction.NODE));
		add(new ColorizeAction(AngstdColorPicker.NODE));
		
		add(new JSeparator());
		add(new TakeAsNewOutgroupAction());
		add(new RotateAction());
		add(new CollapseTreeAction());
		
		if (view instanceof DomainTreeViewI && !((DomainTreeViewI) view).getDomainLayoutManager().isCollapseSameArrangements())
			add(new CollapseSameArrangementsAtNodeAction());
		
		add(new JSeparator());
		
		add(new ColorizeAction(AngstdColorPicker.SUBTREE));
		add(new ColorizeAction(AngstdColorPicker.PATH_TO_ROOT));
		add(new ColorizeAction(AngstdColorPicker.PATH_TO_PARENT));
		add(new ColorizeAction(AngstdColorPicker.PATH_TO_CHILDREN));
		
		add(new JSeparator());
		add(new SelectSubtreeAction());
		add(new SelectPathToRootAction());
		
		add(new JSeparator());
		add(new LookupNodeAtNcbiAction());
		
	}
	
}
