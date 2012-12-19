package domosaics.model.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;


/**
 * see {@link TreeI}
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class Tree implements TreeI {
	
	/** root node */
	protected TreeNodeI root;
	
	/** maximal depth from root to leave */
	protected int maxDepth;
	
	/** list of all nodes stored in the graph */
	protected List<TreeNodeI> nodes;
	
	/** list of all edges occurring within the graph */
	protected List<TreeEdgeI> edges;
	
	
	public void addNode(TreeNodeI node) {
		nodes.add(node);
	}
	
	public void addEdge(TreeEdgeI edge) {
		addEdge(edge.getSource(), edge.getTarget(), edge); //edges.add(edge);
	}
	
	/**
	 * Method which is called when a weightened or unweightened edge was added
	 * to the graph. The edge is added to the parent node as well as to the
	 * child node. 
	 * 
	 * @param parent 
	 * 		node of the edge
	 * @param child  
	 * 		node of the edge
	 * @param edge 
	 * 		edge to add
	 */
	private void addEdge(TreeNodeI parent, TreeNodeI child, TreeEdgeI edge) {
		parent.addEdge(edge); 
		child.addEdge(edge);
		edges.add(edge);
	}
	
	public void removeNode(TreeNodeI node) {
		if(node == null) 
			return;
		if (nodes.contains(node))
			nodes.remove(node);
	}

	public Iterator<TreeNodeI> getNodeIterator() {
		return nodes.iterator();
	}
	
	public int nodeCount () {
		return nodes.size();
	}
	
	/**
	 * Constructor for a new empty tree.
	 */
	public Tree() {
		nodes = new ArrayList<TreeNodeI>();
		edges = new ArrayList<TreeEdgeI>();
		maxDepth = -1;
	}
	
	public void setRoot (TreeNodeI root) {
		this.root = root;
	}

	public TreeNodeI getRoot() {
		return this.root;
	}
	
	public int countLeaves() {
		int leaves = 0;
		Iterator<TreeNodeI> iter = getNodeIterator();
		while (iter.hasNext())
			if (iter.next().isLeaf())
				leaves++;
		return leaves;
	}
	
	public int getMaxDepth() {
		if (root == null) 
			return -1;
	
		if (maxDepth == -1)  
			traverse(root, 0); //	calculate maximal depth from root to leaf
		
		return maxDepth;
	}
	
	/**
	 * Calculates the maximal depth from root to leaf.
	 * 
	 * @param node
	 * 		start node from where the traversing calculation starts.
	 * @param actDepth
	 * 		actual depth within the tree
	 */
	private void traverse(TreeNodeI node, int actDepth) {
		if (actDepth > maxDepth)
			maxDepth = actDepth;
		
		if (!node.isLeaf()) {
			for (int c = 0; c < node.childCount(); c++)
				traverse((TreeNodeI) node.getChildAt(c), actDepth+1);
		}
	}
	
	/**
	 * Its possible to retrieve bootstrap values from the Newick format directly. 
	 * Therefore numeric values for inner labels, are taken as such values.
	 * This method initializes the bootstrap values for edges, based on
	 * the node labels.
	 */
	public void initBootstrapValues() {
		Iterator<TreeNodeI> iter = getNodeIterator();
		while (iter.hasNext()) {
			TreeNodeI node = iter.next();
			try {
				double bootstrap = Double.parseDouble(node.getLabel());
				if (bootstrap >= 0 && bootstrap <= 100 && node.getEdgeToParent() != null) {
					node.setLabel("");
					((TreeEdgeI) node.getEdgeToParent()).setBootstrap(bootstrap);
				}
			} catch( NumberFormatException nfe) {
				Configuration.getLogger().debug(nfe.toString());
			}
		}
	}
	
	/**
	 * This method returns the last common ancestor of two nodes of a tree.
	 * <p>
	 * This eventually returns null if one of the given nodes is null or the given nodes
	 * are not in this tree.
	 * 
	 */	
	public TreeNodeI findLastCommonAncestor(TreeNodeI nodeA, TreeNodeI nodeB) {
		if(nodeA == null || nodeB == null) 
			return null;
		
		/* set nodeA and nodeB to equal level */
		if (nodeA.getLevel() < nodeB.getLevel()) {
			while (nodeA.getLevel() != nodeB.getLevel()) {
				nodeB = (TreeNode) nodeB.getParent();

			}
		} else {			
			while (nodeA.getLevel() != nodeB.getLevel()) {
				nodeA = (TreeNode) nodeA.getParent();
			}
		}
		
		/* set both nodes to their parent, until they are equal */
		while (nodeA != nodeB) {
			nodeA = (TreeNode) nodeA.getParent();
			nodeB = (TreeNode) nodeB.getParent();
		}
		
		return nodeA;
	}
    
	public TreeNodeI findLastCommonAncestor(List<TreeNodeI> nodes) {
		if(nodes == null) 
			return null;
		
		if(nodes.size() == 0) 
			return null;
		
		if(nodes.size() == 1) 
			return nodes.get(0);
		
		TreeNodeI retVal = findLastCommonAncestor(nodes.get(0), nodes.get(1));
		
		for (int i = 2; i < nodes.size(); i++) 
		  retVal = findLastCommonAncestor(retVal, nodes.get(i));
		
		return (retVal);	
	  }


	public void reRoot(TreeNodeI outgroup) {
		// if the new root and the actual root are the same, we are done already
		if (getRoot().equals(outgroup))
			return;
		
		// if the parent of the new outgroup is the actual root, quit here
		if (outgroup.getParent().equals(getRoot())) 
			return;
		
		// add a new rootnode to the tree
		TreeNodeI root = new TreeNode(0);
		addNode(root);
		
		TreeNodeI parent = root;
		TreeNodeI secondChild = outgroup.getParent();
		TreeNodeI newChild = secondChild.getParent();
		
		addEdge(new TreeEdge(root, outgroup)); 
		
		addEdge(new TreeEdge(parent, secondChild));
		secondChild.removeEdge(secondChild.getEdgeToChild(outgroup));
		while(newChild != null) {
			parent = secondChild;
			secondChild = newChild;
			newChild = secondChild.getParent();
			
			addEdge(new TreeEdge(parent, secondChild));
			secondChild.removeEdge(secondChild.getEdgeToChild(parent));
		}
		
		if (newChild == null) {
			parent.removeEdge(parent.getEdgeToChild(secondChild));
			addEdge(new TreeEdge(parent, (TreeNodeI) secondChild.getChildAt(0)));
			removeNode(secondChild);
		}
		
		setRoot(root);
		maxDepth = -1;
	}
	
	public List<TreeNodeI> deleteNode(TreeNodeI node) {
		List<TreeNodeI> deleted = new ArrayList<TreeNodeI>();
		
		// ROOT NODE (but only if it has exact one child. So the child will replace the root node
		if (getRoot().equals(node)) {
			if (node.childCount() != 1)
				return deleted;
			TreeNodeI child = node.getChildAt(0);
			child.removeEdge(child.getEdgeToParent());
			removeNode(node);
			deleted.add(node);
			setRoot(child);	
			return deleted;
		}
			
		// LEAF NODE
		if (node.isLeaf()) {
			TreeNodeI parent = node.getParent();
			
			// check if it is a leaf of the root because we dont want to delete them
			if (getRoot().equals(parent))
				return deleted;
			
			parent.removeEdge(parent.getEdgeToChild(node));
			removeNode(node);
			deleted.add(node);
			
			/*
			 *  if parent has now just one child left: 
			 *  replace the node by this child and add 
			 *  the edge weights if there are any
			 */
			if (parent.childCount() > 1) 
				return deleted;
			
			if (parent.childCount() == 1) {
				TreeNodeI child = parent.getChildAt(0); 	// thats the other leaf
				TreeNodeI newParent = parent.getParent();	// and the new parent for this leaf

				double weightNew2OldParent = newParent.getEdgeToChild(parent).getWeight();
				double weightParent2Child = parent.getEdgeToChild(child).getWeight();
				double newWeight = weightNew2OldParent + weightParent2Child;
				
				newParent.removeEdge(newParent.getEdgeToChild(parent));
				TreeEdgeI edge = new TreeEdge(newParent, child, newWeight);
				addEdge(edge);
				removeNode(parent);
				deleted.add(parent);
				return deleted;
			}
		}

		// inner node
		return deleted;
	}
	
	/* *********************************************************** *
	 *          			 DOMAIN TREE  METHODS				   *
	 * *********************************************************** */
	
	public TreeNodeI getNode4DA(DomainArrangement da) {
		Iterator<TreeNodeI> iter = getNodeIterator();
		while(iter.hasNext()) {
			TreeNodeI node = iter.next();
			if (!node.hasArrangement())
				continue;
			if (node.getArrangement().equals(da))
				return node;
		}
		return null;
	}
	
	
	public DomainArrangement[] getDaSet() {
		List<DomainArrangement> res = new ArrayList<DomainArrangement>();
		
		Iterator<TreeNodeI> iter = getNodeIterator();
		while(iter.hasNext()) {
			TreeNodeI dtn = iter.next();
			if (dtn.hasArrangement() && dtn.getArrangement().getName() != null) 
				res.add(dtn.getArrangement());
		}
		return res.toArray(new DomainArrangement[res.size()]);
	}
	
	/**
	 * After the domain tree structure is completed, the 
	 * domain arrangements are assigned to it.
	 * 
	 * @param tree
	 * 		the original tree
	 * @param daSet
	 * 		set of domain arrengements
	 */
	public void loadDasIntoTree(DomainArrangement[] daSet) {
		// map the labels to the DAs to speed up the creation process
		Map<String, DomainArrangement> label2da = new HashMap<String, DomainArrangement>();
		for (int i = 0; i < daSet.length; i++) 
			label2da.put(daSet[i].getName().toUpperCase(), daSet[i]);

		// if multiple nodes with the same name occur in the tree an arrangement has to be cloned.
		// Therefore this List stores already assigned arrangements and can be used to initiate a clone process
		List<DomainArrangement> toClone = new ArrayList<DomainArrangement>();
		
		// create the DomainTree
		Iterator <TreeNodeI> iter = getNodeIterator();
		while (iter.hasNext()) {
			TreeNodeI dtn = iter.next();
			
			// if node has an arrangement
			if (label2da.get(dtn.getLabel().toUpperCase()) != null) {
				DomainArrangement da = label2da.get(dtn.getLabel().toUpperCase());
				
				// if the node is already set for the da clone it
				if (toClone.contains(da)) {
					try {
						da = (DomainArrangement) da.clone();
					} catch(CloneNotSupportedException cnse) {
						Configuration.getLogger().debug(cnse.toString());
					}
				} else
					toClone.add(da);
				
				// now set the node for the da and vice versa the arrangement for the node
				dtn.setArrangement(da);
			}
		}
		
//TODO		InsertionDeletionAlgo.calcInDel(this);
	}
	
	public List<DomainArrangement> gatherDAs4Subtree(List<DomainArrangement> res, TreeNodeI node) {
		for (int i = 0; i < node.childCount(); i++)
			gatherDAs4Subtree(res, (TreeNodeI) node.getChildAt(i));
			
		if (node.hasArrangement())
			res.add(node.getArrangement());
		
		return res;
	}
	
	public void print() {
		Iterator<TreeNodeI> iter = this.getNodeIterator();
		while (iter.hasNext()) {
			TreeNodeI parent = iter.next();
			for (int i = 0; i < parent.childCount(); i++) 
				System.out.println(parent.getLabel()+"->"+parent.getChildAt(i).getLabel());
		}
	}
	
}