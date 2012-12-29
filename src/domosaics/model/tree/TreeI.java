package domosaics.model.tree;

import java.util.Iterator;
import java.util.List;

import domosaics.model.DoMosaicsData;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.tree.io.NewickTreeReader;
import domosaics.model.tree.io.NexusTreeReader;




/**
 * The tree interface represents rooted trees. The tree extends {@link GraphI} 
 * and consists of {@link TreeNodeI}s and {@link TreeEdgeI}s.
 * <p>
 * To create TreeNodes with an unique id a {@link TreeNodeFactory} is used. 
 * <p>
 * To create trees, parsers can be used, e.g. {@link NewickTreeReader} and 
 * {@link NexusTreeReader} which return the corresponding tree.
 * <p>
 * The tree is rooted and the trees root node can be accessed using 
 * {@link #getRoot()}. 
 *
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public interface TreeI extends DoMosaicsData{
	
	/**
	 * Adds a node to the graph
	 * 
	 * @param 
	 * 		node to add
	 */
	public void addNode(TreeNodeI node);
	
	
	/**
	 * Adds an edge to the graph
	 * 
	 * @param 
	 * 		edge to add
	 */
	public void addEdge(TreeEdgeI edge);
	
	/**
	 * Removes the specified node object from the graph.
	 * 
	 * @param 
	 * 		node to be removed
	 */
	public void removeNode(TreeNodeI node);
	
	/**
	 * Returns an iterator over all nodes which were added to the graph.
	 * 
	 * @return 
	 * 		node iterator over all nodes
	 */
	public Iterator<TreeNodeI> getNodeIterator();
	
	/**
	 * Return the number of nodes contained by the graph
	 * 
	 * @return 
	 * 		the number of nodes
	 */
	public int nodeCount();
	
	/**
	 * Method which sets a new outgroup for the given tree
	 * 
	 * @param newRoot
	 * 		the new root for the tree
	 */
	public void reRoot (TreeNodeI newRoot);
	
	/**
	 * sets the root node of this tree
	 * 
	 * @param 
	 * 		root node
	 */
	public void setRoot (TreeNodeI root);
	
	/**
	 * Returns the root node of this tree
	 * 
	 * @return 
	 * 		root node
	 */
	public TreeNodeI getRoot();
	
	/**
	 * count the number of leaves in the tree
	 * 
	 * @return 
	 * 		number of leaves within the tree
	 */
	public int countLeaves();
	
	/**
	 * Calculates the maximal depth of the tree
	 * 
	 * @return
	 * 		maximal depth of the tree
	 */
	public int getMaxDepth();
	
	/**
	 * initializes the bootstrap values for edges, based on a complete tree.
	 */
	public void initBootstrapValues();
	
	/**
	 * Calculates the last common ancestor of two nodes
	 * 
	 * @param nodeA
	 * 		first inspected node
	 * @param nodeB
	 * 		second inspected node
	 * @return
	 * 		LCA of first and second inspected node
	 */
	public TreeNodeI findLastCommonAncestor(TreeNodeI nodeA, TreeNodeI nodeB);
	
	/**
	 * Calculates the LCA for a list of nodes.
	 * 
	 * @param nodes
	 * 		list of nodes for which the LCA should be calculated
	 * @return
	 * 		LCA from a list of nodes
	 */
	public TreeNodeI findLastCommonAncestor(List<TreeNodeI> nodes);
	
	
	public List<TreeNodeI> deleteNode(TreeNodeI node);
	
	/* *********************************************************** *
	 *          			 DOMAIN TREE  METHODS				   *
	 * *********************************************************** */

	public TreeNodeI getNode4DA(DomainArrangement da);
	
	/**
	 * Returns the dataset for the domain tree
	 * 
	 * @return
	 * 		dataset for the domain tree
	 */
	public DomainArrangement[] getDaSet();
	
	public void loadDasIntoTree(DomainArrangement[] daSet);
	
	public List<DomainArrangement> gatherDAs4Subtree(List<DomainArrangement> res, TreeNodeI node);
}