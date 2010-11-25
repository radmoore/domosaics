package angstd.ui.views.treeview;

import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.treeview.components.TreeMouseController;
import angstd.ui.views.treeview.io.TreeViewExporter;
import angstd.ui.views.treeview.layout.DendogramLayout;
import angstd.ui.views.treeview.layout.TreeLayout;
import angstd.ui.views.treeview.manager.TreeColorManager;
import angstd.ui.views.treeview.manager.TreeComponentManager;
import angstd.ui.views.treeview.manager.TreeLayoutManager;
import angstd.ui.views.treeview.manager.TreeSelectionManager;
import angstd.ui.views.treeview.manager.TreeStrokeManager;
import angstd.ui.views.treeview.manager.TreeViewManagerFactory;
import angstd.ui.views.treeview.manager.TreeViewManagerFactory.TreeViewManager;
import angstd.ui.views.treeview.renderer.DefaultTreeViewRenderer;
import angstd.ui.views.treeview.renderer.TreeViewRenderer;
import angstd.ui.views.treeview.renderer.additional.EdgeSelectionRenderer;
import angstd.ui.views.treeview.renderer.additional.HighlightNodeRenderer;
import angstd.ui.views.treeview.renderer.additional.InnerNodeRenderer;
import angstd.ui.views.treeview.renderer.additional.NodeSelectionRenderer;
import angstd.ui.views.treeview.renderer.additional.SelectionRectangleRenderer;
import angstd.ui.views.treeview.renderer.additional.TreeLinealRenderer;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.View;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.manager.DefaultFontManager;
import angstd.ui.views.view.manager.ViewManager;
import angstd.ui.views.view.renderer.Renderer;

/**
 * The TreeView handles the visualization and manipulation of
 * a backend tree data structure which can be fed in for instance via
 * a Newick formated file. The view should be created using the ViewManager 
 * which wraps around the AngstdViewFactory. The main initialization takes 
 * place when the backend tree is set. Its also possible to 
 * associate a domain arrangement dataset to the view which uses id 
 * mapping and result in a DomainView. 
 * <p>
 * The views components are managed by manager classes which are
 * initialized when the backend tree is set to the view. When
 * initializing a manager the {@link TreeViewManagerFactory}
 * is used and a mapping between the view manager type and the manager 
 * is made to guarantee fast access to each manager. <br>
 * When extending the view by a new manager, the manager has to be added
 * to the TreeViewManagerFactory and get methods have to be specified in 
 * this view as well as in the TreeView interface {@linkTreeViewI}
 * in the same manner as the existing ones.
 * <p>
 * In AbstractView the handling of additional view renderer is
 * described (e.g. NodeSelectionRenderer). Those additional renderer are 
 * also initialized when the tree is set for the view. When adding
 * new renderer to the view pay attention to the order in which
 * the renderer are added.
 * <p>
 * To reach the panel which embeds the view the method {@link #getParentPane()}
 * has to be used.
 * <p>
 * Angstd handles bootstrap values which can be coded in newick format 
 * as node labels. Therefore the TreeComponentManager provides a method to
 * switch between the display of bootstrap values and edge weights.
 * If the user wants to show the bootstrap values the useLabelAsBootstrap()
 * method has to be invoked which manages the rest, e.g. converting the 
 * numeric labels from 0 - 100 into bootstrap values and therefore
 * into edge labels.
 * 
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class TreeView extends AbstractView implements TreeViewI, PropertyChangeListener{
	protected static final long serialVersionUID = 1L;
			
	/** the scroll pane which embeds the view */
	protected JScrollPane scrollPane;
	
	/** the backend tree displayed by this view */
	protected TreeI tree;
	
	/** 
	 * a map of all added view managers defined within the 
	 * TreeViewManagerFactory. By using the
	 * type as key easy access to each manager is provided 
	 */
	protected Map<TreeViewManager, ViewManager> view_manager;
	
	/** The mouse controller for this view */
	protected TreeMouseController treeMouseController;

	/** the layout manager for this view */
	protected TreeLayoutManager treeLayoutManager;
	
	/** the actually used layout to compute the component positions */
	protected TreeLayout viewLayout;
	
	/** the actually used renderer to render the view components */
	protected Renderer viewRenderer;
	
	
	/**
	 * Basic DomainView constructor initializing the manager mapping and
	 * creating the view embedding scroll pane.
	 * The initialization of the rest is done when the backend tree is set.
	 */
	public TreeView() {
		super();
		
		view_manager = new HashMap<TreeViewManager, ViewManager>();
		
		// set up the scrollPane
		scrollPane = new JScrollPane(super.getComponent());
		scrollPane.setFocusable(false);
	}
	
	/* *********************************************************** *
	 *   					MANAGING BACKEND DATA				   *
	 * *********************************************************** */
	
	/**
	 * @see TreeViewI
	 */
	public void setTree(TreeI tree) {
		this.tree = tree;
		initTreeController();
		
		setViewLayout(new DendogramLayout());
		viewRenderer = new DefaultTreeViewRenderer(this);
		
		doLayout();
		repaint();
	}
	
	/**
	 * @see View
	 */
	public void export(File file) {
		new TreeViewExporter().write(file, this);
//		setChanged(false);
	}
	
	/* ******************************************************************* *
	 *   						 INITIALIZING METHODS					   *
	 * ******************************************************************* */
	
	/**
	 * Helper method to initialize all needed controlling instances
	 * for view components. The view specific managers, mouse
	 * controller and renderer are initialized here.
	 */
	protected void initTreeController() {
		for (TreeViewManager manager : TreeViewManager.values()) 
			addViewManager(manager, TreeViewManagerFactory.create(manager, this));
		
		treeLayoutManager = new TreeLayoutManager(viewInfo.getActionManager());
		treeLayoutManager.addPropertyChangeListener(this);
		
		treeMouseController = new TreeMouseController(this);
		
		registerMouseListeners();
		registerViewAsManagerListener(this);
		registerAdditionalTreeRenderer(this);
	}
	
	/**
	 * @see TreeViewI
	 */
	public void registerAdditionalTreeRenderer(TreeViewI view) {
    	view.addRenderer(new TreeLinealRenderer(view));
    	view.addRenderer(new EdgeSelectionRenderer(view));
    	view.addRenderer(new InnerNodeRenderer(view));
    	view.addRenderer(new NodeSelectionRenderer(view));
    	view.addRenderer(new HighlightNodeRenderer(view));
    	view.addRenderer(new SelectionRectangleRenderer(view));   			
	}   
	
	/**
	 * The registering of mouse listeners are done based on 
	 * actual settings within the TreeLayoutManager. For instance
	 * if the zoom mode is enabled just this listener
	 * is added.
	 *  
	 * @see AbstractView
	 */
	public void registerMouseListeners() {
		// remove all listener before registering the new ones.
		removeMouseListeners();
		
		// use zoom mode listeners only (defined in ABstractView)
		if(isZoomMode()) {
			addZoomControlMouseListener();
			return;
		}
		
		addMouseListener(treeMouseController);
		addMouseMotionListener(treeMouseController);
	}
	
	/* ******************************************************************* *
	 *   						PROPERTY CHANGE METHODS					   *
	 * ******************************************************************* */

	/**
	 * Adds a new view manager to the view
	 * 
	 * @param name
	 * 		the view managers type used to identify it for quick access
	 * @param manager
	 * 		the new view manager to be associated with the view
	 */
	public void addViewManager(TreeViewManager name, ViewManager manager) {
		this.view_manager.put(name, manager);
	}
	
	/**
	 * Removes a view manager from the view
	 * 
	 * @param name
	 * 		the view managers type used to remove the manager
	 */
	public void removeViewManager(TreeViewManager name) {
		this.view_manager.remove(name);
	}
	
	/**
	 * @see TreeViewI
	 */
	public void registerViewAsManagerListener(PropertyChangeListener view) {
		Iterator <ViewManager> iter = view_manager.values().iterator();
		while (iter.hasNext()) 
			iter.next().addPropertyChangeListener(view);
	}
	
	/**
	 * @see TreeViewI
	 */
	public void unregisterViewAsManagerListener(PropertyChangeListener view) {
		Iterator <ViewManager> iter = view_manager.values().iterator();
		while (iter.hasNext()) 
			iter.next().removePropertyChangeListener(view);
	}
	
	/**
	 * This method is triggered when one of the associated view manager
	 * fires a property change. In the case of a structural change
	 * a relayout and a repaint is done. In the case of a visual
	 * change only the repaint is done.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewManager.PROPERTY_STRUCTURAL_CHANGE)) {
			viewLayout.treeStructureChanged();
			doLayout();
		}
		repaint(); //implicit PROPERTY_VISUAL_CHANGE
	}
	
	/* ******************************************************************* *
	 *   						 LAYOUTING AND RENDERING METHODS						   *
	 * ******************************************************************* */
	
	/**
	 * Triggered whenever a layout for the view components has to be
	 * done.
	 */
	public void doLayout() {
		if (isZoomMode())
			return;
		
		viewLayout.layoutContainer(this);
	}
	
	/**
	 * Sets the layout for this view used to calculate the positions
	 * on the screen for view components.
	 * 
	 * @param layout
	 * 		the new layout to be used
	 */
	public void setViewLayout(ViewLayout layout) {
		super.setLayout(null);
		layout.setView(this);
		this.viewLayout = (TreeLayout) layout;
	}
	
	/**
	 * @see View
	 */
	public void setViewRenderer(Renderer renderer) {
		this.viewRenderer = renderer;
	}
	
	/**
	 * @see AbstractView
	 */
	public void renderView(Graphics2D g) {
		viewRenderer.render(g);
	}
	
	/**
	 * @see AbstractView
	 */
	public void toggleZoomMode() {
		super.toggleZoomMode();
		treeLayoutManager.toggleZoomMode((AbstractView) this);
	}

	/* ******************************************************************* *
	 *   							GET methods							   *
	 * ******************************************************************* */
	
	/**
	 * @see View
	 */
	public JComponent getComponent() {
		return scrollPane;
	}

	/**
	 * @see TreeViewI
	 */
	public TreeI getTree() {
		return tree;
	}
	
	/**
	 * @see TreeViewI
	 */
	public NodeComponent getNodesComponent(TreeNodeI node) {
		return getTreeComponentManager().getComponent(node);
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeViewRenderer getTreeViewRenderer() {
		return (TreeViewRenderer) viewRenderer;
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeLayout getTreeLayout() {
		return (TreeLayout) viewLayout;
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeMouseController getTreeMouseController() {
		return treeMouseController;
	}
	
	/* ******************************************************************* *
	 *   						     MANAGER METHODS					   *
	 * ******************************************************************* */
	
	/**
	 * Helper method for accessing a tree manager based on its type
	 * and already cast to its correct type by using generic.
	 * 
	 * @param <M>
	 * 		the manager class implementing ViewManager
	 * @param type
	 * 		the TreeManagerType used to get the manager object
	 * @return
	 * 		the manager based on the specified type already cast
	 */
	@SuppressWarnings("unchecked")
	protected <M extends ViewManager> M getViewManager(TreeViewManager type) {
		return (M) view_manager.get(type);
	}

	/**
	 * @see TreeViewI
	 */
	public TreeLayoutManager getTreeLayoutManager() {
		return treeLayoutManager; 
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeColorManager getTreeColorManager() {
		return getViewManager(TreeViewManager.TREECOLORMANAGER);
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeComponentManager getTreeComponentManager() {
		return getViewManager(TreeViewManager.TREECOMPONENTMANAGER);
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeSelectionManager getTreeSelectionManager() {
		return getViewManager(TreeViewManager.TREESELECTIONMANAGER);
	}
	
	/**
	 * @see TreeViewI
	 */
	public DefaultFontManager<NodeComponent> getTreeFontManager() {
		return getViewManager(TreeViewManager.TREEFONTMANAGER);
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeStrokeManager getTreeStrokeManager() {
		return getViewManager(TreeViewManager.TREESTROKEMANAGER);
	}

}
