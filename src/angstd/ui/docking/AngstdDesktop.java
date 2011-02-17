package angstd.ui.docking;

import angstd.ui.AngstdUI;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.view.View;

import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;

/**
 * The Angstd docking desktop uses the external library VLDocking to 
 * arrange the main frame components such as the workspace or views.
 * <p>
 * An extended version of the VLDocking DockableContainerFactory 
 * (@link SDockableContainerFactory) as well as an extended version of 
 * the VLDocking Dockkey (@link SDockkey) is used to introduce a 
 * visibility flag for title bars. Therefore it is possible to 
 * display the view desktop virtually without title bar. 
 * <p>
 * The desktop can be split into two parts. First a data desktop which may 
 * contain the workspace and additional components like a logger. Second
 * the view desktop holding the last added view.
 * <p>
 * A {@link DefaultDockableResolver} creates the dockable components for 
 * backend components like a workspace view, which are then added to the 
 * corresponding desktop.
 * <p>
 * The actual adding, removing and name change for views is done by the desktop,
 * but accessed in {@link AngstdUI} which works as wrapper for those methods.
 * 
 * @author Andreas Held
 *
 */
public class AngstdDesktop extends DockingDesktop {
	private static final long serialVersionUID = 1L;

	/** stores the workspace */
	protected DockingDesktop dataDesktop;		
	
	/** stores the view dockables */
	protected DockingDesktop viewDesktop;	
	
	/** resolver to create new dockables */
	protected DefaultDockableResolver resolver;	
	
	/** the active view */
	protected Dockable viewDockable; 			
	
	
	/**
	 * Creates the main Desktop embedding all components.
	 */
	public AngstdDesktop() {
		super("Main Desktop");
		
		resolver = new DefaultDockableResolver();
		DockableContainerFactory.setFactory(new SDockableContainerFactory());

		// create the dockable components for view/data desktop and workspace
		Dockable viewDesktopDockable = resolver.createDesktopDockable(DefaultDockableResolver.VIEW_DESKTOP_KEY);
		Dockable dataDesktopDockable = resolver.createDesktopDockable(DefaultDockableResolver.DATA_DESKTOP_KEY);
		Dockable workspaceDockable = resolver.createWorkspaceDockable(WorkspaceManager.getInstance().getWorkspacePanel());
		
		viewDesktop = (DockingDesktop) viewDesktopDockable.getComponent();
	    dataDesktop = (DockingDesktop) dataDesktopDockable.getComponent();
	    dataDesktop.addDockable(workspaceDockable);
	    dataDesktop.setDockableHeight(workspaceDockable, 0.6);

		addDockable(viewDesktopDockable);
		split(viewDesktopDockable, dataDesktopDockable, DockingConstants.SPLIT_LEFT);
		setDockableWidth(viewDesktopDockable, 0.7);
	}
	
	/**
	 * Remove the old view from the view desktop and add the new one.
	 * 
	 * @param v
	 * 		the view to add
	 */
	public void addView(View v) {
		viewDesktop.clear();
		viewDockable = resolver.createDockable(v);
		viewDesktop.addDockable(viewDockable);
		viewDesktop.revalidate();
		viewDesktop.repaint();
	}
	
	/**
	 * Removes the actual displayed view
	 */
	public void removeView() {
		viewDesktop.clear();
		viewDesktop.repaint();
	}
	
	/**
	 * Changes the views name within the dockable bar.
	 * 
	 * @param newName
	 * 		the new view name
	 */
	public void changeViewName(String newName)  {
		viewDockable.getDockKey().setName(newName);
		viewDesktop.repaint();
	}

}
