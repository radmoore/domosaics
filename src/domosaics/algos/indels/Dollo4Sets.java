package domosaics.algos.indels;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.DomainSet;
import domosaics.model.configuration.Configuration;
import domosaics.model.tree.TreeI;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;


/**
 * The algorithm computes domain events like insertion deletion which 
 * may be happened along a phylogenetic tree. To do so Dollos law
 * is applied meaning that a domain can only gained once but lost
 * several times. 
 * 
 * @author Andreas Held
 *
 */
public class Dollo4Sets extends AbstractReconstructionAlgo {

	/** mapping between a domain and its last common ancestor */
	private Map<DomainFamily, TreeNodeI> doms2lca;
	
	
	public Dollo4Sets(DomainTreeViewI view, boolean useSets) {
		super(view, useSets);
	}
	
	/**
	 * Sets the parameters for the algorithm.
	 * It would be wise to call this method before starting the 
	 * algorithm.
	 * 
	 * @param tree
	 * 		the tree to be evaluated
	 */
	public void setParams(TreeI tree) {
		super.tree = tree;
	}
	
	/**
	 * Method starting the computation of the dollo algorithm. The actual
	 * reconstruction is done within the evalNode() method.
	 */
	@Override
    protected String doInBackground() {
		try { 
			// do some initialization
			doms2lca = new HashMap<DomainFamily, TreeNodeI>();
			node2daSet = new HashMap<TreeNodeI, DomainSet>();
			DomainSet doms = new DomainSet();
			
			Iterator<TreeNodeI> iter = tree.getNodeIterator();
			while (iter.hasNext()) {
				TreeNodeI node = iter.next();
				
				// create dummy arrangements for inner nodes
				node2daSet.put(node, new DomainSet(node.getArrangement()));
				
				// create the alphabet (all domains within the dataset)
				if (node.hasArrangement())
					doms.add(node.getArrangement().getDomains());
			}

			// create a hash map where each domain is assigned to its least common ancestor
			for (int i = 0; i < doms.size() && !isCancelled() ; i++) {
				TreeNodeI lca = HotspotAlgo.findHotspot(doms.get(i), tree);
				node2daSet.get(lca).add(doms.get(i));
				doms2lca.put(doms.get(i).getFamily(), lca);
			}
			
			// bottom up add domains to its parents until lca is found
			evalNode(tree.getRoot());
		}
		catch(Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		
		return null;
    }
	
	/**
	 * Method assigning a node its reconstructed arrangement using 
	 * Dollos law. This recursive method assigns bottom up the 
	 * domains of the child nodes (starting at the leafs) to its
	 * parent until the least common ancestor of the actual domain is 
	 * found. 
	 * 
	 * @param actNode
	 * 		the node which arrangement is reconstructed.
	 */
	private void evalNode(TreeNodeI actNode) {
		// check from parent to child, so leafs are of no interest
		if (actNode.isLeaf()) 
			return;
		
		// add the domains of the child nodes to its parent
		for (int i = 0; i < actNode.childCount(); i++) {
			TreeNodeI child = actNode.getChildAt(i);
			
			evalNode(child);

			for (int j = 0; j < node2daSet.get(child).size(); j++) {
				TreeNodeI lca = doms2lca.get(node2daSet.get(child).get(j).getFamily());
				if (child.equals(lca))
					continue;
				else 
					node2daSet.get(actNode).add(node2daSet.get(child).get(j));
			}
		}
	}

}