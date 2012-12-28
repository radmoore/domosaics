package angstd.algos.indels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainSet;
import angstd.model.arrangement.DomainVector;
import angstd.model.arrangement.GapDomain;
import angstd.model.domainevent.DomainEvent;
import angstd.model.domainevent.DomainEventI;
import angstd.model.tree.TreeEdgeI;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.manager.InnerNodeArrangementManager;



public abstract class AbstractReconstructionAlgo extends SwingWorker<String, Void> {

	/** the tree which nodes are being reconstructed */
	protected TreeI tree;
	
	/** mapping between nodes and their reconstructed inner states used to assign the domain events */
	protected Map<TreeNodeI, DomainVector> node2da;
	
	// only used when working with domain sets instead of vectors
	protected Map<TreeNodeI, DomainSet> node2daSet;
	
	protected DomainVector domCols; 
	protected DomainArrangement domSeq; 
	protected int actCol = 0;
	
	protected DomainTreeViewI view;
	
	protected boolean useSets;
	
	protected int inCount, delCount; 
	
	
	public AbstractReconstructionAlgo(DomainTreeViewI view, boolean useSets) {
		this.view = view;
		this.useSets = useSets;
	}
	
	public int getInCount() {
		return inCount;
	}
	
	public int getDelCount() {
		return delCount;
	}
	
//	public int[] getEvtCount() {
//		int[] res = new int[2];
//		Iterator<TreeNodeI> iter = tree.getNodeIterator();
//		while(iter.hasNext()) {
//			TreeEdgeI e = iter.next().getEdgeToParent();
//			if (e == null)
//				continue;
//			
//			if (!e.hasDomainEvent())
//				continue;
//			
//			for (DomainEventI evt : e.getDomainEvents())
//				if (evt.isInsertion())
//					res[0]++;
//				else if (evt.isDeletion()) 
//					res[1]++;
//		}
//		return res;
//	}
	
	/**
	 * Should implement the actual computation of the algorithm.
	 */
	protected abstract String doInBackground();
	
	/**
	 * Method being triggered when the algorithm finished. If the 
	 * algorithm was canceled it does nothing at all. 
	 * Else the domain events are assigned to the tree edges.
	 */
	@Override
    protected void done() {
		if (isCancelled() || tree == null) {
			return;
     	}
		setProgress(99);
		
		if (useSets) 
			reconstructAncestralArrangements4Sets();
		else 
			reconstructAncestralArrangements();
		
		removeAllEvents(view);
		
		inCount = 0;
		delCount = 0;
		traverseAddInDelToEdges(tree.getRoot());

     	setProgress(100);
    }
	
	private void reconstructAncestralArrangements() {
		InnerNodeArrangementManager manager = view.getInnerNodeArrangementManager();
		manager.reset();
		
		for (TreeNodeI node : node2da.keySet()) {
			DomainVector vec = node2da.get(node);
			DomainVector newDA = new DomainVector();
			
			// remove all gaps
			for (Domain dom : vec)
				if (!(dom instanceof GapDomain))
					newDA.add(dom);
			
			manager.storeDA(node, newDA.toArrangement());
		}
	}
	
	private void reconstructAncestralArrangements4Sets() {
		InnerNodeArrangementManager manager = view.getInnerNodeArrangementManager();
		manager.reset();
		
		for (TreeNodeI node : node2daSet.keySet()) {
			DomainSet set = node2daSet.get(node);
			DomainVector newDA = new DomainVector();
			
			// remove all gaps
			for (int i = 0; i < set.size(); i++)
				if (!(set.get(i) instanceof GapDomain))
					newDA.add(set.get(i));
			
			manager.storeDA(node, newDA.toArrangement());
		}
	}
	
	public static void removeAllEvents(DomainTreeViewI view) {
		view.getDomainEventComponentManager().clear();
		
     	Iterator<TreeNodeI> iter = view.getDomTree().getNodeIterator();
		while(iter.hasNext()) {
			TreeNodeI node = iter.next();
		
			// first delete all domain events already assigned to the edges
			TreeEdgeI edge = node.getEdgeToParent();
			if (edge != null && edge.hasDomainEvent())
				edge.removeDomainEvenets();
		}
	}
	
	/**
	 * Recursive helper method assigning the domain events to the tree 
	 * edges. This method should be used to start the assignment 
	 * process by calling it with the root node.
	 * 
	 * @param root
	 * 		the trees root node
	 */
	private void traverseAddInDelToEdges(TreeNodeI root) {
		for (int i = 0; i < root.childCount(); i++) {
			TreeEdgeI edge = root.getEdgeToChild(i);
			TreeNodeI child = edge.getTarget();
			if (useSets)
				addIndels4Sets(root, child, edge);
			else
				addInDels(root, child, edge);
			traverseAddInDelToEdges(child);
		}
	}
	
	private void addIndels4Sets(TreeNodeI parent, TreeNodeI child, TreeEdgeI edge) {
		// check if insertion or deletion occur
		if (node2daSet.get(parent).equals(node2daSet.get(child)))
			return;
		
		DomainSet deletions = node2daSet.get(parent).except(node2daSet.get(child));
		for (int i = 0; i < deletions.size(); i++) {
			edge.addDomainEvent(new DomainEvent(DomainEvent.DELETION, edge, deletions.get(i)));
			delCount++;
		}
		
		// check for insertions
		DomainSet insertions = node2daSet.get(child).except(node2daSet.get(parent));
		for (int i = 0; i < insertions.size(); i++) {
			edge.addDomainEvent(new DomainEvent(DomainEvent.INSERTION, edge, insertions.get(i)));
			inCount++;
		}
	}
	
	/**
	 * Helper method which does the assignment of domain events to tree
	 * edges. Called by traverseAddInDelToEdges().
	 *  
	 * @param parent
	 * 		the actual parent node
	 * @param child
	 * 		the actual child node
	 * @param edge
	 * 		the actual tree edge
	 */
	private void addInDels(TreeNodeI parent, TreeNodeI child, TreeEdgeI edge) {
		// check if insertion or deletion occur
		if (node2da.get(parent).equals(node2da.get(child)))
			return;
		
		DomainVector p = node2da.get(parent);
		DomainVector c = node2da.get(child);
		
		for (int i = 0; i < domCols.size(); i++) {
			if (p.get(i) instanceof GapDomain && c.get(i) instanceof GapDomain)
				continue;
			if (p.get(i) instanceof GapDomain) {
				edge.addDomainEvent(new DomainEvent(DomainEvent.INSERTION, edge, c.get(i)));
				inCount++;
			}
			if (c.get(i) instanceof GapDomain) {
				edge.addDomainEvent(new DomainEvent(DomainEvent.DELETION, edge, p.get(i)));
				delCount++;
			}
		}
	}
	
	// helper method to find the lca based on a domain alignment
	protected TreeNodeI findLCA() {
		// find all children containing the domain
		List<TreeNodeI> leafNodes = new ArrayList<TreeNodeI>();
		Iterator<TreeNodeI> iter = tree.getNodeIterator();
		while (iter.hasNext()) {
			TreeNodeI actNode = iter.next();
			
			if (!actNode.isLeaf())
				continue;
			
			if (!(node2da.get(actNode).get(actCol) instanceof GapDomain))
				leafNodes.add(actNode);
		}
		
		// find the last common ancestor of all leaf nodes
		return tree.findLastCommonAncestor(leafNodes);
	}

}
