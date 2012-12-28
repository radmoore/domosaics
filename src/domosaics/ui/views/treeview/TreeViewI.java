package domosaics.ui.views.treeview;

import java.beans.PropertyChangeListener;

import domosaics.model.tree.TreeI;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domaintreeview.DomainTreeView;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.components.TreeMouseController;
import domosaics.ui.views.treeview.layout.TreeLayout;
import domosaics.ui.views.treeview.manager.TreeColorManager;
import domosaics.ui.views.treeview.manager.TreeComponentManager;
import domosaics.ui.views.treeview.manager.TreeLayoutManager;
import domosaics.ui.views.treeview.manager.TreeSelectionManager;
import domosaics.ui.views.treeview.manager.TreeStrokeManager;
import domosaics.ui.views.treeview.renderer.TreeViewRenderer;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.manager.FontManager;


/**
 * TreeViewI describes all methods the TreeView must support to
 * fit in the applications work flow. The basic implementation of this 
 * interface is {@link TreeView} but also a {@link DomainTreeView}
 * implements all necessary functionalities of this interface.
 * <p>
 * When creating a new view just its basic components are initialized by 
 * the view factory. To make the view reasonable a backend dataset 
 * should be assigned after its creation, e.g. using the setTree method 
 * in the case of a tree view.
 * <p>
 * For detailed information and opportunities to extend the view look 
 * into {@link TreeView}.
 * 
 * @author Andreas Held
 *
 */
public interface TreeViewI extends View{

   	/**
	 * Sets the backend data tree for this view. This method should trigger
	 * the initialization of the view components interacting with the
	 * graphical components for this backend data, such as the
	 * view manager, the layout, renderer, mouse controller etc.
	 * 
	 * @param tree
	 * 		the backend tree
	 */
   	public void setTree(TreeI tree);
	
	public int getParsimonyMeth();
	
	public void setParsimonyMeth(int i);
	
   	/**
	 * Return the backend data which were assigned to this view.
	 * 
	 * @return
	 * 		backend tree displayed by this view
	 */
   	public TreeI getTree();
   	
	/**
	 * Registers the active mouse listeners depending on the 
	 * applications state
	 */
	public void registerMouseListeners();
	
	
	/**
	 * Registers the additional renderer for a specified tree view,
	 * such as NodeSelectionRenderer. 
	 * 
	 * @param view
	 * 		the view to which the additional renderer should be registered
	 */
	public void registerAdditionalTreeRenderer(TreeViewI view);
   	
	
	/**
	 * Registers a view as listener to all manager associated 
	 * with the view.
	 * 
	 * @param view
	 * 		the view to be added as manager listener
	 */
	public void registerViewAsManagerListener(PropertyChangeListener view);
	
	/**
	 * Unregisters a view as listener from all manager associated 
	 * with the view.
	 * 
	 * @param view
	 * 		the view to be removed as manager listener
	 */
	public void unregisterViewAsManagerListener(PropertyChangeListener view);
	
	/**
	 * Returns the actually used layout to layout the components.
	 * 
	 * @return
	 * 		actually used layout
	 */
	public TreeLayout getTreeLayout();
	
	/**
	 * Return the actually used view renderer.
	 * 
	 * @return
	 * 		actually used view renderer.
	 */
	public TreeViewRenderer getTreeViewRenderer();
	
	/**
	 * Return the mouse controller used to highlight and select 
	 * node components
	 * 
	 * @return
	 * 		mouse controller for node components
	 */
   	public TreeMouseController getTreeMouseController();
   	
   	/**
	 * Return the layout manager for the view
	 * 
	 * @return
	 * 		layout manager
	 */
   	public TreeLayoutManager getTreeLayoutManager();
	
   	/**
	 * Return the tree component manager.
	 * This manager gives access to the node components.
	 * 
	 * @return
	 * 		node component manager
	 */
	public TreeComponentManager getTreeComponentManager();

	/**
	 * Return the selection manager for node components
	 * 
	 * @return
	 * 		selection manager for node components
	 */
	public TreeSelectionManager getTreeSelectionManager();
	
	/**
	 * Return the color manager for node components
	 * 
	 * @return
	 * 		color manager for node components
	 */
	public TreeColorManager getTreeColorManager();
   	
	/**
	 * Return the font manager for node components 
	 * 
	 * @return
	 * 		font manager for node components
	 */
   	public FontManager<NodeComponent> getTreeFontManager();
   	
	/**
	 * Return the stroke manager for node components 
	 * 
	 * @return
	 * 		stroke manager for node components
	 */
   	public TreeStrokeManager getTreeStrokeManager();

   	/**
   	 * Wrapper around the TreeComponentManager to get a node component based 
   	 * on its backend data structure.
   	 * 
   	 * @param node
   	 * 		the backend tree node data which graphial node component is requested
   	 * @return
   	 * 		the node component for the specified node 
   	 */
	public NodeComponent getNodesComponent(TreeNodeI node);

}
