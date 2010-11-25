package angstd.ui.views.domaintreeview.layout;

import angstd.ui.views.domainview.layout.DomainLayout;
import angstd.ui.views.treeview.layout.TreeLayout;

/**
 * Class DomainTreeLayout specifies all methods necessary to layout
 * a domain tree by extending a {@link TreeLayout} as well as a
 * {@link DomainLayout}.
 * <p>
 * Because the domain layout can change just a method supporting
 * the change is necessary (so far).
 * 
 * @author Andreas Held
 *
 */
public interface DomainTreeLayout extends TreeLayout, DomainLayout{

	/** distance between the tree and the arrangements */
	public static final int DISTANCE_BETWEEN_TREE_AND_DATASET = 4;
	
	/**
	 * Method to change the used domain layout (e.g. for proportional
	 * or unproportional layouting).
	 * 
	 * @param domlayout
	 * 		the layout used to layout the arrangements
	 */
	public void setDomainLayout(DomainLayout domlayout);
	
	/**
	 * Returns the layout used to layout arrangements 
	 * (e.g. UnproportionalLayout=).
	 * 
	 * @return 
	 * 		the layout used to layout the arrangements
	 */
	public DomainLayout getDomainLayout();
}
