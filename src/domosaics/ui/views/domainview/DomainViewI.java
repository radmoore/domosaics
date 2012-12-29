package domosaics.ui.views.domainview;

import java.beans.PropertyChangeListener;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.sequence.SequenceI;
import domosaics.ui.views.domaintreeview.DomainTreeView;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.layout.DomainLayout;
import domosaics.ui.views.domainview.manager.CollapseSameArrangementsManager;
import domosaics.ui.views.domainview.manager.DomainArrangementComponentManager;
import domosaics.ui.views.domainview.manager.DomainColorManager;
import domosaics.ui.views.domainview.manager.DomainLayoutManager;
import domosaics.ui.views.domainview.manager.DomainSearchOrthologsManager;
import domosaics.ui.views.domainview.manager.DomainShapeManager;
import domosaics.ui.views.domainview.manager.DomainShiftManager;
import domosaics.ui.views.domainview.manager.DomainSimilarityManager;
import domosaics.ui.views.domainview.manager.NoteManager;
import domosaics.ui.views.domainview.manager.DomainArrangementComponentManager.DomainComponentManager;
import domosaics.ui.views.domainview.mousecontroller.ArrangementMouseController;
import domosaics.ui.views.domainview.mousecontroller.DomainMouseController;
import domosaics.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;
import domosaics.ui.views.domainview.mousecontroller.ShiftComponentsMouseController;
import domosaics.ui.views.domainview.renderer.DomainViewRenderer;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.manager.FontManager;
import domosaics.ui.views.view.manager.SelectionManager;




/**
 * DomainViewI describes all methods the DomainView must support to
 * fit in the applications work flow. The basic implementation of this 
 * interface is {@link DomainView} but also a {@link DomainTreeView}
 * implements all necessary functionalities of this interface.
 * <p>
 * When creating a new view just its basic components are initialized by 
 * the view factory. To make the view reasonable a backend dataset 
 * should be assigned after its creation, e.g. using the setDaSet method 
 * in the case of a domain view.
 * <p>
 * For detailed information and opportunities to extend the view look 
 * into {@link DomainView}.
 * 
 * @author Andreas Held
 *
 */
public interface DomainViewI extends View {
	
	/* *********************************************************** *
	 *   					MANAGING BACKEND DATA				   *
	 * *********************************************************** */
	
	/**
	 * Sets the backend data set for this view. This method should trigger
	 * the initialization of the view components interacting with the
	 * graphical components for this backend data, such as the
	 * view manager, the layout, renderer, mouse controller etc.
	 * 
	 * @param daSet
	 * 		the backend dataset
	 */
	public void setDaSet(DomainArrangement[] daSet);
	
	/**
	 * Return the backend data which were assigned to this view.
	 * 
	 * @return
	 * 		backend data set displayed by this view
	 */
	public DomainArrangement[] getDaSet();
	
	
	/**
	 * Add a set of arrangements to this views
	 *  
	 */
	public void addDaSet(DomainArrangement[] daSet);
	
	
	/**
	 * Removes a domain arrangement from the dataset
	 * 
	 * @param da
	 * 		the arrangement to be removed
	 */
	public void removeArrangement(DomainArrangement da);
	
	/**
	 * Adds a domain arrangement to the dataset
	 * 
	 * @param da
	 * 		the arrangement to be added
	 */
	public void addArrangement(DomainArrangement da);
	
	/**
	 * Associates sequences with domain arrangements using the id.
	 * 
	 * @param seqs
	 * 		the sequences to be associated with the view
	 * @param daSet
	 * 		the data set in which the sequences are loaded
	 */
	public void loadSequencesIntoDas(SequenceI[] seqs, DomainArrangement[] daSet);
	
	/**
	 * Returns all sequence objects which were associated to
	 * a domain arrangement.
	 * 
	 * @return
	 * 		sequences associated to domain arrangements
	 */
	public SequenceI[] getSequences();
	
	/**
	 * Return whether or not sequences were associated with the 
	 * domain arrangements.
	 * 
	 * @return
	 * 		whether or not sequences were associated to domain arrangements
	 */
	public boolean isSequenceLoaded();
	
	/**
	 * Sets the sequences loaded flag
	 * 
	 * @param flag
	 * 		whether or not sequences are associated with the view
	 */
	public void setSequencesLoaded (boolean flag);

	
	/* *********************************************************** *
	 *   							INIT 						   *
	 * *********************************************************** */
	
	/**
	 * Registers the active mouse listeners depending on the 
	 * applications state
	 */
	public void registerMouseListeners();

	/**
	 * Registers the additional renderer for a specified domain view,
	 * such as DomainToolTipRenderer. 
	 * 
	 * @param view
	 * 		the view to which the additional renderer should be registered
	 */
	public void registerAdditionalDomainRenderer(DomainViewI view);
	
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
	
	
	/* *********************************************************** *
	 *   							GET 						   *
	 * *********************************************************** */
	
	/**
	 * Returns the actually used layout to layout the components.
	 * 
	 * @return
	 * 		actually used layout
	 */
	public DomainLayout getDomainLayout();

	/**
	 * Return the actually used view renderer.
	 * 
	 * @return
	 * 		actually used view renderer.
	 */
	public DomainViewRenderer getDomainViewRenderer();

	
	/* *********************************************************** *
	 *   					ACCESS MOUSE LISTENER				   *
	 * *********************************************************** */
	
	/**
	 * Return the mouse controller used to highlight and select domain
	 * arrangements.
	 * 
	 * @return
	 * 		mouse controller for domain arrangements
	 */
	public ArrangementMouseController getArrangementMouseController();
	
	/**
	 * Return the mouse controller used to highlight and select domains
	 * 
	 * @return
	 * 		mouse controller for domains 
	 */
	public DomainMouseController getDomainMouseController();
	
	/**
	 * Return the mouse controller used to select underlying sequences 
	 * of domain arrangements.
	 * 
	 * @return
	 * 		mouse controller used to select underlying sequences 
	 */
	public SequenceSelectionMouseController getSequenceSelectionMouseController();
	
	/**
	 * Return the mouse controller used to shift arrangements.
	 * 
	 * @return
	 * 		mouse controller for arrangement shifting
	 */
	public ShiftComponentsMouseController getShiftComponentsMouseController();
	
	/* *********************************************************** *
	 *   					ACCESS VIEW MANAGER 				   *
	 * *********************************************************** */
	
	/**
	 * Return the layout manager for the view
	 * 
	 * @return
	 * 		layout manager
	 */
	public DomainLayoutManager getDomainLayoutManager();
	
	/**
	 * Return the domain arrangement component manager.
	 * This manager gives also access to the domain components.
	 * 
	 * @return
	 * 		domain arrangement component manager (with access to the domain components)
	 */
	public DomainArrangementComponentManager getArrangementComponentManager();
	
	/**
	 * Returns the component manager used to manage the domain mappings
	 * 
	 * @return
	 * 		the component manager used to manage the domain mappings	
	 */
	public DomainComponentManager getDomainComponentManager();
	
	
	
	/**
	 * Return the font manager for domain arrangement components 
	 * 
	 * @return
	 * 		font manager for domain arrangement components
	 */
	public FontManager<ArrangementComponent> getDomainArrangementFontManager();
	
	/**
	 * Return the font manager for domain components 
	 * 
	 * @return
	 * 		font manager for domain components 
	 */
	public FontManager<DomainComponent> getDomainFontManager();
	
	/**
	 * Return the color manager for domain components
	 * 
	 * @return
	 * 		color manager for domain components
	 */
	public DomainColorManager getDomainColorManager();
	
	/**
	 * Return the selection manager for domain components
	 * 
	 * @return
	 * 		selection manager for domain components
	 */
	public SelectionManager<DomainComponent> getDomainSelectionManager();
	
	/**
	 * Return the selection manager for domain arrangement components 
	 * 
	 * @return
	 * 		selection manager for domain arrangement components 
	 */
	public SelectionManager<ArrangementComponent> getArrangementSelectionManager();
	
	/**
	 * Return the shape manager for domain components
	 * 
	 * @return
	 * 		shape manager for domain components
	 */
	public DomainShapeManager getDomainShapeManager();
	
	/**
	 * Return the layout manager for the view
	 * 
	 * @return
	 * 		layout manager
	 */
	public DomainSearchOrthologsManager getDomainSearchOrthologsManager();
	
	/**
	 * Return the manager used to shift arrangements and domains on the screen
	 * 
	 * @return
	 * 		shift manager for arrangements and domains
	 */
	public DomainShiftManager getDomainShiftManager();
	
	/**
	 * Return the manager used to collapse same arrangements 
	 * 
	 * @return
	 * 		manager used to collapse same arrangements 
	 */
	public CollapseSameArrangementsManager getCollapseSameArrangementsManager();

	/**
	 * Return the manager used to colorize similar arrangements
	 * 
	 * @return
	 * 		manager to colorize similar arrangements
	 */
	public DomainSimilarityManager getDomainSimilarityManager();
	
	/**
	 * Return the manager used to associate arrangements with notes
	 * 
	 * @return
	 * 		manager to associate arrangements with notes
	 */
	public NoteManager getNoteManager();
	
	/**
	 * Return the flag whether the "comparison of domains
	 * at the sequence level" is active
	 * 
	 */
	public boolean isCompareDomainsMode();
	
	/**
	 * Change the flag whether the "comparison of domains
	 * at the sequence level" is active
	 * 
	 */
	public void setCompareDomainsMode(boolean b);
}
