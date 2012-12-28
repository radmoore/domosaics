package angstd.ui.docking;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;


import angstd.ui.views.view.View;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableResolver;
import com.vlsolutions.swing.docking.DockingDesktop;


/**
 * Creates new dockable components such as "dockable desktops" which
 * manage other dockables or "view dockables" to add in desktop dockables.
 * <p>
 * An unique id for the component is taken to resolve it. 
 * In the case of views this is its unique id (created by the ViewFactory).
 *  
 * @author Andreas Held
 *
 */
public class DefaultDockableResolver implements DockableResolver {
	
	public static final String VIEW_DESKTOP_KEY = "ViewDesktop";
	public static final String DATA_DESKTOP_KEY = "DataDesktop";
	
	/** Maping of unique keyIDs to dockable components */
	protected Map<String, Dockable> dockables = new HashMap<String, Dockable>();


	/**
	 * Maps the key id to the stored dockable and returns it.
	 * 
	 * @param key
	 * 		the key of the requested dockable
	 * @return
	 * 		the dockable with the queried key
	 */
	public Dockable resolveDockable(String key) {
		Dockable dockable = dockables.get(key);
		if(dockable == null)
			System.out.println("Unable to resolve dockable " + key);
		return dockable;
	}

	/**
	 * Creates a new desktop dockable, for instance to store views in it.
	 * 
	 * @param desktopKey
	 * 		the key id for this dockable
	 * @return
	 * 		the dockable desktop component
	 * 
	 */
	public Dockable createDesktopDockable(String desktopKey) {	    
		SDockKey key = new SDockKey();
		
		// set attributes
		key.setAutoHideEnabled(false);
		key.setMaximizeEnabled(false);
		key.setCloseEnabled(false);
		key.setShowTitlebar(false);	
		key.setFloatEnabled(false);
		key.setResizeWeight(1f);
		key.setName(desktopKey);
		
		// create the dockable and store it
		DefaultDockable dockable = new DefaultDockable(new DockingDesktop(desktopKey), key);
		dockables.put(desktopKey, dockable);
		
		return dockable;
	}

	/**
	 * Creates a new dockable component to store at a docking desktop based
	 * on a view. As unique identifier the views id is taken.
	 * 
	 * @param view
	 * 		the view which should be added as dockable
	 * @return
	 * 		the created dockable for this view
	 */
	public Dockable createDockable(View view) {		 
		DockKey key = new DockKey();
		
		// set attributes
		key.setName(view.getParentPane().getName());
		key.setAutoHideEnabled(false);
		key.setMaximizeEnabled(false);
		key.setFloatEnabled(false);
		key.setCloseEnabled(false);
		key.setResizeWeight(1f);

		// create the 
		Dockable dock = new DefaultDockable(view, key);
		dockables.put(""+view.getViewInfo().getID(), dock);

		return dock;
	}
	

	/**
	 * Creates the workspace dockable out of the WorkspaceView
	 * 
	 * @param workspacePanel
	 * 		the workspace panel which will be created as dockable
	 * @return
	 * 		the dockable component for the workspace view
	 */
	public Dockable createWorkspaceDockable(JPanel workspacePanel) {		 
		DockKey key = new DockKey();
		
		// set attributes
		key.setName(workspacePanel.getName());
		key.setAutoHideEnabled(false);
		key.setMaximizeEnabled(false);
		key.setFloatEnabled(false);
		key.setCloseEnabled(false);
		key.setResizeWeight(0f);

		// create the 
		Dockable dock = new DefaultDockable(workspacePanel, key);
		dockables.put(workspacePanel.getName(), dock);

		return dock;
	}

	/**
	 * Get a grip on all dockables stored within the resolver.
	 * 
	 * @return
	 * 		all created dockable components
	 * 
	 */
	public Map<String, Dockable> getDockables() {
		return dockables;
	}
	

	
}
