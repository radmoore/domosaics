package domosaics.model.tree;

import java.util.Iterator;
import java.util.List;

import domosaics.model.DoMosaicsData;
import domosaics.model.arrangement.DomainArrangement;




/**
 * Interface TreeNodeI represents nodes within a {@link TreeI}.
 * <p>
 * New nodes should be created by using a {@link TreeNodeFactory} to ensure that
 * each new node contains an unique ID.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public interface TreeNodeI extends DoMosaicsData{

	/**
	 * Returns the unique id for the node within the graph, which was assigned 
	 * to the node during its creation by a {@link NodeFactoryI}.
	 * 
	 * @return 
	 * 		the unique id within the graph
	 */
	public int getID();
	
	/**
	 * Returns the number of children of this node.
	 * 
	 * @return 
	 * 		number of children of this node
	 */
	public int childCount();
	
	/**
	 * Returns the child node at position index.
	 * 
	 * @param index 
	 * 		the index for the child which should be returned
	 * @return 
	 * 		the child node at position index
	 */
	public TreeNodeI getChildAt(int index);
	
	/**
	 * Returns an iterator of all children of this node
	 * 
	 * @return 
	 * 		iterator over all children
	 */
	public Iterator<TreeNodeI> getChildIter ();
	
	public List<TreeNodeI> getChildren();
	
	
	/**
	 * Returns the incoming edge to the parent
	 * 
	 * @return
	 * 		incoming edge to the parent
	 */
	public TreeEdgeI getEdgeToParent();
	
	/**
	 * Returns the parent node of this node. For root nodes null is returned.
	 * 
	 * @return 
	 * 		the parent node
	 */
	public TreeNodeI getParent();
	
	
	/**
	 * Returns the label for a node.
	 *  
	 * @return 
	 * 		node label
	 */
	public String getLabel();
	
	/**
	 * sets a new label for this node
	 * 
	 * @param 
	 * 		label for this node
	 */
	public void setLabel(String label);
	
	/**
	 * Adds and edge to the node. By checking the source and target of the edge
	 * it can be clarified if its an incoming or outgoing edge.
	 * 
	 * @param edge 
	 * 		edge which has to be added
	 */
	public void addEdge(TreeEdgeI edge);
	
	/**
	 * Removes an edge from the Node.
	 * 
	 * @param edge 
	 * 		edge which has to be removed
	 */
	public void removeEdge(TreeEdgeI edge);
	
	/**
	 * Removes all edges to this node.
	 * Should be called when removing a node from a graph.
	 */
	public void clear();
	
	/**
	 * Returns the edge to the child at the specified position.
	 * 
	 * @param i
	 * 		position of the child edge within the list
	 * @return
	 * 		edge to the child at the specified position
	 */
	public TreeEdgeI getEdgeToChild(int i);
	
	/**
	 * Returns the edge to the specified child node.
	 * 
	 * @param child
	 * 		child node
	 * @return
	 * 		edge to the specified child node
	 */
	public TreeEdgeI getEdgeToChild(TreeNodeI child);
	
	
	
	
	
	/**
	 * Returns the distance to the parent node.
	 * If the node has no parent (root node ) -1 is returned.
	 * 
	 * @return 
	 * 		distance to parent or -1 (if there is no parent)
	 */
	public double getDistanceToParent();
	
	/**
	 * Checks whether or not the node is a leaf node
	 * 
	 * @return
	 * 		whether or not the node is a leaf node
	 */
	public boolean isLeaf();
	
	/**
	 * Rotates the children of this node.
	 */
	public void rotateChildren();
	
	/**
	 * Traverses the tree rooted at this node and counts leaves.
	 * 
	 * @return 
	 * 		leaves under this node
	 */
	public int countLeaves();
	
	/**
	 * Returns the depth of this node within the tree
	 * 
	 * @return
	 * 		depth of this node within the tree
	 */
	public int getLevel();

	/**
	 * Returns the bootstrap value for the edge to the parent
	 * 
	 * @return
	 * 		bootstrap value for the edge to the parent
	 */
	public double getBootstrap();
	
	/* *********************************************************** *
	 *          			 DOMAIN TREE  METHODS				   *
	 * *********************************************************** */
	
	/** 
	 * Checks whether or not there is an arrangement assigned to this node.
	 * 
	 * @return
	 * 		whether or not there is an arrangement assigned to this node.
	 */
	public boolean hasArrangement();
	
	/**
	 * Assigns a domain arrangement to the node
	 * 
	 * @param da
	 * 		the arrangement to be assigned
	 */
	public void setArrangement(DomainArrangement da);
	
	
	/**
	 * Returns the assigned domain arrangement
	 * 
	 * @return
	 * 		assigned domain arrangement
	 */
	public DomainArrangement getArrangement();

//	/**
//	 * Returns the actual state for an inner node.
//	 * 
//	 * @return
//	 * 		actual state for an inner node.
//	 */
//	public DomainSet getCurrentDoms();
}
