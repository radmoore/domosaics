package domosaics.model.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domosaics.model.arrangement.DomainArrangement;




/**
 * see {@link TreeNodeI}
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class TreeNode implements TreeNodeI {

	/** Caches the level within the tree */
	protected int level = -1;
	
	/** the assigned domain arrangement */
	protected DomainArrangement da;
	
//	/** set of domains which can be used to save the states for inner nodes, e.g. when computing indels */
//	protected DomainSet currentDoms;
	
	/** unique id for the tree node */
	protected int id;
	
	/** the node label. */
	protected String label;
	
	/** the incoming edge for this node which connects the node with its parent. */
	protected TreeEdgeI inEdge;
	
	/** the outgoing edges to all children of this node within the tree. */
	protected ArrayList<TreeEdgeI> outEdges;
	
	
	/**
	 * Basic constructor which creates a new empty node containing only the 
	 * unique id, which is automatically created by a {@link TreeNodeFactory}.
	 * 
	 * @param id 
	 * 		unique id
	 */
	public TreeNode(int id) {
		this(id, "");
	}
	
	/**
	 * The constructor for a labeled node containing only the 
	 * unique id, which is automatically created by a {@link TreeNodeFactory}.
	 * @param id 
	 * 		the unique id
	 * @param label 
	 * 		the node label
	 */
	public TreeNode(int id, String label) {
		this.id = id;
		this.label = label;
		this.outEdges = new ArrayList<TreeEdgeI>();
		da = null;
//		currentDoms = new DomainSet();
	}
	
	@Override
	public int getID() {
		return id;
	}

	@Override
	public int childCount() {
		return outEdges.size();
	}
	
	@Override
	public List<TreeNodeI> getChildren() {
		List<TreeNodeI> res = new ArrayList<TreeNodeI>();
		for (TreeEdgeI edge2child : outEdges)
			res.add(edge2child.getTarget());
		return res;
	}
	
	@Override
	public TreeNodeI getChildAt(int index) {
		if (index >= outEdges.size())	
			return null;
		return outEdges.get(index).getTarget();
	}
	
	@Override
	public Iterator<TreeNodeI> getChildIter () {
		ArrayList<TreeNodeI> childs = new ArrayList<TreeNodeI>();
		for (int i = 0; i < childCount(); i++)
			childs.add(getChildAt(i));
		return childs.iterator();
	}
	
	@Override
	public TreeEdgeI getEdgeToParent() {
		return inEdge;
	}
	
	@Override
	public TreeNodeI getParent() {
		if (inEdge != null)
			return inEdge.getSource();
		return null;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public void setLabel(String label) {
		this.label = (label != null) ? label.trim() : label;
	}
	
	@Override
	public void addEdge(TreeEdgeI edge) {
		if(edge.getSource() == this) {		// if this node is parent in edge
			outEdges.add(edge);
		} else								// if this node is child in edge
			inEdge = edge;
	}
	
	@Override
	public void removeEdge(TreeEdgeI edge) {
		if(inEdge != null && edge == inEdge) 			
			inEdge = null;
		else 
			outEdges.remove(edge);
	}

	@Override
	public void clear() {
		if(inEdge != null) {
			inEdge.getSource().removeEdge(inEdge);
			inEdge = null;
		}
	
		Iterator<TreeEdgeI> edgeIter = outEdges.iterator();
		while(edgeIter.hasNext()) {
			TreeEdgeI outEdge = edgeIter.next();
			outEdge.getTarget().removeEdge(outEdge);
		}

		outEdges.clear();
	}
	
	@Override
	public TreeEdgeI getEdgeToChild(int i) {
		if (i >= outEdges.size())
			return null;
		return outEdges.get(i);
	}

	@Override
	public TreeEdgeI getEdgeToChild(TreeNodeI child) {
		for (int i = 0; i < childCount(); i++)
			if (getEdgeToChild(i).getTarget().equals(child))
				return getEdgeToChild(i);
		return null;
	}
	
	
	
	
	@Override
	public double getDistanceToParent() {
		if (getEdgeToParent() == null)
			return -1;
		return getEdgeToParent().getWeight();
	}
	
	@Override
	public boolean isLeaf() {
		if (childCount() == 0)
			return true;
		return false;
	}
	
	@Override
	public void rotateChildren() {	
		if (outEdges != null && outEdges.size() >= 2) {
			TreeEdgeI first = outEdges.get(0);
			removeEdge(first);
			addEdge(first);
		}
	}

	@Override
	public int countLeaves() {
		if (isLeaf()) 
			return 1;
		
		int count = 0;
		Iterator<TreeNodeI> iter = getChildIter();
		while(iter.hasNext())
			count += iter.next().countLeaves();
		return count;
	}
	
	@Override
	public int getLevel() {
		if (level == -1) {
			TreeNodeI parent = this;
			level = 0;
			while ((parent = parent.getParent()) != null)
				level++;
		}
		return level;
	}

	@Override
	public double getBootstrap() {
		if (getEdgeToParent() == null)
			return -1;
		return getEdgeToParent().getBootstrap();
	}
	
	/* *********************************************************** *
	 *          			 DOMAIN TREE  METHODS				   *
	 * *********************************************************** */
	
//	public DomainSet getCurrentDoms () {
//		return currentDoms;
//	}
	
	@Override
	public boolean hasArrangement() {
		return da != null;
	}
	
	@Override
	public void setArrangement(DomainArrangement da) {
		this.da = da;
		if (da != null)
			da.setTreeNode(this);
	}
	
	@Override
	public DomainArrangement getArrangement() {
		return da;
	}

}
