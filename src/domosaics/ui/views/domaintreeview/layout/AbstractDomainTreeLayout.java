package domosaics.ui.views.domaintreeview.layout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.tree.TreeI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.layout.DomainLayout;
import domosaics.ui.views.domainview.layout.ProportionalLayout;
import domosaics.ui.views.domainview.layout.AbstractDomainLayout.ArrangementParameter;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.layout.DendogramLayout;
import domosaics.ui.views.treeview.layout.TreeLayout;
import domosaics.ui.views.treeview.layout.AbstractTreeLayout.TreeParameter;
import domosaics.ui.views.view.View;




/**
 * AbstractDomainTreeLayout provides the actual layout methods. The 
 * tree layout is calculated using a TreeLayout. The arrangements are 
 * layouted using a DomainLayout. Finally the DomainEvents are layouted using 
 * InDelLayouter.
 * 
 * 
 * @author Andreas Held
 *
 */
public abstract class AbstractDomainTreeLayout implements DomainTreeLayout {

	/** the view to be layouted */
	protected DomainTreeViewI view;
	
	/** the tree to be layouted */
	protected TreeI domTree;
	
	/** the tree layout used to layout the tree */
	protected TreeLayout treelayout;
	
	/** the domain layout used to layout the arrangements */
	protected DomainLayout domlayout;
	
	/** the area of the view */
	protected Rectangle viewArea;
	
	/** the layout class used to layout domain events */
	protected InDelLayouter domEventLayouter;
	
	/** parameter needed to layout the domain tree */
	protected DomTreeParameter param;
	
	
	public AbstractDomainTreeLayout() {
		this.treelayout = new DendogramLayout();
		this.domlayout = new ProportionalLayout();
	}
	
	/**
	 * Sets the view on which this layout should be used on.
	 * 
	 * @param view 
	 * 		component on which the layout is done
	 */
	@Override
	public void setView(View view) {
		// store the underlying TreeView
		this.view = (DomainTreeViewI) view;
		
		// store the tree
		this.domTree = this.view.getTree();
		
		treelayout.setView(this.view);
		domlayout.setView(this.view);
		
		domEventLayouter = new InDelLayouter(this.view);
		
		param = new DomTreeParameter();
		
		treeStructureChanged();
	}
	
	/**
	 * @see DomainTreeLayout
	 */
	@Override
	public void setDomainLayout(DomainLayout domlayout) {
		this.domlayout = domlayout;
	}
	
	/**
	 * @see DomainTreeLayout
	 */
	@Override
	public DomainLayout getDomainLayout() {
		return this.domlayout;
	}
	
	
	/**
	 * Calculates the layout area for the tree and the arrangements
	 * within the parent component and takes its insets into account. 
	 * This method sets also the views needed diimensions.
	 * 
	 * @param parent 
	 * 		container to be layouted
	 */
	@Override
	public void layoutContainer(Container parent) {
		param.init();
		
		/// set the views needed dimensions
		
		// set the views height
	    int neededHeight = param.visible * (MIN_DA_HEIGHT+DA_SPACE)+20;
	    view.setNewViewHeight(neededHeight);
	    
	    // set the views width (use 1/3 of the visible rectangle to draw the tree)
	    int insetsWidth = view.getViewComponent().getInsets().left + view.getViewComponent().getInsets().right;
		int neededWidth = 
			param.treeSpaceWidth+
			param.maxLabelWidth+
			DISTANCE_BETWEEN_TREE_AND_DATASET+
			param.domSpaceWidth+
			insetsWidth;
		
		if (view.getDomainLayoutManager().isCollapseSameArrangements() || view.getDomainLayoutManager().isCollapseBySimilarity())
			neededWidth+=40; // draw behind arrangement

		if (!view.getDomainLayoutManager().isFitDomainsToScreen()) 
			view.setNewViewWidth(neededWidth);
		else {
			view.setNewViewWidth(0);
			param.domSpaceWidth = 
				view.getViewComponent().getWidth() -
				param.treeSpaceWidth -
				param.maxLabelWidth -
				DISTANCE_BETWEEN_TREE_AND_DATASET -
				insetsWidth;
		}
		
		/// calculate the tree layout area
		Dimension viewSize = parent.getSize();
		Insets viewInsets = parent.getInsets();
		
		// get width and height without insets
		int width = viewSize.width - viewInsets.left - viewInsets.right;
		int height = viewSize.height - viewInsets.top - viewInsets.bottom;
		viewArea = new Rectangle (viewInsets.left, viewInsets.top, width, height);
		
		// calculate space to the right and upper edge for drawing labels 
		Insets extraSpace = getInsets();
		viewArea.x += extraSpace.left;
		viewArea.y += extraSpace.top;
		viewArea.width -= (extraSpace.right+extraSpace.left);
		viewArea.height -= (extraSpace.bottom+extraSpace.top);

		// finally calculate the layout area for the tree and layout it
		Rectangle treeBounds = new Rectangle(viewArea.x, viewArea.y, viewArea.width, viewArea.height);
		treeBounds.width = param.treeSpaceWidth;
		setTreeBounds(treeBounds);
		layoutTree(getTreeBounds().x, getTreeBounds().y, getTreeBounds().width, getTreeBounds().height, param.maxLabelWidth);
	
		// layout the arrangements after the tree is layouted
		Rectangle domainBounds = new Rectangle(viewArea.x, viewArea.y, viewArea.width, viewArea.height);
		setDomainBounds(domainBounds);
		layoutArrangements(0, 0, param.domSpaceWidth, getDomainBounds().height);

		// layout domain events at the edges
		domEventLayouter.layoutInDels();
	}
	
	/**
	 * Calculate the extra insets for the view on which no components
	 * are layouted. Such as space for the amino acid ruler if present.
	 * 
	 * @return
	 * 		extra insets for the view on which no components are layouted
	 */
	@Override
	public Insets getInsets() {
		// calculate the maximal DA height to display all DAs within the domView size
		Insets pInsets = view.getViewComponent().getInsets();	
		double max_node_height = (view.getViewComponent().getHeight() - pInsets.top - pInsets.bottom)/ (double) getTreeParams().visible;
			
		// calculate the needed extra space where no layout takes place
		Insets insets = new Insets(0,0,0,0);
		
		insets.top = (int) (max_node_height/2.0);
		insets.right = (view.getDomainLayoutManager().isCollapseSameArrangements()) ? 40 : 0;
		insets.right = (view.getDomainLayoutManager().isCollapseBySimilarity()) ? 40 : insets.right;
		insets.bottom = (view.getDomainLayoutManager().isShowLineal()) ? 10 : 0;
		insets.bottom = (view.getTreeLayoutManager().isShowLegend()) ? 10 : insets.bottom;
		
		return insets;
	}
	
	/**
	 * @see TreeLayout
	 */
	@Override
	public void treeStructureChanged() {
		treelayout.treeStructureChanged();
	}
	
	/**
	 * @see TreeLayout
	 */
	@Override
	public Rectangle getTreeBounds() {
		return treelayout.getTreeBounds(); 
	}

	/**
	 * @see DomainLayout
	 */
	@Override
	public Rectangle getDomainBounds() {
		return domlayout.getDomainBounds();
	}
	
	/**
	 * @see TreeLayout
	 */
	@Override
	public void setTreeBounds(Rectangle bounds) {
		treelayout.setTreeBounds(bounds);
	}

	/**
	 * @see DomainLayout
	 */
	@Override
	public void setDomainBounds(Rectangle bounds) {
		domlayout.setDomainBounds(bounds);
	}
	
	/**
	 * @see TreeLayout
	 */
	@Override
	public void layoutTree(int x, int y, Dimension size, int leaveLabaleSpace){
    	treelayout.layoutTree(x,  y, size, leaveLabaleSpace);
    }

	/**
	 * @see TreeLayout
	 */
	@Override
	public void layoutTree(int x, int y, int width, int height, int leaveLabaleSpace) {
		treelayout.layoutTree(x,  y, width, height, leaveLabaleSpace);
	}
	
	/**
	 * @see DomainLayout
	 */
	@Override
	public void layoutArrangements(int x, int y, Dimension size) {
		layoutArrangements(x, y, size.width, size.height);
	}
	
	/**
	 * @see DomainLayout
	 */
	@Override
	public void layoutArrangement(ArrangementComponent dac, int x, int y, int width, int height) {
		domlayout.layoutArrangement(dac, x, y, width, height);
	}
	
	/**
	 * @see DomainLayout
	 */
	@Override
	public abstract void layoutArrangements(int x, int y, int width, int height);
	
	/* ******************************************************************* *
	 *   							Helper Methods		 			  	   *
	 * ******************************************************************* */
	/**
	 * Returns the bounds of a NodeComponent. First the the getPreferredSize()
	 * method defined in the TreeLayout is used and only if the node
	 * has an arrangement assigned to it the bounds of the arrangement are
	 * taken also into account.
	 * 
	 * @param node 
	 * 		the node from which the bounds are requested
	 * @return 
	 * 		the bounds of the node
	 */
	public Dimension getBounds(NodeComponent node) {
		// Calculate Preferred size for the node inclusive DA and label
		Dimension res = getPreferredSize(node);

		// add arrangement length if there is one and take domain height as new height
		if (node.getNode().hasArrangement()) { 
			Dimension domDim = getPreferredArrangementSize(node.getNode().getArrangement()); 
			res.width += domDim.width;
			if (res.height < domDim.height)
				res.height = domDim.height;
		}
		return res;
	}

	/**
	 * @see TreeLayout
	 */
	@Override
	public Dimension getPreferredSize(NodeComponent node) {
		return treelayout.getPreferredSize(node);
	}

	/**
	 * @see DomainLayout
	 */
	@Override
	public Dimension getPreferredArrangementSize(DomainArrangement da) {
		return domlayout.getPreferredArrangementSize(da);
	}

	/**
	 * @see DomainLayout
	 */
	@Override
	public int getMaxLen() {
		return domlayout.getMaxLen();
	}

	/**
	 * @see DomainLayout
	 */
	@Override
	public ArrangementParameter getDomainParams() {
		return domlayout.getDomainParams();
	}
	
	/**
	 * @see TreeLayout
	 */
	@Override
	public TreeParameter getTreeParams() {
		return treelayout.getTreeParams();
	}
	
	/* ********************************************************** *
	 * 				DOMAINTREE TREE PARAMETER CALCULATION   	  *
	 * ********************************************************** */
	
	/**
	 * Returns the used parameter to layout the view properly
	 * 
	 * @return
	 * 		used parameter to layout the view
	 */
	public DomTreeParameter getDomTreeParams() {
		return param;
	}
	
	/**
	 * 
	 * @author Andreas Held
	 *
	 */
	public class DomTreeParameter {
		public int maxLabelWidth = 0;
		
		public int treeSpaceWidth = 0;
		
		public int domSpaceWidth = 0;
		
		public int visible;
		
		/**
		 * Preliminary init of needed parameters
		 */
		public void init () {
			getTreeParams().init();
			getDomainParams().init();
			calcVisible();

			treeSpaceWidth = (int) Math.round(view.getViewComponent().getVisibleRect().width/view.getDomainTreeLayoutManager().getTreeSpace());
//			treeSpaceWidth = (int) Math.round(view.getViewComponent().getVisibleRect().width/3);
			maxLabelWidth = getTreeParams().maxLabelWidth;
			domSpaceWidth = getMaxLen();
		}
		
		private void calcVisible() {
			visible = getTreeParams().visible;
		}
	}
	
}
