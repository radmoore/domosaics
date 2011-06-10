package angstd.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import angstd.model.workspace.CategoryElement;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.Workspace;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.views.view.View;
import angstd.ui.views.view.ViewInfo;
import angstd.ui.workspace.WorkspaceSelectionManager;
import angstd.ui.workspace.WorkspaceView;

/**
 * The WorkspaceManager is the interface to all workspace related classes.
 * <p>
 * This class provides methods for workspace manipulation and communication
 * with the ViewHandler. Also some helper methods are provided s well as
 * a method to get a grip on the selectionManager. 
 * <p>
 * This class follows the singleton pattern and should be used for any 
 * manipulations of the workspace.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class WorkspaceManager {
	
	/** static reference to itself following the singleton pattern */
	protected static WorkspaceManager instance;
	
	/** internal mapping between the unique view id and their corresponding view elements */
	protected Map<Integer, ViewElement> info2view;
	
	/** the workspace data model */
	protected Workspace workspace;
	
	/** the displaying workspace view being a JTree */
	protected WorkspaceView workspaceView;
	
	/** the selection manager for the workspace view */
	protected WorkspaceSelectionManager selectionManager;

	
	/**
	 * Constructor for the workspace manager, initializing all components
	 * for the workspace creation. This class follows the singleton pattern and
	 * therefore the constructor is protected.
	 * To get an instance of this class use getInstance()
	 */
	protected WorkspaceManager() {
		workspace = new Workspace();
		workspaceView = new WorkspaceView(workspace);
		
		selectionManager = new WorkspaceSelectionManager();
		selectionManager.addController(workspaceView);
		
		// add the default project
		workspace.addProject("Default Project");
		
		info2view = new HashMap<Integer, ViewElement>();
	}
	
	public void forceRepaint() {
		workspaceView.repaint();
	}
	
	public ProjectElement addProject(String projectName, boolean select) {
		ProjectElement res = workspace.addProject(projectName);
		if (select)
			selectionManager.setSelectedElement(res);
		return res;
	}
	
	/**
	 * Returns the view element with the specified ViewInfo
	 * 
	 * @param view
	 * 		the viewInfo used to search for the view
	 * @return
	 * 		the view element found using the specified viewInfo
	 */
	public ViewElement getViewElement(ViewInfo view) {
		return workspace.getViewElement(view);
	}
	
	
	/**
	 * Returns a list of all sequence views within all projects.
	 * 
	 * @return
	 * 		list of all sequence views within all projects.
	 */
	public List<WorkspaceElement> getSequenceViews() {
		return workspace.getSequenceViews();
	}
	
	/**
	 * Returns a list of all domain views within all projects.
	 * 
	 * @return
	 * 		list of all domain views within all projects.
	 */
	public List<WorkspaceElement> getDomainViews() {
		return workspace.getDomainViews();
	}
	
	
	// FIXME see Workspace.getDomainViewsWithEmptyElem
//	public List<WorkspaceElement> getDomainViewsWithEmpty() {
//		return workspace.getDomainViewsWithEmptyElem();
//	}
	
	/**
	 * Returns a list of all tree views within all projects.
	 * 
	 * @return
	 * 		list of all tree views within all projects.
	 */
	public List<WorkspaceElement> getTreeViews() {
		return workspace.getTreeViews();
	}
	
	/**
	 * Returns all projects within the workspace.
	 * 
	 * @return
	 * 		all projects within the workspace
	 */
	public ProjectElement[] getProjects() {
		return workspace.getProjects().toArray(new ProjectElement[workspace.getProjects().size()]);
	}
	
	
//	public ViewElement[] getViews() {
//		return workspace.getV
//	}
	
	
	/**
	 * Wrapper around the workspace add project method
	 * 
	 * @param project
	 * 		the project to add 
	 * @param select
	 * 		flag whether or not the added project should be selected
	 */
	public void addProject(ProjectElement project, boolean select) {
		workspace.addProject(project);
		if (select)
			selectionManager.setSelectedElement(project);
	}
	
	/**
	 * Method to get an instance of the Workspace manager, following
	 * the singleton pattern.
	 * 
	 * @return
	 *		instance of the WorkspaceManager
	 */
	public static WorkspaceManager getInstance() {
		if(instance == null)
			instance = new WorkspaceManager();
		return instance;
	}
	
	public ProjectElement getProject(String name) {
		return workspace.getProject(name.trim());
	}
	
	/**
	 * Return the workspace embedding panel.
	 * 
	 * @return
	 * 		the workspace embedding panel to use within the desktop
	 */
	public JPanel getWorkspacePanel() {
		return workspaceView.getParentPane();
	}
	
	public WorkspaceView getWorkspaceView() {
		return workspaceView;
	}
	
	/**
	 * Returns the selectionManager, which is needed for some methods within
	 * the workspace package. For instance if a view should be deleted, its
	 * good to know which view was chosen.
	 * 
	 * @return
	 * 		the selection manager
	 */
	public WorkspaceSelectionManager getSelectionManager() {
		return selectionManager;
	}
	
	/**
	 * Changes the name of a workspace element. This method
	 * is triggered by the workspace context menu "rename".
	 * 
	 * @param element
	 * 		the element to change
	 * @param newName
	 * 		the new name for the element
	 */
	public void changeElementName(WorkspaceElement element, String newName) {
		workspace.changeElementName(element, newName);
	}
	
	/**
	 * Return the first project within the workspace. This method is for 
	 * instance needed when the actual project should be auto selected within
	 * a wizard, but no project was actually selected before.
	 * 
	 * @return
	 * 		first (most top) project within the workspace
	 */
	public WorkspaceElement getFirstProject() {
		return workspace.getChildAt(0);
	}
	
	
	public boolean projectExists(String name) {
		return workspace.projectExists(name);
	}
	
	public boolean viewExists(String projectName, CategoryElement cat, String name) {
		ProjectElement project = workspace.getProject(projectName);
		return project.viewExists(name, cat);
	}
	
	
	/* *************************************************************** *
	 * 			Methods for communicating with the ViewHandler 		   *
	 * *************************************************************** */
	
	/** 
	 * Adds the new view to the specified project within the Workspace.
	 * If the project is null, the currently active project is taken.
	 * If there are no projects in the workspace anymore, e.g.
	 * after deletion the default project is added again.
	 * 
	 * @param view
	 * 		the view to add
	 * @param project
	 * 		the project to which the view should be added
	 */
	public void addViewToWorkspace(View view, ProjectElement project) {
		if (project == null) {
			// if no project was specified take the actual selected project
			project = selectionManager.getSelectedProject();

			// if project is still null, e.g. there are now projects in the workspace after deletion
			// create a new Default project
			if (project == null) 
				project = workspace.addProject("Default Project"); 
		}
		
		info2view.put(view.getViewInfo().getID(), workspace.addView(project, view.getViewInfo()));
	}
	
	/**
	 * Gets only called when view elements are deleted by the 
	 * workspace context menu option "delete".
	 * In a first step all views are deleted from the workspace 
	 * and in a second step they are also removed from the {@link ViewHandler}.
	 * Do not call this method from within the ViewHandler or else you
	 * will get to an infinite loop. If you want to clean the workspace from
	 * within the ViewHandler, e.g. because a view was closed use 
	 * removeViewFromWorkspace().
	 * 
	 * @param element
	 * 		the element to be deleted (e.g. project, category, view)
	 */
	public void removeFromAngstd (WorkspaceElement element) {
		// gather all views which have to be deleted by the ViewHandler
		// also delete all necessary elements from the workspace
		ViewElement[] views = workspace.removeElement(element);
		
		// now delete the views
		for (int i = 0; i < views.length; i++) {
			ViewHandler.getInstance().removeView(views[i].getViewInfo());
			info2view.remove(views[i].getViewInfo().getID());
		}
	}
	
	/**
	 * Method to remove views from the workspace based on its viewInfo
	 * 
	 * @param viewInfo
	 * 		the viewInfo describing the view which is about to be deleted
	 */
	public void removeViewFromWorkspace (ViewInfo viewInfo) {
		ViewElement view = info2view.get(viewInfo.getID());
		if (view == null) // e.g. not clicked on a view
			return;
		workspace.removeElement(view);
		info2view.remove(viewInfo.getID());
	}

	/**
	 * Triggered by the workspace context menu action "show". Enables
	 * the view display of the chosen view within the viewHandler.
	 * 
	 * @param view
	 * 		the view to be displayed
	 */
	public void showViewInMainFrame(ViewElement view) {
		ViewHandler.getInstance().enableView(view.getViewID());
	}

}
