package domosaics.ui.docking;

import java.awt.Component;


import domosaics.ui.views.view.View;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;


/**
 * Simple dockable wrapper around a component. This wrapper
 * gives the opportunity to determine between view dockables and other 
 * dockable components.
 * Therefore returns the ViewPanel from a view when requested.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDockable implements Dockable{

	/** backend component for this dockable */
	protected Component component;
	
	/** the dockkey for this component */
	protected DockKey key;
	
	/** backend view if this is a view dockable */
	protected View view;

	
	/**
	 * Constructor for a desktop dockable created within 
	 * DefaultDockableResolver.
	 * 
	 * @param component
	 * 		the dektop dockable component
	 * @param key
	 * 		the corresponding dockable key
	 */
	public DefaultDockable(Component component, DockKey key){
		super();
		this.component = component;
		this.key = key;		
	}
	
	/**
	 * Constructor for a view dockable created within 
	 * DefaultDockableResolver.
	 * 
	 * @param view
	 * 		the view which should be wrapped to a dockable component.
	 * @param key
	 * 		the dockable key for this view created by DefaultDockableResolver
	 */
	public DefaultDockable(View view, DockKey key){
		super();
		this.view = view;
		this.key = key;
	}

	/**
	 * Returns the backend component for this dockable.
	 * This can for instance be a desktop dockable or a view dockable.
	 * 
	 * @return
	 * 		requested backend component
	 */
	@Override
	public Component getComponent() {
		if(component == null)
			if(view != null)		
				return view.getParentPane();
		return component;
	}

	/**
	 * Return the dockkey for this dockable.
	 * 
	 * @return
	 * 		dockkey for this dockable.
	 */
	@Override
	public DockKey getDockKey() {
		return key;
	}
 
}
