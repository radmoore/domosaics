package domosaics.ui.views.treeview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;




/**
 * Action to change a nodes label
 * 
 * @author Andreas Held
 *
 */
public class ChangeLabelAction  extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ChangeLabelAction () {
		super();
		putValue(Action.NAME, "Change Label");
		putValue(Action.SHORT_DESCRIPTION, "Changes label");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// the tree must be loaded otherwise the triggering node pup up menu wouldn't exist
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		// get the nodes label and if its null init it as empty string
		String label = selectedNode.getLabel();
		if(label == null)
			label = "";
		
		// open change label dialog
		Object ret  = 	JOptionPane.showInputDialog(
						DoMosaicsUI.getInstance(), 
						"Change node label", 
						"Node Label", 
						JOptionPane.QUESTION_MESSAGE, 
						null, 
						null, 
						label);
		
		if(ret != null){
			view.getTreeComponentManager().setLabel(selectedNode, ret.toString());
		}	
		
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null);
	}
}