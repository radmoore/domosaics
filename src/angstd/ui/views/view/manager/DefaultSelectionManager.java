package angstd.ui.views.view.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import angstd.ui.views.view.components.ViewComponent;

/**
 * Basic implementation of the SelectionManager interface. This
 * class provides the functionalities for a selection manager for
 * ViewComponents. 
 * <p>
 * MouseOver selection, Clicked component selection and a general
 * selection pool are provided and can be manipulated.
 * <p>
 * For further details see {@link SelectionManager}.
 * 
 * @author Andreas Held
 *
 * @param <C>
 * 		@see SelectionManager
 */
public class DefaultSelectionManager<C extends ViewComponent> 
				extends DefaultViewManager 
				implements SelectionManager<C> 
{
	/** the component selected via mouse over */
	protected C mouseOverComp;
	
	/** the component selected via a click event */
	protected C clickedComp;
	
	/** the component selected via manually selection */
	protected Collection<C> selection;
	
	
	/**
	 * Basic constructor for DefaultSelectionManager initializing the
	 * class variables.
	 */
	public DefaultSelectionManager() {
		mouseOverComp = null;
		clickedComp = null;
		selection = new ArrayList<C>();
	}
	
	/**
	 * @see SelectionManager
	 */
	public C getMouseOverComp() {
		return mouseOverComp;
	}
	
	/**
	 * @see SelectionManager
	 */
	public C getClickedComp() {
		return clickedComp;
	}
	
	/**
	 * @see SelectionManager
	 */
	public void clearSelection() {
		selection.clear();
		mouseOverComp = null;
		clickedComp = null;
	}

	/**
	 * @see SelectionManager
	 */
	public boolean isCompSelected(C comp) {
		return selection.contains(comp);
	}

	/**
	 * @see SelectionManager
	 */
	public Iterator<C> getSelectionIterator() {
		return selection.iterator();
	}
	
	/**
	 * @see SelectionManager
	 */
	public Collection<C> getSelection() {
		return selection;
	}
	
	/**
	 * @see SelectionManager
	 */
	public void setClickedComp(C clickedComp) {
		this.clickedComp = clickedComp;
		visualChange();
	}	
	
	/**
	 * @see SelectionManager
	 */
	public void setMouseOverComp(C mouseOverComp) {
		this.mouseOverComp = mouseOverComp;
		visualChange();
	}
	
	/**
	 * @see SelectionManager
	 */
	public void addToSelection(C comp) {
		if (!selection.contains(comp))
			selection.add(comp);
		visualChange();
	}
	
	/**
	 * @see SelectionManager
	 */
	public void removeFromSelection(C comp) {
		if (selection.contains(comp))
			selection.remove(comp);
		visualChange();
	}
	
	/**
	 * @see SelectionManager
	 */
	public void setSelection(Collection<C> selectedComps) {
		clearSelection();
		if (selectedComps != null) 
			selection.addAll(selectedComps);
		visualChange();
	}
}
