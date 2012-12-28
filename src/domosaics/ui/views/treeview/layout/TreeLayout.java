package domosaics.ui.views.treeview.layout;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.layout.AbstractTreeLayout.TreeParameter;
import domosaics.ui.views.view.layout.ViewLayout;




/**
 * Layout interface for tree view layouts. {@link AbstractTreeLayout}
 * contains a detailed description of the usage of a tree layout, so
 * for more information on this topic see {@link AbstractTreeLayout}.
 * 
 * @author Andreas Held
 *
 */
public interface TreeLayout extends ViewLayout {

	/**
	 * Indicator that the tree structure has changed and therefore all
	 * parameters have to be recalculated
	 */
	public void treeStructureChanged() ;
	
	/**
	 *  Do a full tree layout in the given space
	 *  
	 * @param x
	 * 		the x coordinate of the area being used for the layout process
	 * @param y
	 * 		the y coordinate of the area being used for the layout process
	 * @param width
	 * 		the width of the area being used for the layout process
	 * @param height
	 * 		the height of the area being used for the layout process
	 * @param leaveLabaleSpace
	 * 		the space used to render the node labels
	 */
	public void layoutTree(int x, int y, int width, int height, int leaveLabaleSpace);
	
	/**
	 * Delegates to layoutTree(int width, int height, int x, int y, int leaveLabaleSpace)
	 * 
	 * @param x
	 * 		the x coordinate of the area being used for the layout process
	 * @param y
	 * 		the y coordinate of the area being used for the layout process
	 * @param size
	 * 		The width and height of the area being used for the layout process
	 * @param leaveLabaleSpace
	 * 		the space used to render the node labels
	 */
    public void layoutTree(int x, int y, Dimension size, int leaveLabaleSpace);
    
    /**
	 * Calculates the Insets for the TreeView, which means the original insets
	 * plus the space which is needed to draw the labels at the right and top edge 
	 * of the container.
	 * 
	 * @return 
	 * 		The area of the view where no layout is done
	 */
	public Insets getInsets();
	
	/**
	 * Returns the layout area being used for the layout process
	 * 
	 * @return
	 * 		area containing the layouted tree
	 */
	public Rectangle getTreeBounds();
	
	/**
	 * Sets the tree bounds for the tree view
	 * 
	 * @param bounds
	 * 		the new tree bounds
	 */
	public void setTreeBounds(Rectangle bounds);
	
	/**
	 * Get the preferred size of a NodeComponent
	 * 
	 * @param node 
	 * 		the node from which the preferred are requested
	 * @return 
	 * 		the preferred size of the node
	 */
	public Dimension getPreferredSize(NodeComponent node);
	
	/**
	 * Returns the used parameter to layout the view properly
	 * 
	 * @return
	 * 		used parameter to layout the view
	 */
	public TreeParameter getTreeParams();
	
	
}
