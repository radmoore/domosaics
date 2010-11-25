package angstd.ui.views.domaintreeview;

import angstd.model.tree.TreeI;
import angstd.ui.views.domaintreeview.components.DomainEventComponent;
import angstd.ui.views.domaintreeview.layout.DomainTreeLayout;
import angstd.ui.views.domaintreeview.manager.CSAInSubtreeManager;
import angstd.ui.views.domaintreeview.manager.DomainEventComponentManager;
import angstd.ui.views.domaintreeview.manager.DomainTreeLayoutManager;
import angstd.ui.views.domaintreeview.manager.InnerNodeArrangementManager;
import angstd.ui.views.domaintreeview.mousecontroller.DomainEventMouseController;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.view.manager.DefaultSelectionManager;

/**
 * DomainTreeViewI describes all methods a DomainTreeView must support to
 * fit in the applications work flow. It extends the basic tree and domain
 * view interfaces by domaintree view specific methods.
 * <p>
 * The basic implementation of this interface is {@link DomainTreeView} 
 * which can be used as view parameter for all domain and tree view specific 
 * methods.
 * <p>
 * When creating a new view just its basic components are initialized by 
 * the view factory. A domain tree view is based on a backend domain
 * and a backend tree view which must be assigned to the view after its
 * creation using the setBackendViews() method. 
 * <p>
 * For detailed information and opportunities to extend the view look 
 * into {@link DomainTreeView}.
 * 
 * @author Andreas Held
 *
 */
public interface DomainTreeViewI extends TreeViewI, DomainViewI {

	/**
	 * Returns the tree with associated arrangements to the nodes
	 * 
	 * @return
	 * 		the domain tree
	 */
	public TreeI getDomTree();
	
	/**
	 * Return the layout manager for the view
	 * 
	 * @return
	 * 		layout manager
	 */
	public DomainTreeLayoutManager getDomainTreeLayoutManager();
	
	/**
	 * Sets the backend views on which the domain tree view is based on. 
	 * This method should trigger the initialization of the view such as 
	 * the view manager, the layout, renderer, mouse controller etc.
	 * 
	 * @param treeView
	 * 		the backend tree view
	 * @param domView
	 * 		the backend domain view
	 */
	public void setBackendViews(TreeViewI treeView, DomainViewI domView);
	
	/**
	 * Returns the component manager used to layout the domain events
	 * such as insertion deletion
	 * 
	 * @return
	 * 		component manager used to layout the domain events
	 */
	public DomainEventComponentManager getDomainEventComponentManager();
	
	/**
	 * Returns the backend tree view
	 * 
	 * @return
	 * 		backend tree view
	 */
	public TreeViewI getTreeView();
	
	/**
	 * Returns the backend domain view
	 * 
	 * @return
	 * 		backend domain view
	 */
	public DomainViewI getDomainView();
	
	/**
	 * Returns the manager used to manage the collapsing of same arrangements
	 * at a tree node.
	 * 
	 * @return
	 * 		manager used to manage the collapsing of same arrangements
	 */
	public CSAInSubtreeManager getCSAInSubtreeManager();
	
	/**
	 * Returns the selection manager for domain events
	 * 
	 * @return
	 * 		 selection manager for domain events
	 */
	public DefaultSelectionManager<DomainEventComponent> getDomainEventSelectionManager();
	
	/**
	 * Returns the manager managing the inner nodes arrangements
	 * 
	 * @return
	 * 		manager managing the inner nodes arrangements
	 */
	public InnerNodeArrangementManager getInnerNodeArrangementManager();
	
	
	/**
	 * Return the domain tree specific mouse controller
	 * 
	 * @return
	 * 		domain tree specific mouse controller
	 */
   	public DomainEventMouseController getDomainEventMouseController();
	
   	/**
   	 * Returns the actually used DomainTreeLayout
   	 * 
   	 * @return
   	 * 		actually used DomainTreeLayout
   	 */
   	public DomainTreeLayout getDomainTreeLayout();
   	
//   	/**
//   	 * Sets a new renderer to render the view
//   	 * 
//   	 * @param renderer
//   	 * 		the new renderer used to render the view
//   	 */
//   	public void setViewRenderer(Renderer renderer);
   	
   	/**
	 * Registers the additional renderer for a specified domain tree view,
	 * such as DomainToolTipRenderer. 
	 * 
	 * @param view
	 * 		the view to which the additional renderer should be registered
	 */
	public void registerAdditionalDomainTreeRenderer(DomainTreeViewI view);
}
