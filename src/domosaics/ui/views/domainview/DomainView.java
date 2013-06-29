 package domosaics.ui.views.domainview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdom2.Attribute;
import org.jdom2.Element;

import domosaics.model.GO.GeneOntology;
import domosaics.model.GO.GeneOntologyTerm;
import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.DomainType;
import domosaics.model.arrangement.io.GatheringThresholdsReader;
import domosaics.model.sequence.Sequence;
import domosaics.model.sequence.SequenceI;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.components.SequenceMatchErrorFrame;
import domosaics.ui.views.domainview.io.DomainViewExporter;
import domosaics.ui.views.domainview.layout.DomainLayout;
import domosaics.ui.views.domainview.layout.MSALayout;
import domosaics.ui.views.domainview.layout.ProportionalLayout;
import domosaics.ui.views.domainview.layout.UnproportionalLayout;
import domosaics.ui.views.domainview.manager.CollapseSameArrangementsManager;
import domosaics.ui.views.domainview.manager.DomainArrangementComponentManager;
import domosaics.ui.views.domainview.manager.DomainColorManager;
import domosaics.ui.views.domainview.manager.DomainLayoutManager;
import domosaics.ui.views.domainview.manager.DomainSearchOrthologsManager;
import domosaics.ui.views.domainview.manager.DomainShapeManager;
import domosaics.ui.views.domainview.manager.DomainShiftManager;
import domosaics.ui.views.domainview.manager.DomainSimilarityManager;
import domosaics.ui.views.domainview.manager.DomainViewManagerFactory;
import domosaics.ui.views.domainview.manager.NoteManager;
import domosaics.ui.views.domainview.manager.DomainArrangementComponentManager.DomainComponentManager;
import domosaics.ui.views.domainview.manager.DomainViewManagerFactory.DomainViewManager;
import domosaics.ui.views.domainview.mousecontroller.ArrangementMouseController;
import domosaics.ui.views.domainview.mousecontroller.DomainMouseController;
import domosaics.ui.views.domainview.mousecontroller.DomainMouseControllerFactory;
import domosaics.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;
import domosaics.ui.views.domainview.mousecontroller.ShiftComponentsMouseController;
import domosaics.ui.views.domainview.mousecontroller.DomainMouseControllerFactory.DomainMouseControllerType;
import domosaics.ui.views.domainview.renderer.DefaultDomainViewRenderer;
import domosaics.ui.views.domainview.renderer.DomainViewRenderer;
import domosaics.ui.views.domainview.renderer.additional.CollapseNumberRenderer;
import domosaics.ui.views.domainview.renderer.additional.DomainRulerRenderer;
import domosaics.ui.views.domainview.renderer.additional.DomainTooltipRenderer;
import domosaics.ui.views.domainview.renderer.additional.HighlightArrangementRenderer;
import domosaics.ui.views.domainview.renderer.additional.HighlightDomainRenderer;
import domosaics.ui.views.domainview.renderer.additional.NoteMarkRenderer;
import domosaics.ui.views.domainview.renderer.additional.SequenceSelectionRenderer;
import domosaics.ui.views.domainview.renderer.additional.SimilarityLabelRenderer;
import domosaics.ui.views.domainview.renderer.additional.SimilarityRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.MsaArrangementRenderer;
import domosaics.ui.views.view.AbstractView;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;
import domosaics.ui.views.view.manager.FontManager;
import domosaics.ui.views.view.manager.SelectionManager;
import domosaics.ui.views.view.manager.ViewManager;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * The DomainView handles the visualization and manipulation of
 * a backend dataset consisting of domain arrangements. The view
 * should be created via the ViewManager which wraps around the
 * DoMosaicsViewFactory. The main initialization takes place when
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
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
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
		quickSort(0, daSet.length-1);
		setViewLayout(new ProportionalLayout());
		viewRenderer = new DefaultDomainViewRenderer(this);

		doLayout();
		repaint();
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
	public void loadSequencesIntoDas(SequenceI[] seqs, DomainArrangement[] daSet, boolean checkBeforeAssociation) {
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
				sequencesLoaded = true;
				if(checkBeforeAssociation)
					if(daSet[i].getDomain(daSet[i].countDoms()-1).getTo() < label2Seq.get(daSet[i].getName().toUpperCase()).getLen(false))
						if(!daSet[i].hasSeq() && !label2Seq.get(daSet[i].getName().toUpperCase()).equals(daSet[i].getSequence()))
							if(!daSet[i].hasSeqBeenModifiedManually())
								daSet[i].setSequence(label2Seq.get(daSet[i].getName().toUpperCase()));
							else
								MessageUtil.showInformation(DoMosaicsUI.getInstance(),daSet[i].getName().toUpperCase()+" sequence has been manually modified.\nDoMosaics prevents automatical load of the sequence for consistency reasons.\nThis must be done manually with caution regarding the existing domain annotation. ");
						else
							MessageUtil.showInformation(DoMosaicsUI.getInstance(),daSet[i].getName().toUpperCase()+" already have a different recorded sequence.\nDoMosaics prevents automatical load of the sequence for consistency reasons.\nThis must be done manually with caution regarding the existing domain annotation. ");
					else
						MessageUtil.showInformation(DoMosaicsUI.getInstance(),daSet[i].getName().toUpperCase()+" has domain annotation exceeding the sequence length.\nDoMosaics prevents automatical load of the sequence for consistency reasons.\nThis must be done manually with caution regarding the existing domain annotation. ");
				
				else
					daSet[i].setSequence(label2Seq.get(daSet[i].getName().toUpperCase()));
				if (noMatchSeqs.contains(label2Seq.get(daSet[i].getName().toUpperCase())))
					noMatchSeqs.remove(label2Seq.get(daSet[i].getName().toUpperCase()));
			} else {
				noMatchCount++;
				noMatchDAs.add(daSet[i]);
				
			}
				
		
		if (!sequencesLoaded) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No match between sequence and protein ids");
			return;
		}
		
		if (noMatchSeqs.size() > 0 && noMatchDAs.size() > 0) 
			new SequenceMatchErrorFrame(this, noMatchDAs, noMatchSeqs).showDialog(DoMosaicsUI.getInstance(), "SequenceMatcher");
		

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
	
	/**
	 */
	public ArrayList<String> getLabels() {
		ArrayList<String> lab = new ArrayList<String>();
		for (int i = 0; i < daSet.length; i++)
			lab.add(daSet[i].getName());
		return lab;
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
	
	public void xmlWrite(Element viewType) {
		//PROTEINS
		DomainArrangement[] arrangements = this.getDaSet();
		HashSet<DomainFamily> domFamilies = new HashSet<DomainFamily >();
		for (int i = 0; i < arrangements.length; i++) {
			Element prot = new Element("PROTEIN");
			viewType.addContent(prot);
			Attribute protId = new Attribute("id",arrangements[i].getName());
			prot.setAttribute(protId);

			// AA sequence
			if (arrangements[i].hasSeq()) {
				Element seq = new Element("SEQUENCE");
				prot.addContent(seq);
				seq.setText(arrangements[i].getSequence().getSeq(true));
			}
			
			// Manual modification flag
			if (arrangements[i].hasSeqBeenModifiedManually()) {
				Element manualModif = new Element("MANUALMODIF");
				prot.addContent(manualModif);
			}

			// Comment
			if(arrangements[i].getDesc()!=null && !arrangements[i].getDesc().equals("")) {
				Element com = new Element("COMMENT");
				prot.addContent(com);
				com.setText(arrangements[i].getDesc());
			}

			// DOMAINS (normal and hidden)
			HashSet<String> familyList=new HashSet<String>();
			HashMap<String, ArrayList<Domain> > doms = new HashMap<String, ArrayList<Domain> >();
			Iterator<Domain> iterDom = arrangements[i].getDomainIter();
			while(iterDom.hasNext()) {
				Domain currentDomain = iterDom.next();
				if(!doms.containsKey(currentDomain.getID())) {
					familyList.add(currentDomain.getID());
					domFamilies.add(currentDomain.getFamily());
					doms.put(currentDomain.getID(), new ArrayList<Domain>());
				}
				doms.get(currentDomain.getID()).add(currentDomain);
			}
			HashMap<String, ArrayList<Domain> > hiddenDoms = new HashMap<String, ArrayList<Domain> >();
			Iterator<Domain> iterHidden = arrangements[i].getHiddenDoms().iterator();
			while(iterHidden.hasNext()) {
				Domain currentDomain = iterHidden.next();
				if(!hiddenDoms.containsKey(currentDomain.getID())) {
					familyList.add(currentDomain.getID());
					domFamilies.add(currentDomain.getFamily());
					hiddenDoms.put(currentDomain.getID(), new ArrayList<Domain>());
				}
				hiddenDoms.get(currentDomain.getID()).add(currentDomain);
			}
			Iterator<String> famIter = familyList.iterator();
			while(famIter.hasNext()) {
				// Family
				Element dom = new Element("DOMAIN");
				prot.addContent(dom);
				String domFam = famIter.next();
				Attribute id = new Attribute("id",""+domFam);
				dom.setAttribute(id);
				if(doms.containsKey(domFam)) {
					Iterator<Domain> domains = doms.get(domFam).iterator();
					// Not hidden occurences
					while(domains.hasNext()) {
						Domain currentDomain = domains.next();
						if(currentDomain.getID().equals(currentDomain.getID())) {
							Element occ = new Element("OCCURRENCE");
							dom.addContent(occ);
							Attribute from = new Attribute("from",""+currentDomain.getFrom());
							occ.setAttribute(from);
							Attribute to = new Attribute("to",""+currentDomain.getTo());
							occ.setAttribute(to);
							// TODO A domain have to have an E-value or not?
							if(currentDomain.getEvalue() != Double.POSITIVE_INFINITY)
								occ.setAttribute(new Attribute("evalue",""+currentDomain.getEvalue()));
							if(currentDomain.getScore() != Double.NEGATIVE_INFINITY)
								occ.setAttribute(new Attribute("score",""+currentDomain.getScore()));
							if(currentDomain.isPutative())
								occ.setAttribute(new Attribute("isPutative","true"));
						}
					}
				}
				if(hiddenDoms.containsKey(domFam)) {
					Iterator<Domain> hidden = hiddenDoms.get(domFam).iterator();
					while(hidden.hasNext()) {
						Domain currentDomain = hidden.next();
						if(currentDomain.getID().equals(currentDomain.getID())) {
							Element occ = new Element("OCCURRENCE");
							dom.addContent(occ);
							Attribute hiddenState = new Attribute("hasBeenHidden", "true");
							occ.setAttribute(hiddenState);
							Attribute from = new Attribute("from",""+currentDomain.getFrom());
							occ.setAttribute(from);
							Attribute to = new Attribute("to",""+currentDomain.getTo());
							occ.setAttribute(to);
							// TODO A domain have to have an E-value or not?
							if(currentDomain.getEvalue() != Double.POSITIVE_INFINITY)
								occ.setAttribute(new Attribute("evalue",""+currentDomain.getEvalue()));
							if(currentDomain.getScore() != Double.NEGATIVE_INFINITY)
								occ.setAttribute(new Attribute("score",""+currentDomain.getScore()));
							if(currentDomain.isPutative())
								occ.setAttribute(new Attribute("isPutative","true"));
						}
					}
				}
			}

		}

		// TODO DOMAIN FAMILIES
		Element allTypes = new Element("ALL_DOMAIN_TYPES");
		viewType.addContent(allTypes);
		Iterator<DomainFamily> famIter = domFamilies.iterator();
		HashSet<GeneOntologyTerm> allGO = new HashSet<GeneOntologyTerm>();
		while (famIter.hasNext()) {
			DomainFamily fam = famIter.next();
			Element domFam = new Element("DOMAIN_FAMILY");
			allTypes.addContent(domFam);
			Attribute id = new Attribute("id",""+fam.getId());
			domFam.setAttribute(id);
			Attribute famName = new Attribute("name",""+fam.getName());
			domFam.setAttribute(famName);
			Attribute db = new Attribute("source",DomainType.getType(fam.getId()).getName());
			domFam.setAttribute(db);
			if(fam.getInterproEntry()!=null)
				domFam.setAttribute(new Attribute("interpro",""+fam.getInterproEntry()));
			Attribute color = new Attribute("color", ""+this.getDomainColorManager().getDomainColor(fam).getRGB());
			domFam.setAttribute(color);
			Attribute shape = new Attribute("shape", ""+this.getDomainShapeManager().getShapeID(fam));
			domFam.setAttribute(shape);
			if(fam.getGathThreshByFam()!=Double.POSITIVE_INFINITY) {
				Attribute famThresh = new Attribute("famThresh", ""+fam.getGathThreshByFam());
				domFam.setAttribute(famThresh);	
			}
			if(fam.getGathThreshByDom()!=Double.POSITIVE_INFINITY) {		
				Attribute occThresh = new Attribute("occThresh", ""+fam.getGathThreshByDom());
				domFam.setAttribute(occThresh);			
			}
			if(fam.hasGoAnnotation()) {
				Iterator<GeneOntologyTerm> iterGO = fam.getGoTerms();
				while(iterGO.hasNext()) {
					GeneOntologyTerm term = iterGO.next();
					allGO.add(term);
					Element goFam = new Element("GO_ANNOT");
					domFam.addContent(goFam);
					Attribute goID = new Attribute("id", ""+term.getID());
					goFam.setAttribute(goID);
				}
			}
		}

		// GO terms
		if(allGO.size()!=0) {
			Element go = new Element("GENE_ONTOLOGY");
			viewType.addContent(go);
			Iterator<GeneOntologyTerm> iterTerms = allGO.iterator();
			while(iterTerms.hasNext()) {
				GeneOntologyTerm term = iterTerms.next();
				Element goTerm = new Element("GO_TERM");
				go.addContent(goTerm);
				Attribute goID = new Attribute("id", ""+term.getID());
				goTerm.setAttribute(goID);
				Attribute goName = new Attribute("name", ""+term.getName());
				goTerm.setAttribute(goName);
				Attribute onto = new Attribute("ontology", ""+term.getParentOntology());
				goTerm.setAttribute(onto);
			}
		}        

		// LAYOUT SETTINGS
		// TODO test is any setting different from default init
		Element layout = new Element("LAYOUT_SETTINGS");
		viewType.addContent(layout);
		// View
		DomainLayoutManager layoutManager = this.getDomainLayoutManager();
		// TODO test is required or if is already set as default in import
		Attribute layoutView = new Attribute("view", "PROPORTIONAL");
		if (layoutManager.isUnproportionalView())
			layoutView.setValue("UNPROPORTIONAL");
		else if (layoutManager.isMsaView()) 
			layoutView.setValue("MSA");
		layout.setAttribute(layoutView);
		// Others
		if(layoutManager.isFitDomainsToScreen())
			layout.setAttribute(new Attribute("isFitToScreen","true"));
		if(layoutManager.isEvalueColorization())
			layout.setAttribute(new Attribute("evalueColorization","true"));
		if(layoutManager.isShowShapes())
			layout.setAttribute(new Attribute("showShapes","true"));
	}

	@Override
	public void xmlWriteViewType() {
		Attribute type = new Attribute("type","ARRANGEMENTS");
		viewType.setAttribute(type);	
	}

	@Override
	public void xmlRead(Element viewType) {		
		int cptSeq=0;
		this.setName(viewType.getAttributeValue("name"));
		initDomainController();
		
		// TODO Read GO terms in details in new compared to our data notably		
		
		// Read domain families
		List<Element> families = viewType.getChild("ALL_DOMAIN_TYPES").getChildren("DOMAIN_FAMILY");
		Iterator<Element> f = families.iterator();
		while(f.hasNext()) {
			Element family = f.next();
			// TODO process name inverted with id in xml?
			DomainFamily domFamily = GatheringThresholdsReader.getInstance().get(family.getAttributeValue("id"));
			if(domFamily == null) {	
				domFamily = new DomainFamily(family.getAttributeValue("id"), family.getAttributeValue("name"), DomainType.getType(family.getAttributeValue("id")));
				GatheringThresholdsReader.add(domFamily);
			} else {
				if(!DomainType.getType(family.getAttributeValue("id")).getName().equals(family.getAttributeValue("source")) || !domFamily.getDomainType().getName().equals(family.getAttributeValue("source")) || !domFamily.getName().equals(family.getAttributeValue("name")))
					MessageUtil.showDialog(DoMosaicsUI.getInstance(),"Error: import of a domain family inconsistent with DoMosaics data");
			}
			String interproEntry=family.getAttributeValue("interpro");
			if(interproEntry!=null)
				domFamily.setInterproEntry(interproEntry);
			String domColor=family.getAttributeValue("color");
			if(domColor!=null)
				this.getDomainColorManager().setDomainColor(domFamily, new Color(new Integer(domColor)));
			String domShape=family.getAttributeValue("shape");
			if(domShape!=null)
				this.getDomainShapeManager().setDomainShape(domFamily, new Integer(domShape));
			String famThresh=family.getAttributeValue("famThresh");
			if(famThresh!=null)
				domFamily.setGathThreshByFam(new Double(famThresh));
			String domThresh=family.getAttributeValue("domThresh");
			if(domThresh!=null)
				domFamily.setGathThreshByDom(new Double(domThresh));
			List<Element> GOs = family.getChildren("GO_ANNOT");
			Iterator<Element> go = GOs.iterator();
			if(go.hasNext()) {
				Element term = go.next();
				// TODO authorized inconsistent GO dur to different versions of .obo
				GeneOntology geneOnto = GeneOntology.getInstance();
				GeneOntologyTerm goTerm = geneOnto.getTerm(term.getAttributeValue("id"));
				if(goTerm != null)
					domFamily.addGoTerm(goTerm);
				else
					MessageUtil.showDialog(DoMosaicsUI.getInstance(),"Error: import of a go term inconsistent with DoMosaics data");
			}
		}
		
		// Read domain arrangments
		List<Element> prots = viewType.getChildren("PROTEIN");
		List<DomainArrangement> list = new ArrayList<DomainArrangement>(prots.size());
		// Iterate over proteins
		Iterator<Element> p = prots.iterator();
		while(p.hasNext()) {
			Element protein = p.next();
			DomainArrangement da = new DomainArrangement();
			da.setName(protein.getAttributeValue("id"));
			// Iterate over domains
			List<Element> doms = protein.getChildren("DOMAIN");
			Iterator<Element> d = doms.iterator();
			while(d.hasNext()) {
				Element domainFamily = d.next();
				DomainFamily domFam = GatheringThresholdsReader.getInstance().get(domainFamily.getAttributeValue("id"));
				// Iterate over occurrences
				List<Element> occurrences = domainFamily.getChildren("OCCURRENCE");
				Iterator<Element> o = occurrences.iterator();
				while(o.hasNext()) {
					Element occ= o.next();
					Domain dom = new Domain(new Integer(occ.getAttributeValue("from")),new Integer(occ.getAttributeValue("to")), domFam);
					String evalue = occ.getAttributeValue("evalue");
					if(evalue != null)
						dom.setEvalue(new Double(evalue));
					String score = occ.getAttributeValue("score");
					if(score != null)
						dom.setScore(new Double(score));
					String putativeState = occ.getAttributeValue("isPutative");
					if(putativeState != null)
						dom.setPutative(true);
					String hiddenState = occ.getAttributeValue("hasBeenHidden");
					if(hiddenState != null)
						da.addHiddenDomain(dom);						
					else
						da.addDomain(dom);
				}
			}
			Element note = protein.getChild("COMMENT");
			if(note != null)
				da.setDesc(note.getText());
			Element seq = protein.getChild("SEQUENCE");
			if(seq != null) {
				cptSeq++;
				da.setSequence(new Sequence(protein.getAttributeValue("id"),seq.getText()));
			}
			Element manualModif = protein.getChild("MANUALMODIF");
			if(manualModif != null)
				da.seqModifiedManually();
			list.add(da);
		}
		this.daSet = list.toArray(new DomainArrangement[list.size()]);
		if(cptSeq==list.size())
			setSequencesLoaded(true);
		
		viewRenderer = new DefaultDomainViewRenderer(this);
		// Read Layout settings
		Element layoutSettings = viewType.getChildren("LAYOUT_SETTINGS").get(0);
		String layoutView = layoutSettings.getAttributeValue("view");
		if (layoutView.equals("PROPORTIONAL")) {
			setViewLayout(new ProportionalLayout());
			domLayoutManager.setToProportionalView();
			getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
		} else {
			if(layoutView.equals("UNPROPORTIONAL")) {
				setViewLayout(new UnproportionalLayout());
				domLayoutManager.setToUnproportionalView();
				getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			} else {
			 	if(layoutView.equals("MSA")) {
			 		setViewLayout(new MSALayout());
			 		getDomainViewRenderer().setArrangementRenderer(new MsaArrangementRenderer());		
					domLayoutManager.setToMsaView();
			 	}
			 }
		}
		String fitToScreen = layoutSettings.getAttributeValue("isFitToScreen");
		if(fitToScreen!=null)
			domLayoutManager.setFitDomainsToScreen(true);
		String evalueColor = layoutSettings.getAttributeValue("evalueColorization");
		if(evalueColor!=null)
			domLayoutManager.setEvalueColorization(true);
		String showShapes = layoutSettings.getAttributeValue("showShapes");
		if(showShapes!=null)
			domLayoutManager.setShowShapes(true);
		
		registerMouseListeners();
		doLayout();
		repaint(); 
		
	}
	
	/**
	 * sub-function of the QuickSort to order
	 * alphabetically the proteins in views 
	 */
	int partition(int left, int right) {
		int i = left, j = right;
		DomainArrangement tmp;
		DomainArrangement pivot = daSet[(left + right) / 2];
		while (i <= j) {
			while (daSet[i].getName().compareTo(pivot.getName()) < 0)
				i++;
			while (daSet[j].getName().compareTo(pivot.getName()) > 0)
				j--;
			if (i <= j) {
				tmp = daSet[i];
				daSet[i] = daSet[j];
				daSet[j] = tmp;
				i++;
				j--;
			}
		};
		return i;
	}

	/**
	 * QuickSort function that orders alphabetically 
	 * the protein IDs in domain views
	 */
	void quickSort(int left, int right) {
	      int index = partition(left, right);
	      if (left < index - 1)
	            quickSort(left, index - 1);
	      if (index < right)
	            quickSort(index, right);
	}
	
}
