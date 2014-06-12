package domosaics.ui.views.view.manager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Basic implementation of the ViewManager interface managing the
 * methods for communication with the observed view.
 * <p>
 * For details see {@link ViewManager} interface.
 * 
 * @author Andreas Held
 *
 */
public abstract class DefaultViewManager implements ViewManager {
	
	/** the PropertyChangeSupport to handle the notifying of registered listeners */
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	
	/* ******************************************************************* *
	 *   							Listener methods					   *
	 * ******************************************************************* */
	
	/**
	 * @see ViewManager
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
     
	/**
	 * @see ViewManager
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @see ViewManager
	 */
	@Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * @see ViewManager
	 */
	@Override
	public void structuralChange() {
		firePropertyChange(PROPERTY_STRUCTURAL_CHANGE, -1, 1);        
	}

	/**
	 * @see ViewManager
	 */
	@Override
	public void visualChange(){
		firePropertyChange(PROPERTY_VISUAL_CHANGE, -1, 1);
	}
}
