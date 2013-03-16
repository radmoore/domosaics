package domosaics.ui.views.view.manager;

import java.beans.PropertyChangeListener;

import domosaics.ui.views.domainview.manager.DomainViewManagerFactory;
import domosaics.ui.views.view.components.ViewComponent;




/**
 * Views within DoMosaics display graphical components {@link ViewComponent}
 * for backend data models. The concept to manage the attributes of those
 * view components is manager based. For instance the font used for the
 * rendering of its label can be controlled and set by a {@link FontManager}.
 * <p>
 * The ViewComponents just know their position and boundaries on the 
 * screen and all other attributes like color, font, selection status etc.
 * is managed by ViewManagers.
 * <p>
 * To communicate with the view, the view is added as PropertyChangeListener
 * to a manager and can be informed of visual changes and structural changes
 * which are necessary after an attribute for a component changed.
 * <p>
 * For instance changing a color would normally result in a visual change
 * (meaning repaint) and a collapsing of nodes within a tree would
 * result in a structural change (meaning the layout is recalculated before 
 * the repaint is triggered).
 * <p>
 * This interface just describes the methods to communicate with the 
 * observed view and the basic implementation is {@link DefaultViewManager}.
 * Therefore all manager classes which want to notify the observed
 * view about changes should extend the AbstractViewManager.
 * <p>
 * Its also possible to register more than one view to a manager 
 * so that all views are notified when an observed component changes.
 * This is necessary if views share the same objects.
 * <p>
 * To add and extend ViewManager for a view in an easy and extendible way
 * there should exist a ViewManagerFactory like {@link DomainViewManagerFactory}
 * which creates all manager needed for a specific view.
 * 
 * 
 * 
 * 
 * @author Andreas Held
 *
 */
public interface ViewManager {
	
	/** property indicating a visual change, resulting in a repaint */
	public static final String PROPERTY_VISUAL_CHANGE = "Visual Change";
	
	/** property indicating a structural change, resulting in a relayout and repaint */
	public static final String PROPERTY_STRUCTURAL_CHANGE = "Structural Change";
	
	/**
	 * Method to add a PropertyChangeListener e.g. the observed view to the
	 * manager.
	 * 
	 * @param listener
	 * 		the view to be observed
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);
     
	/**
	 * Method to remove a PropertyChangeListener e.g. the observed view 
	 * from the manager.
	 * 
	 * @param listener
	 * 		the view which shouldn't be observed anymore
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Fires a property change event to the registered listeners.
	 * 
	 * @param propertyName
	 * 		the property name e.g. PROPERTY_VISUAL_CHANGE
	 * @param oldValue
	 * 		the old value of a view component
	 * @param newValue
	 * 		the new value of a view component
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue);

	/**
	 * Shortcut method to notify the view that a structural change happened
	 * and therefore a relayout has to take place.
	 */
	public void structuralChange();

	/**
	 * Shortcut method to notify the view that a visual change happened and
	 * therefore a repaint have to be triggered.
	 */
	public void visualChange();
}
