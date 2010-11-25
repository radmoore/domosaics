package angstd.ui.views.domaintreeview;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.sequence.SequenceI;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domaintreeview.components.DomainEventComponent;
import angstd.ui.views.domaintreeview.io.DomainTreeViewExporter;
import angstd.ui.views.domaintreeview.layout.DefaultDomainTreeLayout;
import angstd.ui.views.domaintreeview.layout.DomainTreeLayout;
import angstd.ui.views.domaintreeview.manager.CSAInSubtreeManager;
import angstd.ui.views.domaintreeview.manager.DomainEventComponentManager;
import angstd.ui.views.domaintreeview.manager.DomainTreeLayoutManager;
import angstd.ui.views.domaintreeview.manager.DomainTreeViewManagerFactory;
import angstd.ui.views.domaintreeview.manager.InnerNodeArrangementManager;
import angstd.ui.views.domaintreeview.manager.DomainTreeViewManagerFactory.DomainTreeViewManager;
import angstd.ui.views.domaintreeview.mousecontroller.DomainEventMouseController;
import angstd.ui.views.domaintreeview.mousecontroller.DomainTreeMouseControllerFactory;
import angstd.ui.views.domaintreeview.mousecontroller.DomainTreeMouseControllerFactory.DomainTreeMouseControllerType;
import angstd.ui.views.domaintreeview.renderer.DefaultDomainTreeViewRenderer;
import angstd.ui.views.domaintreeview.renderer.additional.CollapseNumberRenderer;
import angstd.ui.views.domaintreeview.renderer.additional.DomainEventTooltipRenderer;
import angstd.ui.views.domaintreeview.renderer.additional.InDelRenderer;
import angstd.ui.views.domaintreeview.renderer.additional.InnerNodeArrangementRenderer;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.layout.DomainLayout;
import angstd.ui.views.domainview.manager.CollapseSameArrangementsManager;
import angstd.ui.views.domainview.manager.DomainArrangementComponentManager;
import angstd.ui.views.domainview.manager.DomainColorManager;
import angstd.ui.views.domainview.manager.DomainLayoutManager;
import angstd.ui.views.domainview.manager.DomainSearchOrthologsManager;
import angstd.ui.views.domainview.manager.DomainShapeManager;
import angstd.ui.views.domainview.manager.DomainShiftManager;
import angstd.ui.views.domainview.manager.DomainSimilarityManager;
import angstd.ui.views.domainview.manager.NoteManager;
import angstd.ui.views.domainview.manager.DomainArrangementComponentManager.DomainComponentManager;
import angstd.ui.views.domainview.manager.DomainViewManagerFactory.DomainViewManager;
import angstd.ui.views.domainview.mousecontroller.ArrangementMouseController;
import angstd.ui.views.domainview.mousecontroller.DomainMouseController;
import angstd.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;
import angstd.ui.views.domainview.mousecontroller.ShiftComponentsMouseController;
import angstd.ui.views.domainview.renderer.DomainViewRenderer;
import angstd.ui.views.domainview.renderer.additional.NoteMarkRenderer;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.treeview.components.TreeMouseController;
import angstd.ui.views.treeview.layout.TreeLayout;
import angstd.ui.views.treeview.manager.TreeColorManager;
import angstd.ui.views.treeview.manager.TreeComponentManager;
import angstd.ui.views.treeview.manager.TreeLayoutManager;
import angstd.ui.views.treeview.manager.TreeSelectionManager;
import angstd.ui.views.treeview.manager.TreeStrokeManager;
import angstd.ui.views.treeview.renderer.TreeViewRenderer;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.View;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.manager.DefaultSelectionManager;
import angstd.ui.views.view.manager.FontManager;
import angstd.ui.views.view.manager.SelectionManager;
import angstd.ui.views.view.manager.ViewManager;
import angstd.ui.views.view.renderer.Renderer;

/**
 * The DomainTreeView handles the visualization and manipulation of
 * a combined tree and domain view where the domain arrangements
 * are associated to tree nodes using their id. 
 * <p>
 * The view should be created via the ViewManager which wraps around the
 * AngstdViewFactory. The main initialization takes place when
 * the backend views are set.
 * <p>
 * The views components are managed by manager classes which are
 * initialized when the backend views are set. When
 * initializing a manager the {@link DomainTreeViewManagerFactory}
 * is used and a mapping between the view manager type and the manager 
 * is made to guarantee fast access to each manager. <br>
 * When extending the view by a new manager, the manager has to be added
 * to the DomainTreeViewManagerFactory and get methods have to be specified in 
 * this view as well as in the DomainTreeView interface {@link DomainTreeViewI}
 * in the same manner as the existing ones.
 * <p>
 * Mouse controllers which are possibly needed by this view are 
 * initialized using a DomainTreeMouseControllerFactory and stored
 * inside a mapping using the DomainTreeMouseControllerType as key.
 * Therefore fast access to an mouse controller is provided.
 * The initialization which is done when the backend views are set 
 * has nothing to do with the registering of mouse controllers,
 * because depending on the views state different controller must
 * be active / inactive. The register- and removeMouseListener
 * methods are then used to register a listener to the view. The
 * registering process uses settings from the LayoutManagers 
 * to ensure the registering of correct listeners depending on the views
 * state. 
 * <p>
 * In AbstractView the handling of additional view renderer is
 * described (e.g. InDelRenderer). Those additional renderer are 
 * also initialized when the backend views are set for the view. 
 * When adding new renderer to the view pay attention to the order in 
 * which the renderer are added.
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainTreeView extends AbstractView implements PropertyChangeListener, DomainTreeViewI {
	private static final long serialVersionUID = 1L;

	/** the scroll pane which embeds the view */
	protected JScrollPane scrollPane;
	
	/** the domain tree (a normal tree with arrangements associated to its nodes */
	protected TreeI domTree;
	
	/** the backend data set consisting of arrangements */
	protected DomainArrangement[] daSet;
	
	/** the backend domain view */
	protected DomainViewI domView;
	
	/** the backend tree view */
	protected TreeViewI treeView;
	
	/** 
	 * a map of all added view managers defined within the 
	 * DomainTreeViewManagerFactory. By using the
	 * type as key easy access to each manager is provided 
	 */
	protected Map<DomainTreeViewManager, ViewManager> view_manager;
	
	/** the layout manager for this view */
	protected DomainTreeLayoutManager domTreeLayoutManager;
	
	/** layout manager of the backend tree view */
	protected TreeLayoutManager treeLayoutManager;
	
	/** layout manager of the backend domain view */
	protected DomainLayoutManager domLayoutManager;
	
	/** the actually used layout to compute the component positions */
	protected ViewLayout viewLayout;
	
	/** the actually used renderer to render the view components */
	protected Renderer viewRenderer; //DefaultDomainTreeViewRenderer
	
	/** 
	 * a map of all possible mouse controller defined within the 
	 * DomainTreeMouseControllerFactory. By using the
	 * type as key easy access to each mouse event handler is provided 
	 */
	protected Map<DomainTreeMouseControllerType, MouseAdapter> mouse_controller;
	

	/**
	 * Basic DomainTreeView constructor initializing the manager 
	 * mapping and creating the view embedding scroll pane.
	 * The initialization of the rest is done when the backend views are set.
	 */
	public DomainTreeView () { 
		super();
		setFocusable(true);
		setAutoscrolls(true);
		
		view_manager = new HashMap<DomainTreeViewManager, ViewManager>();
		mouse_controller = new HashMap<DomainTreeMouseControllerType, MouseAdapter>();
		
		// set up the scrollPane
		scrollPane = new JScrollPane(super.getComponent());
		scrollPane.setFocusable(false);
	
		// set up the border and layout
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	/**
	 * @see View
	 */
	public void export(File file) {
		new DomainTreeViewExporter().write(file, this);
//		setChanged(false);
	}
	
	
	/**
	 * @see DomainTreeViewI
	 */
	public DomainViewI getDomainView() {
		return domView;
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public TreeViewI getTreeView() {
		return treeView;
	}
	
	/* *********************************************************** *
	 *   					MANAGING BACKEND DATA				   *
	 * *********************************************************** */
	
	/**
	 * @see TreeViewI
	 */
	public void setTree(TreeI tree) {
		this.domTree = tree;
	}
	
	/**
	 * @see DomainViewI
	 */
	public void setDaSet(DomainArrangement[] daSet) {
		this.daSet = daSet;
	}
	
	/**
	 * @see DomainViewI
	 */
	public void removeArrangement(DomainArrangement da) {
		// convert daSet to a list structure
		List<DomainArrangement> list = new ArrayList<DomainArrangement>(Arrays.asList(daSet));
		if (!list.contains(da))
			return;
		
		// get the corresponding tree node and remove it from the tree
		TreeNodeI node = domTree.getNode4DA(da);
		
		// TODO if the view would be added as listener to a tree it could react directly on changes in the underlying data structure (e.g. if in the deletion process more than one node is deleted)
		List<TreeNodeI> deletedNodes = domTree.deleteNode(node);
		if (deletedNodes.size() == 0) {
			MessageUtil.showWarning("Couldn't delete node, e.g. because the node is a child of the root");
			return;
		}
			
		for (TreeNodeI deleted : deletedNodes)
			getTreeComponentManager().removeComponent(deleted);
		
		// remove the components from the component manager
		getArrangementComponentManager().removeComponent(da);
		for (Domain dom : da.getDomains())
			getDomainComponentManager().removeComponent(dom);

		// and from the data set
		list.remove(da);
		
		// re convert the daSet list to an array
		daSet = list.toArray(new DomainArrangement[list.size()]);
	}
	
	/**
	 * @see DomainViewI
	 */
	public void addArrangement(DomainArrangement da) {
		System.out.println("not supported yet");
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public void setBackendViews(TreeViewI treeView, DomainViewI domView) {
		// keep a reference to the backend views
		this.treeView = treeView;
		this.domView = domView;

		/* 
		 * create the "domain tree" and keep the new dataset containing
		 * possible cloned arrangements but exclusive inner nodes in the 
		 * current implementation
		 */
		this.domTree = treeView.getTree();
		domTree.loadDasIntoTree(domView.getDaSet());
		this.daSet = domTree.getDaSet();	
		
		getDomainComponentManager().clear();
		getArrangementComponentManager().clear();
		
		// init the manager, mouse controller and additional renderer 
		initDomainTreeController();
		
		// next set the vibility status for the arrangements corresponding the nodes visibility status
		setVisibleStatusForDas();

		// set up layout
		setViewLayout(new DefaultDomainTreeLayout());
		
		// set up the main view renderer
		setViewRenderer(new DefaultDomainTreeViewRenderer(this));
		
		if(getSequences().length != 0) {
			// set the new icon within the workspace (associated icon)
			viewInfo.setUsedIcon(viewInfo.getAssociatedIcon());
			WorkspaceManager.getInstance().forceRepaint();
		}
			

		doLayout();
		repaint();
	}
	
	/**
	 * Helper method to ensure the correct visibility status for 
	 * arrangements. The visibility status must be the same as the
	 * node components visibility status.
	 */
	private void setVisibleStatusForDas() {
		// first make sure that all arrangements are invisible
		for (int i = 0; i < daSet.length; i++) 
			getArrangementComponentManager().getComponent(daSet[i]).setVisible(false);
			
		// then iterate over domain tree and assign visibility corresponding to the node visibility
		Iterator<TreeNodeI> iter = domTree.getNodeIterator();
		while (iter.hasNext()) {
			TreeNodeI dtn = iter.next();
			if (dtn.hasArrangement()) {
				ArrangementComponent dac = getArrangementComponentManager().getComponent(dtn.getArrangement());
				dac.setVisible(getTreeComponentManager().getComponent(dtn).isVisible());
			}		
		}
	}
	
	/**
	 * @see DomainViewI
	 */
	public SequenceI[] getSequences() {
		List<SequenceI> seqs = new ArrayList<SequenceI>();
		for (int i = 0; i < daSet.length; i++) 
			if (daSet[i].getSequence() != null) 
				seqs.add(daSet[i].getSequence());
		return seqs.toArray(new SequenceI[seqs.size()]);
	}
	
	/**
	 * @see DomainViewI
	 */
	public void loadSequencesIntoDas(SequenceI[] seqs, DomainArrangement[] daSet) {
		domView.loadSequencesIntoDas(seqs, daSet);
		
		// set the new icon within the workspace (associated icon)
		viewInfo.setUsedIcon(viewInfo.getAssociatedIcon());
		WorkspaceManager.getInstance().forceRepaint();
	}
	
	/**
	 * @see DomainViewI
	 */
	public void setSequencesLoaded (boolean flag) {
		domView.setSequencesLoaded(flag);
		
		if (isSequenceLoaded() && (!viewInfo.getIcon().equals(viewInfo.getAssociatedIcon()))) {
			viewInfo.setUsedIcon(viewInfo.getAssociatedIcon());
			WorkspaceManager.getInstance().forceRepaint();
			return;
		}
		if (!isSequenceLoaded() && (viewInfo.getIcon().equals(viewInfo.getAssociatedIcon()))) {
			viewInfo.setUsedIcon(viewInfo.getDefaultIcon());
			WorkspaceManager.getInstance().forceRepaint();
			return;
		}
	}
	
	/* *********************************************************** *
	 *   						 INITIALIZING METHODS			   *
	 * *********************************************************** */
	
	/**
	 * Helper method to initialize all needed controlling instances
	 * for view components. The view specific managers, mouse
	 * controller and renderer are initialized here.
	 */
	protected void initDomainTreeController() {
		// init all view manager
		for (DomainTreeViewManager manager : DomainTreeViewManager.values())
			addViewManager(manager, DomainTreeViewManagerFactory.create(manager, this));

		// as well as the layout manager
		domTreeLayoutManager = new DomainTreeLayoutManager(viewInfo.getActionManager());
		domLayoutManager = new DomainLayoutManager(viewInfo.getActionManager());
		treeLayoutManager = new TreeLayoutManager(viewInfo.getActionManager());
		
		domTreeLayoutManager.addPropertyChangeListener(this);
		domLayoutManager.addPropertyChangeListener(this);
		treeLayoutManager.addPropertyChangeListener(this);
		
		// init the mouse listeners
		for (DomainTreeMouseControllerType mouseControllerType : DomainTreeMouseControllerType.values())
			addMouseController(mouseControllerType, DomainTreeMouseControllerFactory.create(mouseControllerType, this));
		
		// register the domainTreeView as listener for the manager changes in dom and tree view
		registerMouseListeners();
		registerViewAsManagerListener(this);
		registerAdditionalDomainTreeRenderer(this);
	}
	
	/**
	 * @see TreeViewI
	 */
	public void registerAdditionalTreeRenderer(TreeViewI view) {
		treeView.registerAdditionalTreeRenderer(view);
	}
	
	/**
	 * @see DomainViewI
	 */
	public void registerAdditionalDomainRenderer(DomainViewI view) {
		domView.registerAdditionalDomainRenderer(view);
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public void registerAdditionalDomainTreeRenderer(DomainTreeViewI view) {
		removeAllRenderer();
		
		if (getDomainTreeLayoutManager().isShowTree()) 
			registerAdditionalTreeRenderer(this);
		
		
		if (getDomainTreeLayoutManager().isShowArrangements()) 
			registerAdditionalDomainRenderer(this);
			
		
		if (getDomainTreeLayoutManager().isShowTree() && getDomainTreeLayoutManager().isShowArrangements()) {
			view.addRenderer(new InDelRenderer(view));
			view.addRenderer(new DomainEventTooltipRenderer(view));
			view.addRenderer(new CollapseNumberRenderer(view));
			view.addRenderer(new InnerNodeArrangementRenderer(view));
		}
		
	}
	
	
	/* ******************************************************************* *
	 *   						HANDLING THE MOUSE CONTROLLER			   *
	 * ******************************************************************* */
	
	/**
	 * The registering of mouse listeners are done based on 
	 * actual settings within the layoutManagers. 
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
		
		// only tree controller
		if (!getDomainTreeLayoutManager().isShowArrangements()) {
			addMouseListener(getTreeMouseController());
			addMouseMotionListener(getTreeMouseController());
			return;
		}
		
		// check if sequence selection is active if so add just this mouse controller
		if (getDomainLayoutManager().isSelectSequences()) {
			addMouseListener(getSequenceSelectionMouseController());
			addMouseMotionListener(getSequenceSelectionMouseController());
			return;
		}
		
		// domain view mouse controller
		addMouseListener(getArrangementMouseController());
		addMouseMotionListener(getArrangementMouseController());
		addMouseListener(getDomainMouseController());
		addMouseMotionListener(getDomainMouseController());
		addMouseListener(getShiftComponentsMouseController());
		
		if (!getCSAInSubtreeManager().isActive())
			addMouseMotionListener(getShiftComponentsMouseController());
		
		
		if (getDomainTreeLayoutManager().isShowTree()) {
			// tree mouse controller
			addMouseListener(getTreeMouseController());
			addMouseMotionListener(getTreeMouseController());
			
			// domain tree mouse controller
			addMouseListener(getDomainEventMouseController());
			addMouseMotionListener(getDomainEventMouseController());
		}
	}

	/**
	 * Adds a mouse controller to the view (but the registering is not done)
	 * This method is used to fill the mouseController map during
	 * the views initialization.
	 * 
	 * @param type
	 * 		type of he mouse controller defined in DomainTreeMouseControllerFactory
	 * @param mc
	 * 		the mouse controller to add
	 */
	public void addMouseController(DomainTreeMouseControllerType type, MouseAdapter mc) {
		mouse_controller.put(type, mc);
	}
	
	/**
	 * Removes a mouse controller from the map of available mouse
	 * controller. This method has nothing to with register or unregistering
	 * of mouse controller.
	 * 
	 * @param type
	 * 		the type of mouse controller to be removed
	 */
	public void removeMouseController(DomainTreeMouseControllerType type) {
		mouse_controller.remove(type);
	}
	
	/**
	 * Helper method for accessing a mouse controller based on its type
	 * and already cast to its correct type by using generic.
	 * 
	 * @param <M>
	 * 		the mouse controller class extending MouseAdapter
	 * @param type
	 * 		the DomainTreeMouseControllerType used to get the controller object
	 * @return
	 * 		the mouse controller based on the specified type already cast
	 */
	@SuppressWarnings("unchecked")
	protected <M extends MouseAdapter> M getMouseController(DomainTreeMouseControllerType type) {
		return (M) mouse_controller.get(type);
	}

	/**
	 * @see DomainViewI
	 */
	public ArrangementMouseController getArrangementMouseController() {
		return getMouseController(DomainTreeMouseControllerType.ARRANGEMENTMC);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainMouseController getDomainMouseController() {
		return getMouseController(DomainTreeMouseControllerType.DOMAINMC);
	}
	
	/**
	 * @see DomainViewI
	 */
	public SequenceSelectionMouseController getSequenceSelectionMouseController() {
		return getMouseController(DomainTreeMouseControllerType.SEQUENCESELECTIONMC);
	}
	
	/**
	 * @see DomainViewI
	 */
	public ShiftComponentsMouseController getShiftComponentsMouseController() {
		return getMouseController(DomainTreeMouseControllerType.COMPONENTSHIFTMC);
	}

	/**
	 * @see TreeViewI
	 */
	public TreeMouseController getTreeMouseController() {
		return getMouseController(DomainTreeMouseControllerType.TREEMC);
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public DomainEventMouseController getDomainEventMouseController() {
		return getMouseController(DomainTreeMouseControllerType.DOMEVENTMC);
	}


	/* ******************************************************************* *
	 *   						PROPERTY CHANGE METHODS					   *
	 * ******************************************************************* */
	
	/**
	 * @see TreeViewI
	 */
	public void registerViewAsManagerListener(PropertyChangeListener view) {
		Iterator <ViewManager> iter = view_manager.values().iterator();
		while (iter.hasNext()) 
			iter.next().addPropertyChangeListener(view);
		
		treeView.registerViewAsManagerListener(view);
		domView.registerViewAsManagerListener(view);
	}

	/**
	 * @see TreeViewI
	 */
	public void unregisterViewAsManagerListener(PropertyChangeListener view) {
		Iterator <ViewManager> iter = view_manager.values().iterator();
		while (iter.hasNext()) 
			iter.next().removePropertyChangeListener(view);
		
		treeView.unregisterViewAsManagerListener(view);
		domView.unregisterViewAsManagerListener(view);
	}
	
	/**
	 * This method is triggered when one of the associated view manager
	 * fires a property change. In the case of a structural change
	 * a relayout and a repaint is done. In the case of a visual
	 * change only the repaint is done.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewManager.PROPERTY_STRUCTURAL_CHANGE)) {
			if (viewLayout instanceof TreeLayout)
				((TreeLayout) viewLayout).treeStructureChanged();
			doLayout();
		}
		repaint(); //implicit PROPERTY_VISUAL_CHANGE
	}
	
	/**
	 * Adds a new view manager to the view
	 * 
	 * @param name
	 * 		the view managers type used to identify it for quick access
	 * @param manager
	 * 		the new view manager to be associated with the view
	 */
	public void addViewManager(DomainTreeViewManager name, ViewManager manager) {
		this.view_manager.put(name, manager);
	}
	
	/**
	 * Removes a view manager from the view
	 * 
	 * @param name
	 * 		the view managers type used to remove the manager
	 */
	public void removeViewManager(DomainTreeViewManager name) {
		this.view_manager.remove(name);
	}
	
	
	/* ************************************************************** *
	 *   					LAYOUT AND RENDERING METHODS		      *
	 * ************************************************************** */
    
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
		
		if (layout instanceof DomainTreeLayout) {
			this.viewLayout = (DomainTreeLayout) layout;
			return;
		}
		
		if (layout instanceof TreeLayout && !getDomainTreeLayoutManager().isShowArrangements()) {
			this.viewLayout = (TreeLayout) layout;
			return;
		}
		
		if (layout instanceof DomainLayout) {
			if (!getDomainTreeLayoutManager().isShowTree())
				this.viewLayout = (DomainLayout) layout;
			else {
				((DomainTreeLayout) this.viewLayout).setDomainLayout((DomainLayout) layout);
			}	
			return;
		}	
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
		domTreeLayoutManager.toggleZoomMode((AbstractView) this);
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
		return domTree;
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainArrangement[] getDaSet() {
		return daSet;
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public TreeI getDomTree() {
		return domTree;
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeViewRenderer getTreeViewRenderer() {
		return (TreeViewRenderer) viewRenderer;
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainViewRenderer getDomainViewRenderer() {
		return (DomainViewRenderer) viewRenderer;
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeLayout getTreeLayout() {
		return (TreeLayout) viewLayout;
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainLayout getDomainLayout() {
		if (viewLayout instanceof DomainTreeLayout)
			return ((DomainTreeLayout) viewLayout).getDomainLayout();
		return (DomainLayout) viewLayout;
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public DomainTreeLayout getDomainTreeLayout() {
		return (DomainTreeLayout) viewLayout;
	}
	
	/**
	 * @see DomainViewI
	 */
	public boolean isSequenceLoaded() {
		return domView.isSequenceLoaded();
	}
	
	/**
	 * @see TreeViewI
	 */
	public NodeComponent getNodesComponent(TreeNodeI node) {
		return treeView.getNodesComponent(node);
	}
	
	/* ******************************************************************* *
	 *   						 ACCESS MANAGER METHODS					   *
	 * ******************************************************************* */
	
	/**
	 * Helper method for accessing a domain manager based on its type
	 * and already cast to its correct type by using generic.
	 * 
	 * @param <M>
	 * 		the manager class implementing ViewManager
	 * @param type
	 * 		the DomainTreeManagerType used to get the manager object
	 * @return
	 * 		the manager based on the specified type already cast
	 */
	@SuppressWarnings("unchecked")
	protected <M extends ViewManager> M getManager(DomainTreeViewManager type) {
		return (M) view_manager.get(type);
	}
	
	/**
	 * Helper method for accessing a domain manager based on its type
	 * and already cast to its correct type by using generic. Returns always null
	 * because its not needed for the DomainTreeView.
	 * 
	 * @param <M>
	 * 		the manager class implementing ViewManager
	 * @param type
	 * 		the DomainManagerType used to get the manager object
	 * @return
	 * 		the manager based on the specified type already cast
	 */
	public <M extends ViewManager> M getViewManager(DomainViewManager type) {
		return null;
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeLayoutManager getTreeLayoutManager() {
		return treeLayoutManager;
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainLayoutManager getDomainLayoutManager() {
		return domLayoutManager;
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public DomainTreeLayoutManager getDomainTreeLayoutManager() {
		return domTreeLayoutManager; 
	}
	
	/* TREE MANAGER */
	
	/**
	 * @see TreeViewI
	 */
	public TreeColorManager getTreeColorManager() {
		return treeView.getTreeColorManager();
	}

	/**
	 * @see TreeViewI
	 */
	public TreeComponentManager getTreeComponentManager() {
		return treeView.getTreeComponentManager();
	}

	/**
	 * @see TreeViewI
	 */
	public FontManager<NodeComponent> getTreeFontManager() {
		return treeView.getTreeFontManager();
	}
	
	/**
	 * @see TreeViewI
	 */
	public TreeSelectionManager getTreeSelectionManager() {
		return treeView.getTreeSelectionManager();
	}

	/**
	 * @see TreeViewI
	 */
	public TreeStrokeManager getTreeStrokeManager() {
		return treeView.getTreeStrokeManager();
	}
	
	/* DOMAIN MANAGER */

	/**
	 * @see DomainViewI
	 */
	public DomainColorManager getDomainColorManager() {
		return domView.getDomainColorManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainArrangementComponentManager getArrangementComponentManager() {
		return domView.getArrangementComponentManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainComponentManager getDomainComponentManager() {
		return domView.getDomainComponentManager();
	} 
	
	/**
	 * @see DomainViewI
	 */
	public SelectionManager<DomainComponent> getDomainSelectionManager() {
		return domView.getDomainSelectionManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public FontManager<ArrangementComponent> getDomainArrangementFontManager() {
		return domView.getDomainArrangementFontManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public FontManager<DomainComponent> getDomainFontManager() {
		return domView.getDomainFontManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainShapeManager getDomainShapeManager() {
		return domView.getDomainShapeManager();
	}

	/**
	 * @see DomainViewI
	 */
	public SelectionManager<ArrangementComponent> getArrangementSelectionManager() {
		return domView.getArrangementSelectionManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainSearchOrthologsManager getDomainSearchOrthologsManager() {
		return domView.getDomainSearchOrthologsManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainShiftManager getDomainShiftManager() {
		return domView.getDomainShiftManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public CollapseSameArrangementsManager getCollapseSameArrangementsManager() {
		return domView.getCollapseSameArrangementsManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainSimilarityManager getDomainSimilarityManager() {
		return domView.getDomainSimilarityManager();
	}
	
	/**
	 * @see DomainViewI
	 */
	public NoteManager getNoteManager() {
		return domView.getNoteManager();
	}
	
	/* DOMAINTREE MANAGER */
	
	/**
	 * @see DomainTreeViewI
	 */
	public CSAInSubtreeManager getCSAInSubtreeManager() {
		return getManager(DomainTreeViewManager.COLLAPSESAMEARRANGEMENTSINSUBTREEMANAGER);
	}

	/**
	 * @see DomainTreeViewI
	 */
	public DefaultSelectionManager<DomainEventComponent> getDomainEventSelectionManager() {
		return getManager(DomainTreeViewManager.DOMAINEVENTSELECTIONMANAGER);
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public DomainEventComponentManager getDomainEventComponentManager() {
		return getManager(DomainTreeViewManager.DOMAINEVENTCOMPONENTMANAGER);
	}
	
	/**
	 * @see DomainTreeViewI
	 */
	public InnerNodeArrangementManager getInnerNodeArrangementManager() {
		return getManager(DomainTreeViewManager.INNERNODEARRANGEMENTMANAGER);
	}

}
