package angstd.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.model.tree.TreeNodeI;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.components.DomainComponent;

/** 
 * Action used to trace a domain within the tree to its last common
 * ancestor.
 * 
 * @author Andreas Held
 *
 */
public class TraceDomainAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public TraceDomainAction () {
		super();
		putValue(Action.NAME, "Trace in Tree");
		putValue(Action.SHORT_DESCRIPTION, "Marks the occurences of the domain within the tree");
	}
	
	
	public void actionPerformed(ActionEvent e) {
		DomainTreeViewI view = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();

		if(!view.getDomainTreeLayoutManager().isShowTree()) {
			MessageUtil.showWarning("Please select the \"Show Tree\" menu entry first");
			return;
		}
		
		// get the selected domain from the SelectionManager
		DomainComponent selectedDomain =  view.getDomainSelectionManager().getClickedComp();
		if (selectedDomain == null) 
			return;
		
		// deselct all edges e.g. because the user traced already a domain
		view.getTreeSelectionManager().clearSelection();
		
		// create a list of all nodes containing the domain
		List<TreeNodeI> observedNodes = new ArrayList<TreeNodeI>();
		Iterator<TreeNodeI> iter = view.getDomTree().getNodeIterator();
		while(iter.hasNext()) {
			TreeNodeI node = iter.next();
			
			if (!node.hasArrangement())
				continue;
			
			if (node.getArrangement().contains(selectedDomain.getDomain()))
				observedNodes.add(node);
		}
		
		// find the least common ancestor node
		TreeNodeI lca = view.getDomTree().findLastCommonAncestor(observedNodes);

		// add the subtree to the selection
		for (TreeNodeI child : observedNodes) 
			view.getTreeSelectionManager().addPathToSelection(view.getNodesComponent(lca), view.getNodesComponent(child));
	
		// change selection color into the domain color
		view.getTreeColorManager().setSelectionColor(view.getDomainColorManager().getDomainColor(selectedDomain));
	}
}