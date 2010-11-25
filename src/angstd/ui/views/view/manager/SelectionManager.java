package angstd.ui.views.view.manager;

import java.util.Collection;
import java.util.Iterator;

import angstd.ui.views.view.components.ViewComponent;

/**
 * The SelectionManager is a ViewManager which handles the selection
 * of ViewComponents. In most cases it would be triggered by mouseHandlers
 * to respond to user interaction. <br>
 * Of course its also possible to use this manager for manually selection 
 * when working in actions.
 * <p>
 * This interface describes methods for handling mouse over selections
 * as well as selections which can be triggered by mouse clicks.
 * Again the specifics should be implemented by MouseListeners or
 * actions.
 * <p>
 * The basic implementation of this interface is {@link DefaultSelectionManager}
 * To extend or overwrite some of the basic functionalities the 
 * DefaultSelectionManager should be extended.
 * <p>
 * The extending of the ViewManager interface ensures the communication
 * with the observed view.
 * 
 * @author Andreas Held
 *
 * @param <C>
 * 		the generic type of the ViewComponent
 */
public interface SelectionManager<C extends ViewComponent> extends ViewManager {

	/**
	 * Sets the ViewComponent which was selected by clicking on it
	 * 
	 * @param clickedComp
	 * 		the view component on which were clicked
	 */
	public void setClickedComp(C clickedComp);
	
	/**
	 * Return the ViewComponent which was selected by clicking on it
	 * 
	 * @return
	 * 		the view component on which were clicked
	 */
	public C getClickedComp();
	
	/**
	 * Sets the ViewComponent which was selected by a mouse over event
	 * 
	 * @param mouseOverComp
	 * 		the component on which the mouse is currently over
	 */
	public void setMouseOverComp(C mouseOverComp);
	
	/**
	 * Return the ViewComponent which was selected by a mouse over event
	 * 
	 * @return
	 * 		the component on which the mouse is currently over
	 */
	public C getMouseOverComp();

	/**
	 * Method to manually add view components to the selection.
	 * 
	 * @param selection
	 * 		the ViewComponents which are about to be added to the selection
	 */
	public void setSelection(Collection<C> selection);
	
	/**
	 * Removes all components from the selection
	 */
	public void clearSelection();
	
	/**
	 * Check whether or not the specified component 
	 * is currently selected.
	 * 
	 * @param comp
	 * 		the component which should be checked against the selection
	 * @return
	 * 		whether or not the specified component is selected
	 */
	public boolean isCompSelected(C comp);
	
	/**
	 * Return an iterator over all ViewComponents which are 
	 * currently selected
	 * 
	 * @return
	 * 		an iterator over all ViewComponents which are currently selected
	 */
	public Iterator<C> getSelectionIterator();
	
	/**
	 * Return all selected ViewComponents as collection
	 * 
	 * @return
	 * 		all selected ViewComponents
	 */
	public Collection<C> getSelection();
	
	/**
	 * Adds a component to the selection
	 * 
	 * @param comp
	 * 		the component to be added to the selection
	 */
	public void addToSelection(C comp);
	
	/**
	 * Removes a component from the selection
	 * 
	 * @param comp
	 * 		the component to be removed from the selection
	 */
	public void removeFromSelection(C comp);
	
}
