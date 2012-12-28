package domosaics.algos.indels;

import java.util.HashMap;
import java.util.Iterator;

import domosaics.algos.align.ConsensusAlignment;
import domosaics.algos.align.nw.NW4DomainsAffine;
import domosaics.model.arrangement.DomainVector;
import domosaics.model.arrangement.GapDomain;
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
public class Dollo extends AbstractReconstructionAlgo {
	
	public Dollo(DomainTreeViewI view, boolean useSets) {
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
	 * reconstruction is done within the dollo_up() method.
	 */
	@Override
    protected String doInBackground() {
		try {
			// calculate the consensus domain vector for the dataset
			domCols = new ConsensusAlignment().align(tree);
			domSeq = domCols.toArrangement();
			
			// do some initialization
			node2da = new HashMap<TreeNodeI, DomainVector>();
			
			Iterator<TreeNodeI> iter = tree.getNodeIterator();
			while (iter.hasNext()) {
				TreeNodeI node = iter.next();
				
				// align each arrangement against the consensus domain vector
				if (node.isLeaf()) {
					DomainVector[] aligned = new NW4DomainsAffine(domSeq, node.getArrangement()).getMatch();
					node2da.put(node, aligned[1]);
				} else // create dummy arrangements for inner nodes
					node2da.put(node, new DomainVector());
			}
			
			for (actCol = 0; actCol < domCols.size() && !isCancelled(); actCol++) {
				TreeNodeI lca = findLCA();
				dollo_up(tree.getRoot()); 
				dollo_down(tree.getRoot(), lca);
			}
				
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
	 * @param parent
	 * 		the node which arrangement is reconstructed.
	 */
	private void dollo_up(TreeNodeI parent) {
		// check from parent to child, so leafs are of no interest
		if (parent.isLeaf()) 
			return;
		
		// bottom up
		for (int i = 0; i < parent.childCount(); i++) 
			dollo_up(parent.getChildAt(i));

		// check all children if domain is present
		boolean toAdd = false;
		for (int i = 0; i < parent.childCount(); i++) {
			DomainVector childDomains = node2da.get(parent.getChildAt(i));
				
			if (childDomains.get(actCol).getID().equals(domCols.get(actCol).getID())) {
				toAdd = true;
				break;
			}
		}
		
		// add the domain then
		if (toAdd)
			node2da.get(parent).add(domCols.get(actCol));
		else
			node2da.get(parent).add(new GapDomain(null));
	}
	
	private void dollo_down(TreeNodeI parent, TreeNodeI lca) {
		// remove domain until lca is reached
		
		// check for lca
		if (parent.equals(lca))
			return;
		
		// don't mess around with leaf nodes
		if (parent.isLeaf()) 
			return;
		
		// switch domain to Gap if the lca is not reached already
		node2da.get(parent).remove(actCol);
		node2da.get(parent).insertElementAt(new GapDomain(null),actCol);

		for (int i = 0; i < parent.childCount(); i++) 
			dollo_down(parent.getChildAt(i), lca);
	}

}