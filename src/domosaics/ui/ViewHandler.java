package domosaics.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.docking.DoMosaicsDesktop;
import domosaics.ui.tools.Tool;
import domosaics.ui.views.DoMosaicsViewFactory;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.ViewInfo;


/**
 * Class ViewManager manages the creation, adding and removing of view 
 * objects to DoMosaics and its main frame. The adding to the workspace
 * view is delegated to the {@link WorkspaceManager}.
 * <p>
 * The class follows the singleton pattern, which means only one instance is
 * created during the applications lifetime. To get an instance of ViewManager,
 * use the static {@link #getInstance()} method.
 * <p>
 * To add a view to the main frame its necessary to call in a first step
 * the {@link #createView(ViewType, String)} method where a new View object is
 * created using the {@link DoMosaicsViewFactory}.
 * After the object is created it has to be added by calling 
 * {@link #addView(View, ProjectElement)}. Now for everything is taken care
 * of (meaning e.g. the view is added to the workspace automatically as well).
 * <p>
 * Because Tools like the DomainDotplot or the DomainLegend are views as well 
 * (the difference is they are stored in an extra frame and not within the workspace)
 * This class also manages the tool handling.
 * 
 * 
 * 
 * @author Andreas Held
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 * 
 * 
 */
public class ViewHandler {
	
	/** instance of ViewHandler which follows the singleton pattern */
	private static ViewHandler instance;
	
	/** a map of the views which are already created */
	private Map<Integer, View> views;
	
	/** a map of the tools which are already created */
	private Map<ViewType, View> tools;
	
	/** the id of the actual displayed view */
	private int actView;

	
	/**
     * Create a new ViewManager instance. This is a protected constructor 
     * to support the singleton pattern. To get an instance of ViewManager, 
     * use the static {@link #getInstance()} method. 
	 */
	protected ViewHandler() {
		views = new HashMap<Integer, View>();
		tools = new HashMap<ViewType, View>();
		actView = -1;
	}
	
    /**
     * Returns the current ViewManager instance. If not initialized, 
     * it creates a new instance. This is the method which should be used
     * to initialize the ViewManager.
     * 
     * @return 
     * 		the ViewManager instance
     */
	public static ViewHandler getInstance() {
		if (instance == null)
			instance = new ViewHandler();
		return instance;
	}
	
	/* **************************************************************** *
	 * 							VIEW METHODS							*
	 * **************************************************************** */
	
	public Collection<View> getViews() {
		return views.values();
	}
	
	/**
	 * Uses the {@link DoMosaicsViewFactory} to create a new view object of the 
	 * specified type. This method does not automatically add the created
	 * object to the DoMosaicsDesktop. To do that its necessary to invoke 
	 * addView() with the created view object.
	 *  
	 * @param <V>
	 * 		the generic view type (e.g. DomainViewI)
	 * @param type
	 * 		the type of the view to be created (e.g. ViewType.DOMAINS)
	 * @param name
	 * 		the name for the view displayed in the workspace
	 * @return
	 * 		the created view object for the specified type
	 */
	public <V extends View> V createView(ViewType type, String name) {
		V view = DoMosaicsViewFactory.createView(type);
		view.getViewInfo().setName(name);
		view.getParentPane().setName(name);
		return view;
	}

	/**
	 * Adds a view to the {@link DoMosaicsDesktop} and to the workspace. The
	 * view is stored in the view mapping using its ID. Therefore this id 
	 * must be unique (associated automatically when using the 
	 * {@link DoMosaicsViewFactory}. <br>
	 * The project is allowed to be null in which case the view is added to the 
	 * currently selected project element.
	 * <p>
	 * If a domain tree view is added the underlying backend views are
	 * deleted from the workspace as well as from this manager.
	 * 
	 * @param view
	 * 		The view to be added
	 * @param project
	 * 		The project to which the view should be added (allowed to be null)
	 * @param show
	 * 		flag whether or not the view should be shown after it was added
	 */
	public void addView(final View view, final ProjectElement project, final boolean show) {
		SwingUtilities.invokeLater(new Runnable() {						
			@Override
			public void run() {			
				// add view to viewHandler
				int viewID = view.getViewInfo().getID();
				views.put(viewID, view);
				
				if(!show) {
					WorkspaceManager.getInstance().addViewToWorkspace(view, project);
					return;
				}
				
				// remove the old view from the desktop
				if (getActiveView() != null)
					disableActiveView();

				// now make the new view internally active
				actView = viewID;

				// and add it to workspace
				WorkspaceManager.getInstance().addViewToWorkspace(view, project);

				// if the view is a domain tree view , remove the backend views from the workspace
				/*if (view instanceof DomainTreeViewI) {
					removeView(((DomainTreeViewI) view).getDomainView().getViewInfo());
					removeView(((DomainTreeViewI) view).getTreeView().getViewInfo());
				}*/
				
				// finally add the ViewPanel to the MainFrame
				DoMosaicsUI.getInstance().addView(view);
//				view.setChanged(changed);
			}
		});
	}
	
	public void addView(final View view, final ProjectElement project) {
		addView(view, project, true);
	}
	

	/**
	 * Completely eliminates a view from DoMosaics. If you are just interested 
	 * in removing the view from the desktop but not from the workspace, 
	 * you may be interested in disableView().
	 *  
	 * @param info
	 * 		the view to remove
	 */
	public void removeView(final ViewInfo info) {
		SwingUtilities.invokeLater(new Runnable() {						
			@Override
			public void run() {			
				int viewID = info.getID();	
				View view = views.get(viewID);
				
				if (view == null) // e.g. backend views arent in the workspace because of view import
					return;

				// if the deleted view is displayed remove it from the frame
				if (getActiveView() != null && getActiveView().getViewInfo().getID() == view.getViewInfo().getID()) {
					disableActiveView();
					view.getParentPane().dispose();
				}
				
				views.remove(viewID);
				
				// delete it from the workspace as well
				WorkspaceManager.getInstance().removeViewFromWorkspace(view.getViewInfo());
	
				System.gc();
			}
		});
	}
	
	/**
	 * enables a view which means that it gets displayed within the mainframe.
	 * 
	 * @param viewID
	 * 		the viewID of the view which is about to be displayed
	 */
	public void enableView(final int viewID) {
		SwingUtilities.invokeLater(new Runnable() {						
			@Override
			public void run() {
				
				// if the view is already displayed do nothing
				if (actView == viewID)
					return;
		
				// remove the old view from the desktop
				if (getActiveView() != null)
					disableActiveView();
		
				// and the new view to active
				actView = viewID;
				
				DoMosaicsUI.getInstance().addView(views.get(viewID));
			}
		});
	}
	
	/**
	 * Removes the active view from the domosaics desktop.
	 */
	public void disableActiveView() {
		DoMosaicsUI.getInstance().removeView();
		actView = -1;
	}
	
	/**
	 * Returns the queried view based on its unique id stored within the 
	 * view info.
	 * 
	 * @param <V>
	 * 		the generic view type (e.g. DomainViewI)
	 * @param viewInfo
	 * 		the view info of the requested view
	 * @return
	 * 		the requested view
	 */
	@SuppressWarnings("unchecked")
	public <V extends View> V getView (ViewInfo viewInfo) {
		return (V) views.get(viewInfo.getID());
	}

	/**
	 * Counts the number of created views (not Tools) 
	 * stored in the workspace.
	 * 
	 * @return
	 * 		number of created views
	 */
	public int countViews() {
		return views.size();
	}

	/**
	 * Return the actually displayed View
	 * 
	 * @param <V>
	 * 		the generic view type (e.g. DomainViewI)
	 * @return
	 * 		actually displayed View
	 */
	@SuppressWarnings("unchecked")
	public <V extends View> V getActiveView() {
		if (actView != -1)
			return (V) views.get(actView);
		return null;
	}
	
	
	/* **************************************************************** *
	 * 							TOOL METHODS							*
	 * **************************************************************** */
	/**
	 * Check if a Tool is focused. This is used for exporting and
	 * closing actions to determine if a view or a tool has to be exported or
	 * closed.
	 *	 
	 * @return
	 * 		the focused tool or null
	 */
	public View getFocussedTool() {
		for (View tool : tools.values()) 
			if(((Tool)tool).getToolFrame().isFocused()) 
				return tool;
		return null;
	}
	
	/**
	 * Creates tools which open an external frame when launched.
	 * E.g. dot plot, domain graph etc. If there is already a tool
	 * open of this type its closed. <br>
	 * The adding to the ViewManager and displaying is not done here
	 * therefore the addTool method is used 
	 * (in most cases after the backend data was set)
	 *
	 * @param type
	 * 		the tool type to launch
	 */
	@SuppressWarnings("unchecked")
	public <V extends View> V createTool(ViewType type) {
		// first check if the tool is already opened if so remove it
		View tool = tools.get(type);
		if (tool != null) 
			removeTool(tool);
		
		// now create a new tool 
		tool = DoMosaicsViewFactory.createView(type);

		return (V) tool;
	}
	
	/**
	 * Adds a tool to the ViewManager and displays it.
	 * 
	 * @param tool
	 * 		the tool to add and display.
	 */
	public void addTool(final View tool) {
		((Tool) tool).getToolFrame().addView(tool);
		tools.put(tool.getViewInfo().getType(), tool);
	}
	
	
	/**
	 * Adss a tool to the ViewManager
	 * 
	 * @param tool
	 */
	public void addVisibleTool(final View tool) {
		tools.put(tool.getViewInfo().getType(), tool);
	}
	
	/**
	 * Remove a tool from the ViewHandler.
	 * 
	 * @param tool
	 */
	public void removeTool(final View tool) {
		tools.remove(tool.getViewInfo().getType());
		SwingUtilities.invokeLater(new Runnable() {						
			@Override
			public void run() {
				tool.getParentPane().dispose();
				((Tool) tool).getToolFrame().dispose();
				System.gc();
			}
		});
	}
	
	
	public void removeAllTools() {
		List<ViewType> views = new ArrayList<ViewType>( tools.keySet() );
		for ( ViewType view : views )
			tools.remove(view);
	}
	
	
	
	
	/**
	 * Return the tool object based on its ViewType.
	 * 
	 * @param <V>
	 * 		the generic view type (e.g. DotplotView)
	 * @param type
	 * 		the viewtype (e.g. ViewType.DOTPLOT)
	 * @return
	 * 		the requested tool object
	 */
	@SuppressWarnings("unchecked")
	public <V extends View> V getTool(ViewType type) {
		return (V) tools.get(type);
	}

}

