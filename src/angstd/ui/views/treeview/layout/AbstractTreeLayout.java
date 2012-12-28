package angstd.ui.views.treeview.layout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import angstd.model.tree.TreeI;
import angstd.ui.views.treeview.TreeView;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.view.View;



/**
 * The basic method {@link #layoutContainer(Container)} of a LayoutManager 
 * is triggered by the {@link TreeView#doLayout()} method. For instance
 * a TreeView extends JComponent so the layout calculation
 * is always triggered, when the view is resized.
 * <p>
 * The {@link #layoutContainer(Container)} method does only calculate 
 * the drawing area for the tree, which means the label space to the right 
 * and top is cut off. The actual Positioning of the tree nodes is done
 * in subclasses by triggering the abstract method {@link #layoutTree(Dimension, int, int)}.
 * <p>
 * To layout a component Java uses the bounds of the components 
 * for positioning. So the aim of the layout process is to calculate the 
 * bounds of all node components within the tree, which should
 * be done in layoutTree. The methods {@link #getBounds(NodeComponent, double)}
 * and {@link #getPreferredSize(NodeComponent)} return the bounds of the
 * requested node component by calculating its width and height.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public abstract class AbstractTreeLayout implements TreeLayout{

	/** The view on which the layout is done */
	protected TreeViewI treeView;	
	
	/** The space in pixel to the right, which must be reserved for labels (set by getInsets()) */
	protected int leaveLabaleSpace; 
	
	/** The relative space which is needed for label drawing (set by getInsets()) */
	protected double relativeleaveLabaleSpace;
	
	/** the are on which the tree is layouted */
	protected Rectangle layoutArea;
	
	/** parameter used during the layout process */
	protected TreeParameter param = new TreeParameter();
	
	
	/**
	 * Sets the view as well as the tree to be layouted 
	 * and forces a relayout by calling treeStructureChanged
	 * 
	 * @param treeView 
	 * 		tree view on which the layout is done
	 */
	public void setView(View treeView) {
		// store the underlying TreeView
		this.treeView = (TreeViewI) treeView;
		treeStructureChanged();
	}
	
	/**
	 * Returns the tree view being layouted
	 * 
	 * @return
	 * 		tree view being layouted
	 */
	public TreeViewI getView() {
		return treeView ;
	}
    
	/**
	 * Indicates that the tree structure changed and therefore
	 * the new largest node has to be found.
	 */
	public void treeStructureChanged() {
		param.largestNode = null;
	}

	/**
	 * Do a full tree layout in the given space
	 */
	public abstract void layoutTree(int x, int y, int width, int height, int leaveLabaleSpace);
	
    /**
     * Delegates to {@link #layoutTree(int, int, int, int, int)}
     * 
     * @param size 
     * 		width and height of the layout area
     */
    public void layoutTree(int x, int y, Dimension size, int leaveLabaleSpace){
    	layoutTree(x, y, size.width, size.height, leaveLabaleSpace);
    }
    		
	/**
	 * Calculates the layout area within the parent component and takes
	 * it insets into account. The drawing area is the complete
	 * area without the parents insets and without the space which is
	 * needed to draw the labels.
	 * Finally {@link #layoutTree(int, int, int, int) is called where the actual
	 * tree layout is done.
	 * 
	 * @param parent 
	 * 		container to be layouted
	 */
	public void layoutContainer(Container parent) {
		param.init();

		// set the needed views height
		int minHeight = (int) treeView.getTreeFontManager().getFont().getSize2D() + 2;        
    	int newHeight = (param.visible * minHeight);
		
    	treeView.setNewViewHeight(newHeight);
    	treeView.setNewViewWidth(0);
    	
		// get the size and insets of the view
		Dimension viewSize = parent.getSize();
		Insets viewInsets = parent.getInsets();

		// get width and height without insets
		int width = viewSize.width - viewInsets.left - viewInsets.right;
		int height = viewSize.height - viewInsets.top - viewInsets.bottom;

		// calculate space to the right and upper edge for drawing labels 
		Insets extraSpace = getInsets();
		leaveLabaleSpace = extraSpace.right;

		// calculate new relative label space 
		relativeleaveLabaleSpace = (double)leaveLabaleSpace / (double)width;
		
		// finally calculate the layout area
		Rectangle layoutBounds = new Rectangle(viewInsets.left, viewInsets.top, width, height);
		layoutBounds.x += extraSpace.left;
		layoutBounds.y += extraSpace.top;
		layoutBounds.width -= (extraSpace.left + extraSpace.right);
		layoutBounds.height -= (extraSpace.bottom);
		
		setTreeBounds(layoutBounds);

		layoutTree(getTreeBounds().x, getTreeBounds().y, getTreeBounds().width, getTreeBounds().height, leaveLabaleSpace);
	}
	
	/**
	 * @see TreeLayout
	 */
	public Rectangle getTreeBounds() {
		return layoutArea;
	}
	
	/**
	 * @see TreeLayout
	 */
	public void setTreeBounds(Rectangle bounds) {
		layoutArea = bounds;
	}
	
	/**
	 * @see TreeLayout
	 */
	public Insets getInsets() {	
		// insets of the parent
        Insets pInsets = treeView.getViewComponent().getInsets();	

		// calculate the maximal node height to display all nodes within the views size
		double node_height = (treeView.getViewComponent().getHeight() - pInsets.top - pInsets.bottom)/ (double) param.visible;
		
		Insets insets = new Insets(0,0,0,0);
		insets.right = (param.largestNode.getLabel() != null) ? param.maxLabelWidth : 0;
		insets.top = (int) (node_height/2.0);
		insets.bottom = (treeView.getTreeLayoutManager().isShowLegend()) ? 10 : 0;

		return insets;
	}

	/**
	 * Returns the Bounds of a NodeComponent. Therefore the font size+2 is
	 * taken as height and a minimum width of 5 pixel. If a label exists
	 * the actual width of the label is taken.
	 * 
	 * @param node 
	 * 		the node from which the bounds are requested
	 * @param height 
	 * 		height of the node
	 * @return 
	 * 		the bounds of the node
	 */
	public Dimension getBounds(NodeComponent node, double height){
		int width = 5;	// minimum width
		
		if (node == null)
			return new Dimension(width, (int) (2+height));
			
		// get label width
		String label = node.getLabel();
		if(label != null && treeView != null && treeView.getViewComponent().getGraphics() != null){
			Font font = treeView.getTreeFontManager().getFont(node);
			width = SwingUtilities.computeStringWidth(treeView.getViewComponent().getGraphics().getFontMetrics(font), label);
		}
		
		return new Dimension(width, (int) (2+height));
	}
	
	/**
	 * @see TreeLayout
	 */
	public Dimension getPreferredSize(NodeComponent node){
		return getBounds(node, treeView.getTreeFontManager().getFont(node).getSize());
	}	
	

	/* ********************************************************** *
	 * 					TREE PARAMETER CALCULATION 				  *
	 * ********************************************************** */
	
	/**
	 * Returns the used parameter to layout the view properly
	 * 
	 * @return
	 * 		used parameter to layout the view
	 */
	public TreeParameter getTreeParams() {
		return param;
	}
	
	/**
	 * 
	 * @author Andreas Held
	 *
	 */
	public class TreeParameter {
		
		/** The largest node regarding the label length */
		protected NodeComponent largestNode;	
		
		/** the maximal needed label width */
		public int maxLabelWidth = 0;
		
		/** number of visible leafs (can be collapsed nodes as well) */
		public int visible = 0;
		
		
		/**
		 * Preliminary init of needed parameters
		 */
		public void init () {
			compute_Leaves();
			compute_maxLabelWidth();
		}
		
		private void compute_Leaves() {
			visible = 0;
			for (NodeComponent n : treeView.getNodesComponent(treeView.getTree().getRoot()).depthFirstIterator()) 
				if (n.getNode().isLeaf())
					visible++;
		}
		
		private void compute_maxLabelWidth() {
			// run through all nodes starting at the root and get the largest node 
			largestNode = treeView.getNodesComponent(treeView.getTree().getRoot());
		
			// the first question is: are there any leaves visible?
			boolean isLeafVisible = false;
			for (NodeComponent n : largestNode.depthFirstIterator()) 
				if (n.getNode().isLeaf() && n.isVisible()) {
					isLeafVisible = true;
					break;
				}
			
			// because if not we have to take invisible leaf
			if (!isLeafVisible) 
				for (NodeComponent n : largestNode.depthFirstIterator()) 
					if (n.getNode().isLeaf()) { 
						int actNodeWidth = (n.getLabel() != null) ? getPreferredSize(n).width : -1;
					
						if (largestNode.getLabel() == null || actNodeWidth > maxLabelWidth) {
							largestNode = n;
							maxLabelWidth = actNodeWidth;
						}
					}
			
//			// because if not we have to take a collapsed node as leaf
//			if (!isLeafVisible) {
//				for (NodeComponent n : largestNode.depthFirstIterator()) 
//					if (n.isCollapsed() && n.isVisible()) 
//						if(largestNode.getLabel() == null || (n.getLabel() != null &&  n.getLabel().length() > largestNode.getLabel().length())) {
//							largestNode = n;
//							maxLabelWidth = getPreferredSize(n).width;
//						}
//				return;
//			}
			
			// if we have visible leaves we search for the largest one
			for (NodeComponent n : largestNode.depthFirstIterator()) 
				if (n.getNode().isLeaf() && n.isVisible()) { 
					int actNodeWidth = (n.getLabel() != null) ? getPreferredSize(n).width : -1;
					
					if (largestNode.getLabel() == null || actNodeWidth > maxLabelWidth) {
						largestNode = n;
						maxLabelWidth = actNodeWidth;
					}
				}
		}
	}
}
