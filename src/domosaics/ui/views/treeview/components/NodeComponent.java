package domosaics.ui.views.treeview.components;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.treeview.manager.TreeComponentManager;
import domosaics.ui.views.view.components.AbstractViewComponent;





/**
 * Class NodeComponent represents the graphical unit of a node. 
 * <p> 
 * Here the coordinates, bounds and shapes as well as flags like visibility and 
 * collapsed are stored.
 * <p>
 * By adding the {@link TreeComponentManager} reference to this object a node
 * component can also return node component iterators for its children and its 
 * subtree, which comes in handy during the layout and rendering process.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 * 
 */
public class NodeComponent extends AbstractViewComponent {

	/**
	 * The underlying {@link TreeNodeI} on which the node component is based on.
	 */
	protected TreeNodeI treeNode = null;
	
	/**
	 * the component manager which manages this node component.
	 * The Reference is needed to make subtree and child iterations possible.
	 */
	protected TreeComponentManager manager;

	/**
	 * Bounds of the subtree where this node component is root.
	 * Changes are made by calling {@link #getSubtreeBounds()} .x, .y and so on.
	 */
	protected Rectangle2D.Double subtreeBounds = new Rectangle2D.Double();
	
    /**
     * The subtree shape where this node is root. Can be used during the 
     * rendering process to determine if a subtree of a node intersects with the
     * clipping area and therefore has to be traversed.
     */
    protected Shape subtreeShape = new Rectangle();
   
    /**
     * the visibility flag for this node
     */
    protected boolean visible = true;

    /**
     * the collapsed flag for this node
     */
    protected boolean collapsed = false;
    

	/**
	 * Constructor for a new node component.
	 * @param treeNode 
	 * 		the TreeNode on which the node component is based on
	 * @param manager 
	 * 		the component manager which manages this node component.
	 */
	public NodeComponent(TreeNodeI treeNode, TreeComponentManager manager) {
		super();
        if(treeNode == null){
        	throw new RuntimeException("Can not create NodeComponent without backend node !");
        }
        this.treeNode = treeNode;
        this.manager = manager;        
	}
	
	
	/* ******************************************************************* *
	 *   						General get methods						   *
	 * ******************************************************************* */
	
	/**
	  * Returns the label of this node. If the node is collapsed
	  * the number of nodes of the subtree.
	  * 
	  * @return 
	  * 		the node components label.
	  */
	public String getLabel(){
//		if (!getNode().isLeaf())
//			return ""+getNode().getID();
		String label = getNode().getLabel();
		if(label != null && !label.trim().isEmpty()) 
			if (label.length() > 20)
				return label.substring(0, 16)+"...";
			else
				return label;
		if(isCollapsed())
			return " "+getNode().countLeaves() +" nodes";
		return null;
	}
	
	/**
	 * Return the underlying {@link TreeNodeI} for this node.
	 * 
	 * @return 
	 * 		The TreeNode for this node component.
	 */
	public TreeNodeI getNode() {
		return treeNode;
	}
	
	/**
	 * Returns the graphical node components parent node of this node.
	 * 
	 * @return 
	 * 		the parent node as graphical node component.
	 */
	public NodeComponent getParent() {
		return manager.getComponent((TreeNodeI) treeNode.getParent());
	}
	
	
	/* ******************************************************************* *
	 *   							Flag methods						   *
	 * ******************************************************************* */

	/**
	 * Sets the visible flag for the node component determining whether or not the 
	 * node is visible. For example all nodes within a collapsed subtree are
	 * invisible and therefore mustn't be drawn.
	 * 
	 * @param visible 
	 * 		new visible status for this node.
	 */
	public void setVisible(boolean visible) {
  		this.visible = visible;
	}

	/**
	 * Returns whether or not the node component is visible.
	 * 
	 * @return 
	 * 		visibility flag for this node component.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Returns whether or not the node component is collapsed.
	 * 
	 * @return 
	 * 		collapsed flag for this node component.
	 */
	public boolean isCollapsed() {
		return collapsed;
	}

	/**
	 * Sets the new collapsed status for this node.
	 * 
	 * @param collapse 
	 * 		new collapsed status.
	 */
	public void setCollapsed(boolean collapse) {
		if (treeNode.isLeaf() || collapse == collapsed) 
	           return;
		
       collapsed = collapse;
	}

	/* ******************************************************************* *
	 *   						Coordinate  methods						   *
	 * ******************************************************************* */

	/**
	 * Returns the bounds of the subtree where this node component is root.
	 * <p>
	 * Changes on the subtree bounds are done by using this method. For example
	 * node.getSubtreeBounds().y = newY;
	 * 
	 * @return 
	 * 		bounds of the subtree
	 */
	public Rectangle2D.Double getSubtreeBounds() {
		return subtreeBounds;
	}


	/* ******************************************************************* *
	 *   							Shape  methods						   *
	 * ******************************************************************* */
	
	/**
	 * Returns the actual shape for this component, which is used for mouse
	 * events to determine whether or not the node component was clicked on.
	 * <p>
	 * For collapsed nodes this shape is the collapsed triangle. For inner 
	 * nodes without a label the shape is a circle.
	 */
	public Shape getDisplayedShape () {
		int height = getHeight();
		int cr = height > 12 ? 12 : height;
		int x = getX();
		int y = getY();
		if (isCollapsed())
			return new Polygon(new int[]{x,x+cr,x+cr,x}, new int[]{y,y-(cr/2), y+(cr/2),y}, 4);
		else
			return new Arc2D.Double(getX()-5, getY()-5, 10, 10, 0, 360, 0);
	}


	/**
	 * Returns the subtree shape for this node component. 
	 * 
	 * @return 
	 * 		Subtree as shape
	 */
	public Shape getSubtreeShape() {
		return subtreeShape;
	}

	/**
	 * Sets the new subtree shape for this node. Used in the layout process.
	 * 
	 * @param subtreeShape 
	 * 		the new subtreeShape
	 */
	public void setSubtreeShape(Shape subtreeShape) {
		this.subtreeShape = subtreeShape;
	}
	
	
	/* ******************************************************************* *
	 *   			Iterator methods over children and subtree			   *
	 * ******************************************************************* */

   /**
    * Returns a new PostorderIterator over the subtree of this node component.
    * 
    * @return
    * 		a new iterable depth first iterator 
    */
	public Iterable<NodeComponent> depthFirstIterator() {
		return new PostorderIterator(this);
	}
   
	/**
	 * Returns an Iterator over all children of this node.
	 * 
	 * @return
	 * 		a new iterable child iterator
	 */
	public Iterable<NodeComponent> children() {
		return new ChildIterable();
	}

   /**
    * Inner class ChildIterable. 
    * Iterate over the children of this node component.
    * 
    * @author Thasso Griebel - thasso@minet.uni-jena.de
    * 
    */
   class ChildIterable implements Iterator<NodeComponent>, Iterable<NodeComponent> {
	   Iterator<TreeNodeI> it;

       public ChildIterable() {
    	   it = treeNode.getChildIter();
       }

       public boolean hasNext() {
           return it.hasNext();
       }

       public NodeComponent next() {
           return manager.getComponent(it.next());
       }

       public void remove() {
           throw new RuntimeException("Method not supported");
       }

       public Iterator<NodeComponent> iterator() {
           return this;
       }
   }
   
   /**
    * Iteration helper
    */
   private static final Iterator<NodeComponent> EMPTY_ITERABLE = new Iterator<NodeComponent>() {
       public boolean hasNext() {
           return false;
       }

       public NodeComponent next() {
           return null;
       }

       public void remove() {
       }
   };
  
   /**
    * Inner class PostorderIterator.
    * PostOrder or DepthFirst iteration over the subtree of a {@link NodeComponent}.
    * 
    * @author Thasso Griebel - thasso@minet.uni-jena.de
    */
   class PostorderIterator implements Iterable<NodeComponent>, Iterator<NodeComponent> {

       protected NodeComponent root;
       protected Iterator<NodeComponent> children;
       protected Iterator<NodeComponent> subtree;

       public PostorderIterator(NodeComponent node) {
    	   root = node;
           children = node.children().iterator();
           subtree = EMPTY_ITERABLE;
       }
       
       public Iterator<NodeComponent> iterator() {
       		return this;
       }

       public boolean hasNext() {
    	   return root != null;
       }

       public NodeComponent next() {
    	   NodeComponent retval;
    	  
    	   if (subtree.hasNext()) {
    		   retval = subtree.next();
       		} else if (children.hasNext()) {
       		   subtree = new PostorderIterator(children.next());
       		   retval = subtree.next();
       		} else {
       		   retval = root;
       		   root = null;
            }
           return retval;
       }

       public void remove() {
       }
	
   }
 
}

