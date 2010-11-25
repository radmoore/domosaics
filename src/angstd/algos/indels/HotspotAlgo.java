package angstd.algos.indels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import angstd.model.arrangement.Domain;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeI;

/**
 * The algorithm finds the node in a tree which is the LCA of a specified domain.
 * 
 * @author Andreas Held
 *
 */
public class HotspotAlgo {

	/**
	 * Finds the node in a tree which is the LCA of a specified domain.
	 * 
	 * @param dom
	 * 		domain where the LCA is wanted
	 * @param tree
	 * 		tree to search in
	 * @return
	 * 		LCA of the domain within the tree
	 */
	public static TreeNodeI findHotspot(Domain dom, TreeI tree) {
		
		// find all children containing the domain
		List<TreeNodeI> leafNodes = new ArrayList<TreeNodeI>();
		Iterator<TreeNodeI> iter = tree.getNodeIterator();
		while (iter.hasNext()) {
			TreeNodeI actNode = iter.next();
			
			if (actNode.getArrangement() != null && actNode.getArrangement().contains(dom))
				leafNodes.add(actNode);
		}
		
		// find the last common ancestor of all leaf nodes
		return tree.findLastCommonAncestor(leafNodes);
	}
}
