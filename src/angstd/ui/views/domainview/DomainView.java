 package angstd.ui.views.domainview;

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

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.sequence.SequenceI;
import angstd.ui.AngstdUI;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.components.SequenceMatchErrorFrame;
import angstd.ui.views.domainview.io.DomainViewExporter;
import angstd.ui.views.domainview.layout.DomainLayout;
import angstd.ui.views.domainview.layout.ProportionalLayout;
import angstd.ui.views.domainview.manager.CollapseSameArrangementsManager;
import angstd.ui.views.domainview.manager.DomainArrangementComponentManager;
import angstd.ui.views.domainview.manager.DomainColorManager;
import angstd.ui.views.domainview.manager.DomainLayoutManager;
import angstd.ui.views.domainview.manager.DomainSearchOrthologsManager;
import angstd.ui.views.domainview.manager.DomainShapeManager;
import angstd.ui.views.domainview.manager.DomainShiftManager;
import angstd.ui.views.domainview.manager.DomainSimilarityManager;
import angstd.ui.views.domainview.manager.DomainViewManagerFactory;
import angstd.ui.views.domainview.manager.NoteManager;
import angstd.ui.views.domainview.manager.DomainArrangementComponentManager.DomainComponentManager;
import angstd.ui.views.domainview.manager.DomainViewManagerFactory.DomainViewManager;
import angstd.ui.views.domainview.mousecontroller.ArrangementMouseController;
import angstd.ui.views.domainview.mousecontroller.DomainMouseController;
import angstd.ui.views.domainview.mousecontroller.DomainMouseControllerFactory;
import angstd.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;
import angstd.ui.views.domainview.mousecontroller.ShiftComponentsMouseController;
import angstd.ui.views.domainview.mousecontroller.DomainMouseControllerFactory.DomainMouseControllerType;
import angstd.ui.views.domainview.renderer.DefaultDomainViewRenderer;
import angstd.ui.views.domainview.renderer.DomainViewRenderer;
import angstd.ui.views.domainview.renderer.additional.CollapseNumberRenderer;
import angstd.ui.views.domainview.renderer.additional.DomainRulerRenderer;
import angstd.ui.views.domainview.renderer.additional.DomainTooltipRenderer;
import angstd.ui.views.domainview.renderer.additional.HighlightArrangementRenderer;
import angstd.ui.views.domainview.renderer.additional.HighlightDomainRenderer;
import angstd.ui.views.domainview.renderer.additional.NoteMarkRenderer;
import angstd.ui.views.domainview.renderer.additional.SequenceSelectionRenderer;
import angstd.ui.views.domainview.renderer.additional.SimilarityLabelRenderer;
import angstd.ui.views.domainview.renderer.additional.SimilarityRenderer;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.View;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.manager.FontManager;
import angstd.ui.views.view.manager.SelectionManager;
import angstd.ui.views.view.manager.ViewManager;
import angstd.ui.views.view.renderer.Renderer;

/**
 * The DomainView handles the visualization and manipulation of
 * a backend dataset consisting of domain arrangements. The view
 * should be created via the ViewManager which wraps around the
 * AngstdViewFactory. The main initialization takes place when
 * the backend dataset is set. Its also possible to 
 * associate sequences to the view which uses id mapping and changes
 * the underlying backend data by adding the sequences to the 
 * corresponding domain arrangements.
 * <p>
 * The views components are managed by manager classes which are
 * initialized when the backend dataset is set to the view. When
 * initializing a manager the {@link DomainViewManagerFactory}
 * is used and a mapping between the view manager type and the manager 
 * is made to guarantee fast access to each manager. <br>
 * When extending the view by a new manager, the manager has to be added
 * to the DomainViewManagerFactory and get methods have to be specified in 
 * this view as well as in the DomainView interface {@link DomainViewI}
 * in the same manner as the existing ones.
 * <p>
 * Mouse controllers which are possibly needed by this view are 
 * initialized using a DomainMouseControllerFactory and stored
 * inside a mapping using the DomainMouseControllerType as key.
 * Therefore fast access to an mouse controller is provided.
 * This initialization which is done when the dataset is set 
 * has nothing to do with the registering of mouse controllers,
 * because depending on the views state different controller must
 * be active / inactive. The register- and removeMouseListener
 * methods are then used to register a listener to the view. The
 * registering process uses settings from the DomainLayoutManager 
 * to ensure the registering of correct listeners depending on the views
 * state. <br>
 * To add new mouse listener the listener class has to be implemented,
 * two entries must be added to the DomainLayoutManager and the
 * registerMouseListener method has to be changed. 
 * <p>
 * In AbstractView the handling of additional view renderer is
 * described (e.g. DomainToolTipRenderer). Those additional renderer are 
 * also initialized when the dataset is set for the view. When adding
 * new renderer to the view pay attention to the order in which
 * the renderer are added.
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainView extends AbstractView implements DomainViewI, PropertyChangeListener {
	protected static final long serialVersionUID = 1L;
	
	/** the backend data set displayed by this view */
	protected DomainArrangement[] daSet;
	
	/** the scroll pane which embeds the view */
	protected JScrollPane scrollPane;
	
	/** the layout manager for this view */
	protected DomainLayoutManager domLayoutManager;
	
	/** the actually used layout to compute the component positions */
	protected DomainLayout viewLayout;
	
	/** the actually used renderer to render the view components */
	protected Renderer viewRenderer;

	/** flag indicating whether or not sequences were associated with domains */
	protected boolean sequencesLoaded = false;

	/** flag indicating whether or not the domain sequence comparison is active */
	protected boolean isCompareDomainsMode = false;
	/** 
	 * a map of all added view managers defined within the 
	 * DomainViewManagerFactory. By using the
	 * type as key easy access to each manager is provided 
	 */
	protected Map<DomainViewManager, ViewManager> view_manager;
	
	/** 
	 * a map of all possible mouse controller defined within the 
	 * DomainMouseControllerFactory. By using the
	 * type as key easy access to each mouse event handler is provided 
	 */
	protected Map<DomainMouseControllerType, MouseAdapter> mouse_controller;
	
	
	/**
	 * Basic DomainView constructor initializing the manager mapping and
	 * creating the view embedding scroll pane.
	 * The initialization of the rest is done when the data set is set.
	 */
	public DomainView () {
		super();
	
		view_manager = new HashMap<DomainViewManager, ViewManager>();
		mouse_controller = new HashMap<DomainMouseControllerType, MouseAdapter>();
		
		// set up the scrollPane which embeds the view
		scrollPane = new JScrollPane(super.getComponent());
		scrollPane.setFocusable(false);
	}
	
	/* *********************************************************** *
	 *   					MANAGING BACKEND DATA				   *
	 * *********************************************************** */
	
	/**
	 * @see DomainViewI
	 */
	public void setDaSet(DomainArrangement[] daSet) {
		// TODO handle the setting of a new dataset of a view
		if (this.daSet != null) {
			System.out.println("Tried to set a new dataset for an existing view. This case is not handled yet");
			return;
		}
		
		// initialize the domain controller, e.g. manager, mouse listener, additional renderer...
		initDomainController();
		
		// set the backend data, default layout and the main view renderer
		this.daSet = daSet;
		setViewLayout(new ProportionalLayout());
		viewRenderer = new DefaultDomainViewRenderer(this);

		doLayout();
		repaint();
	}
	
	/**
	 * @see View
	 */
	public void export(File file) {
		new DomainViewExporter().write(file, this);
//		setChanged(false);
	}
	
	/**
	 * @see DomainViewI
	 */
	public void removeArrangement(DomainArrangement da) {
		// convert daSet to a list structure
		List<DomainArrangement> list = new ArrayList<DomainArrangement>(Arrays.asList(daSet));
		if (!list.contains(da))
			return;
		
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
		// convert daSet to a list structure
		List<DomainArrangement> list = new ArrayList<DomainArrangement>(Arrays.asList(daSet));
		if (list.contains(da))
			return;
		
		list.add(da);
	
		// re convert the daSet list to an array
		daSet = list.toArray(new DomainArrangement[list.size()]);
	}
	
	
	/**
	 * @see DomainViewI
	 */
	public void addDaSet(DomainArrangement[] newDaSet) {
		List<DomainArrangement> currentArrangements = new ArrayList<DomainArrangement>(Arrays.asList(daSet));
		
		for (DomainArrangement da : newDaSet) {
			if (currentArrangements.contains(da))
				continue;
			currentArrangements.add(da);
		}
			
		daSet = currentArrangements.toArray(new DomainArrangement[currentArrangements.size()]);
	}
	
	
	/**
	 * @see DomainViewI
	 */
	public void loadSequencesIntoDas(SequenceI[] seqs, DomainArrangement[] daSet) {
		List<DomainArrangement> noMatchDAs = new ArrayList<DomainArrangement>();
		List<SequenceI> noMatchSeqs = new ArrayList<SequenceI>();
		
		// first create a mapping between sequences and their names to get fast access to each sequence using its name
		Map<String, SequenceI> label2Seq = new HashMap<String, SequenceI>();
		for (int i = 0; i < seqs.length; i++) {    
			label2Seq.put(seqs[i].getName().toUpperCase(), seqs[i]);
			noMatchSeqs.add(seqs[i]);
		}
		
		// then assign the sequences to the DAs
		int noMatchCount = 0;
		for (int i = 0; i < daSet.length; i++) 
			if (label2Seq.get(daSet[i].getName().toUpperCase()) != null) {
				daSet[i].setSequence(label2Seq.get(daSet[i].getName().toUpperCase()));
				sequencesLoaded = true;
				if (noMatchSeqs.contains(label2Seq.get(daSet[i].getName().toUpperCase())))
					noMatchSeqs.remove(label2Seq.get(daSet[i].getName().toUpperCase()));
			} else {
				noMatchCount++;
				noMatchDAs.add(daSet[i]);
				
			}
				
		
		if (!sequencesLoaded) {
			MessageUtil.showWarning("No match between sequence and protein ids");
			return;
		}
		
		if (noMatchSeqs.size() > 0 && noMatchDAs.size() > 0) 
			new SequenceMatchErrorFrame(this, noMatchDAs, noMatchSeqs).showDialog(AngstdUI.getInstance(), "SequenceMatcher");
		

		if (this.getSequences().length == 0)
			return;
		
		// set the new icon within the workspace (associated icon)
		viewInfo.setUsedIcon(viewInfo.getAssociatedIcon());
		
		// update workspace
		WorkspaceManager.getInstance().forceRepaint();
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

	
	/* *********************************************************** *
	 *   						 INITIALIZING METHODS			   *
	 * *********************************************************** */

	/**
	 * Helper method to initialize all needed controlling instances
	 * for view components. The view specific managers, mouse
	 * controller and renderer are initialized here.
	 */
	protected void initDomainController() {
		// init all view manager
		for (DomainViewManager manager : DomainViewManager.values())
			addViewManager(manager, DomainViewManagerFactory.create(manager)); //, this));
		
		// as well as the layout manager
		domLayoutManager = new DomainLayoutManager(viewInfo.getActionManager());
		domLayoutManager.addPropertyChangeListener(this);
		
		// init the mouse listeners
		for (DomainMouseControllerType mouseControllerType : DomainMouseControllerType.values())
			addMouseController(mouseControllerType, DomainMouseControllerFactory.create(mouseControllerType, this));
		
		registerMouseListeners();
		registerViewAsManagerListener(this);
		registerAdditionalDomainRenderer(this);
	}
	
	/**
	 * @see DomainViewI
	 */
	public void registerAdditionalDomainRenderer(DomainViewI view) {
		view.addRenderer(new SimilarityRenderer(view));
		view.addRenderer(new HighlightArrangementRenderer(view));
		view.addRenderer(new HighlightDomainRenderer(view));
		view.addRenderer(new SequenceSelectionRenderer(view));
		view.addRenderer(new DomainTooltipRenderer(view));
		view.addRenderer(new DomainRulerRenderer(view));
		view.addRenderer(new CollapseNumberRenderer(view));
		view.addRenderer(new SimilarityLabelRenderer(view));
		view.addRenderer(new NoteMarkRenderer(view));
	}
	
	/* ******************************************************************* *
	 *   						HANDLING THE MOUSE CONTROLLER			   *
	 * ******************************************************************* */

	/**
	 * The registering of mouse listeners are done based on 
	 * actual settings within the DomainLayoutManager. For instance
	 * if select sequences mode is enabled just this listener
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
		
		// check if sequence selection is active if so add just this mouse controller
		if (getDomainLayoutManager().isSelectSequences()) {
			addMouseListener(getSequenceSelectionMouseController());
			addMouseMotionListener(getSequenceSelectionMouseController());
			return;
		}
		
		addMouseListener(getArrangementMouseController());
		addMouseMotionListener(getArrangementMouseController());
		addMouseListener(getDomainMouseController());
		addMouseMotionListener(getDomainMouseController());
		addMouseListener(getShiftComponentsMouseController());
		addMouseMotionListener(getShiftComponentsMouseController());
	}
	
	/**
	 * Adds a mouse controller to the view (but the registering is not done)
	 * This method is used to fill the mouseController map during
	 * the views initialization.
	 * 
	 * @param type
	 * 		type of he mouse controller defined in DomainMouseControllerFactory
	 * @param mc
	 * 		the mouse controller to add
	 */
	public void addMouseController(DomainMouseControllerType type, MouseAdapter mc) {
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
	public void removeMouseController(DomainMouseControllerType type) {
		mouse_controller.remove(type);
	}
	
	/**
	 * Helper method for accessing a mouse controller based on its type
	 * and already cast to its correct type by using generic.
	 * 
	 * @param <M>
	 * 		the mouse controller class extending MouseAdapter
	 * @param type
	 * 		the DomainMouseControllerType used to get the controller object
	 * @return
	 * 		the mouse controller based on the specified type already cast
	 */
	@SuppressWarnings("unchecked")
	protected <M extends MouseAdapter> M getMouseController(DomainMouseControllerType type) {
		return (M) mouse_controller.get(type);
	}

	/**
	 * @see DomainViewI
	 */
	public ArrangementMouseController getArrangementMouseController() {
		return getMouseController(DomainMouseControllerType.ARRANGEMENTMC);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainMouseController getDomainMouseController() {
		return getMouseController(DomainMouseControllerType.DOMAINMC);
	}
	
	/**
	 * @see DomainViewI
	 */
	public SequenceSelectionMouseController getSequenceSelectionMouseController() {
		return getMouseController(DomainMouseControllerType.SEQUENCESELECTIONMC);
	}
	
	/**
	 * @see DomainViewI
	 */
	public ShiftComponentsMouseController getShiftComponentsMouseController() {
		return getMouseController(DomainMouseControllerType.COMPONENTSHIFTMC);
	}

	/* ******************************************************************* *
	 *   						PROPERTY CHANGE METHODS					   *
	 * ******************************************************************* */
	
	/**
	 * @see DomainViewI
	 */
	public void registerViewAsManagerListener(PropertyChangeListener view) {
		for (ViewManager manager : view_manager.values()) 
			manager.addPropertyChangeListener(view);
	}
	
	/**
	 * @see DomainViewI
	 */
	public void unregisterViewAsManagerListener(PropertyChangeListener view) {
		for (ViewManager manager : view_manager.values())
			manager.removePropertyChangeListener(view);
	}
	
	/**
	 * This method is triggered when one of the associated view manager
	 * fires a property change. In the case of a structural change
	 * a relayout and a repaint is done. In the case of a visual
	 * change only the repaint is done.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewManager.PROPERTY_STRUCTURAL_CHANGE)) 
			doLayout();
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
	public void addViewManager(DomainViewManager name, ViewManager manager) {
		view_manager.put(name, manager);
	}
	
	/**
	 * Removes a view manager from the view
	 * 
	 * @param name
	 * 		the view managers type used to remove the manager
	 */
	public void removeViewManager(DomainViewManager name) {
		view_manager.remove(name);
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
		viewLayout = (DomainLayout) layout;	
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
		domLayoutManager.toggleZoomMode((AbstractView) this);
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
	 * @see DomainViewI
	 */
	public DomainArrangement[] getDaSet() {
		return daSet;
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainViewRenderer getDomainViewRenderer() {
		return (DomainViewRenderer) viewRenderer;
	}

	/**
	 * @see DomainViewI
	 */
	public DomainLayout getDomainLayout() {
		return viewLayout;
	}

	/**
	 * @see DomainViewI
	 */
	public boolean isSequenceLoaded() {
		return sequencesLoaded;
	}
	
	/**
	 * @see DomainViewI
	 */
	public void setSequencesLoaded (boolean flag) {
		sequencesLoaded = flag;
		
		if (sequencesLoaded && (!viewInfo.getIcon().equals(viewInfo.getAssociatedIcon()))) {
			viewInfo.setUsedIcon(viewInfo.getAssociatedIcon());
			WorkspaceManager.getInstance().forceRepaint();
			return;
		}
		if (!sequencesLoaded && (viewInfo.getIcon().equals(viewInfo.getAssociatedIcon()))) {
			viewInfo.setUsedIcon(viewInfo.getDefaultIcon());
			WorkspaceManager.getInstance().forceRepaint();
			return;
		}
	}
	
	/* ******************************************************************* *
	 *   						 ACCESS MANAGER METHODS					   *
	 * ******************************************************************* */
	
	/**
	 * Helper method for accessing a domain manager based on its type
	 * and already cast to its correct type by using generics.
	 * 
	 * @param <M>
	 * 		the manager class implementing ViewManager
	 * @param type
	 * 		the DomainManagerType used to get the manager object
	 * @return
	 * 		the manager based on the specified type already cast
	 */
	@SuppressWarnings("unchecked")
	protected <M extends ViewManager> M getViewManager(DomainViewManager type) {
		return (M) view_manager.get(type);
	}

	/**
	 * @see DomainViewI
	 */
	public DomainLayoutManager getDomainLayoutManager() {
		return domLayoutManager;
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainColorManager getDomainColorManager() {
		return getViewManager(DomainViewManager.DOMAINCOLORMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainArrangementComponentManager getArrangementComponentManager() {
		return getViewManager(DomainViewManager.DOMAINCOMPONENTMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainComponentManager getDomainComponentManager() {
		return getArrangementComponentManager().getDomainComponentManager();
	} 
	
	/**
	 * @see DomainViewI
	 */
	public SelectionManager<DomainComponent> getDomainSelectionManager() {
		return getViewManager(DomainViewManager.DOMAINSELECTIONMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public FontManager<ArrangementComponent> getDomainArrangementFontManager() {
		return getViewManager(DomainViewManager.DAFONTMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public FontManager<DomainComponent> getDomainFontManager() {
		return getViewManager(DomainViewManager.DOMAINFONTMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainShapeManager getDomainShapeManager() {
		return getViewManager(DomainViewManager.DOMAINSHAPEMANAGER);
	}

	/**
	 * @see DomainViewI
	 */
	public SelectionManager<ArrangementComponent> getArrangementSelectionManager() {
		return getViewManager(DomainViewManager.ARRANGEMENTSELECTIONMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainSearchOrthologsManager getDomainSearchOrthologsManager() {
		return getViewManager(DomainViewManager.DOMAINSEARCHORTHOLOGSMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainShiftManager getDomainShiftManager() {
		return getViewManager(DomainViewManager.DOMAINSHIFTMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public CollapseSameArrangementsManager getCollapseSameArrangementsManager() {
		return getViewManager(DomainViewManager.COLLAPSESAMEARRANGEMENTSMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public DomainSimilarityManager getDomainSimilarityManager() {
		return getViewManager(DomainViewManager.DOMAINSIMILARITYMANAGER);
	}
	
	/**
	 * @see DomainViewI
	 */
	public NoteManager getNoteManager() {
		return getViewManager(DomainViewManager.NOTEMANAGER);
	}

	@Override
	public boolean isCompareDomainsMode() {
       return isCompareDomainsMode;		
	}

	@Override
	public void setCompareDomainsMode(boolean b) {
		isCompareDomainsMode=b;
	}

}
