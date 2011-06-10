package angstd.model.workspace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.view.ViewInfo;
import angstd.ui.workspace.WorkspaceTreeModel;
import angstd.ui.workspace.components.WorkspaceChangeEvent;
import angstd.ui.workspace.components.WorkspaceChangeListener;

/**
 * Workspace is the top level hierarchy from all workspace elements.
 * <p>
 * In general {@link ProjectElement}s are assigned to this workspace element
 * as children.
 * <p>
 * {@link WorkspaceChangeListener}s can be assigned to a workspace which are
 * then notified when the workspace changes. This is for instance the 
 * {@link WorkspaceTreeModel} managing the tree structure of the JTree displayed
 * as the {link WorkspaceView}.
 * 
 * @author Andreas Held
 *
 */
public class Workspace extends WorkspaceElement {
	private static final long serialVersionUID = 1L;
	
	/** all listeners assigned to the workspace */
	protected List<WorkspaceChangeListener> listenerList = new ArrayList<WorkspaceChangeListener>();
	
	
	/**
	 * Constructor for a new workspace
	 */
	public Workspace() {
		super(WorkspaceElement.WORKSPACE, "Angstd Workspace");
	}
	
	/**
	 * Changes an elements name within the workspace
	 * 
	 * @param element
	 * 		the element where the name has to be changed 
	 * @param newName
	 * 		the name to be changed in
	 */
	public void changeElementName(WorkspaceElement element, String newName) {
		element.setTitle(newName);
		fireChangeEvent(element, false, true, false);
	}
	
	/* **************************************************************** *
	 *				Methods for adding Views and Projects				*
	 * **************************************************************** */
	
	/**
	 * Adds a new project to the workspace. A new Project element is
	 * created automatically.
	 * 
	 * @param projectName
	 * 		the name of the project to be added
	 */
	public ProjectElement addProject(String projectName) {
		ProjectElement project = new ProjectElement(projectName);
		addProject(project);
		return project;
	}
	
	/**
	 * Adds a new project to the workspace. 
	 * 
	 * @param project
	 * 		the project to be added
	 */
	public ProjectElement addProject(ProjectElement project) {
		addChild(project);
		fireChangeEvent(project, true, false, false);
		return project;
	}
	
	/**
	 * Adds a new view to a specified project
	 * 
	 * @param project
	 * 		the project to which the view is added
	 * @param viewInfo
	 * 		the information about the view to be added
	 */
	public ViewElement addView (ProjectElement project, ViewInfo viewInfo) {
		ViewElement addedViewElement = project.addView(viewInfo);
		
		// select the view within the workspace
		WorkspaceManager.getInstance().getSelectionManager().setSelectedElement(addedViewElement);
		
		fireChangeEvent(addedViewElement, true, false, false);
		return addedViewElement;
	}
	
	/* **************************************************************** *
	 *							Remove Element							*
	 * **************************************************************** */
	
	/**
	 * When removing an element from the workspace the procedure
	 * has to vary based on the elements type, because the views
	 * also have to be removed from the ViewHandler and if active
	 * from the AngstdDesktop.
	 * <p>
	 * The view (TreeModel) is changed automatically by sending him a
	 * change event.
	 * 
	 * @param element
	 * 		the element to remove 
	 */
	public ViewElement[] removeElement(WorkspaceElement element) {
		ViewElement[] viewElem;
		
		if (element.isView()) 
			viewElem = removeView(element);
		else if (element.isCategory()) {
			CategoryElement catElem = (CategoryElement) element;
			viewElem = ((ProjectElement) catElem.getParent()).removeCategory(catElem);
		} else { // delete project
			viewElem = removeProject((ProjectElement) element);
		}
		
		fireChangeEvent(this, false, false, true);
		return viewElem;
	}
	
	/**
	 * Helper method for removing workspace elements 
	 * 
	 * @param element
	 * 		the elemnt to be removed
	 * @return
	 * 		the temoved view elements
	 */
	private ViewElement[] removeView(WorkspaceElement element) {
		ViewElement[] res = new ViewElement[1];
		res[0] = (ViewElement) element;
		
		// gather ViewElement and its category from where it will be deleted
		ViewElement viewElem = (ViewElement) element;
		CategoryElement catElem = (CategoryElement) viewElem.getParent();
		
		// remove view from the workspace
		catElem.removeView(viewElem);
		
		// if category is empty now remove it as well from its project
		if (catElem.countViews() == 0) 
			((ProjectElement) catElem.getParent()).removeCategory(catElem);
		
		return res;
	}
	
	/**
	 * Helper method for removing workspace elements 
	 * 
	 * @param project
	 * 		the project to be removed
	 * @return
	 * 		removed views from the deleted project
	 */
	public ViewElement[] removeProject(ProjectElement project) {
		List<ViewElement> res = new ArrayList<ViewElement>();
		
		// start by removing all categorys inclusive views
		if (project.getChildCount()>0) {
			CategoryElement[] catElem = (CategoryElement[]) project.getChildren().toArray(new CategoryElement[project.getChildCount()]);
			for (int i =0; i< catElem.length; i++) {
				ViewElement[] dummy = project.removeCategory(catElem[i]);
				for (int j = 0; j < dummy.length; j++)
					res.add(dummy[j]);
			}
				
		}
		// then remove the project from the workspace
		this.removeChild(project);
		project.setParent(null);
		
		return res.toArray(new ViewElement[res.size()]);
	}

	/* **************************************************************** *
	 *								OTHER								*
	 * **************************************************************** */
	
	/**
	 * Return all projects
	 * 
	 * @return	
	 * 		all projects	
	 */
	public List<WorkspaceElement> getProjects() {
		return children;
	}
	
	
//	public List<ViewElement> getViews() {
//		for (WorkspaceElement elem : children)
//	}
//	
	
	/**
	 * Get a project specified by name
	 * @param name
	 *     	name of a project
	 * @return
	 * 		ProjectElement or null
	 */
	public ProjectElement getProject(String name) {
		for (WorkspaceElement element : children)
			if (element.getTitle().equals(name))
				return (ProjectElement) element;
		return null;
	}
	
	/**
	 * Checks whether a Project with Name already exists
	 * in the workspace
	 * @param name
	 * @return 
	 * 		true if a project exists with name, false otherwise
	 */
	public boolean projectExists(String name) {
		for (WorkspaceElement element : children)
			if (element.getTitle().equals(name))
				return true;
		
		return false;
	}
	
	/**
	 * Number of all assigned projects
	 * 
	 * @return
	 * 		number of all assigned projects
	 */
	public int countProjects() {
		return children.size();
	}

	/**
	 * Returns the icon for the workspace element.
	 * 
	 * @return
	 * 		icon for the workspace element 
	 */
	public ImageIcon getIcon() {
		return null;
	}
	
	/**
	 * @see WorkspaceElement
	 */
	public ProjectElement getProject() {
		return null;
	}

	/* **************************************************************** *
	 *						Listener Methods							*
	 * **************************************************************** */
	
	/**
	 * Registers a new listener to the workspace.
	 * 
	 * @param listener
	 * 		listener which shall be registered.
	 */
	public void addWorkspaceChangeListener(WorkspaceChangeListener listener){
		if (listenerList == null)
			listenerList = new ArrayList<WorkspaceChangeListener>();
		listenerList.add(listener);
	}

	/**
	 * Removes a new listener from the workspace.
	 * 
	 * @param listener
	 * 		listener which shall be removed.
	 */
	public void removeWorkspaceChangeListener(WorkspaceChangeListener listener){
		listenerList.remove(listener);
	}
	
	/**
	 * Fires a workspace change event to all registered listeners.
	 * 
	 * @param e
	 * 		the event to be fired to all listeners.
	 */
	protected void fireWorkspaceChangeEvent(WorkspaceChangeEvent e) {
		Iterator<WorkspaceChangeListener> iter = listenerList.iterator();
		while(iter.hasNext()) {
			WorkspaceChangeListener l = iter.next();
			if(e.isAdded())
				l.nodeAdded(e);
			if(e.isChanged())
				l.nodeChanged(e);
			if(e.isRemoved())
				l.nodeRemoved(e);
		}
	}	

	/**
	 * Creates a new ChangeEvent, which is then fired to all registered listeners.
	 * 
	 * @param added
	 * 		specifies whether or not the node was added
	 * @param changed
	 * 		specifies whether or not the node was changed
	 * @param removed
	 * 		specifies whether or not the node was removed
	 */
	protected void fireChangeEvent(WorkspaceElement element, boolean added, boolean changed, boolean removed){
		WorkspaceChangeEvent e = new WorkspaceChangeEvent(element, added, changed, removed);	
		fireWorkspaceChangeEvent(e);		
	}
	
	/* **************************************************************** *
	 *							GET VIEWS								*
	 * **************************************************************** */

	/**
	 * Returns all sequence views from all projects.
	 * 
	 * @return
	 * 		all sequence views from all projects.
	 */
	public List<WorkspaceElement> getSequenceViews() {
		List<WorkspaceElement> res = new ArrayList<WorkspaceElement>();
		ProjectElement[] projects = getProjects().toArray(new ProjectElement[getProjects().size()]);
		for (int i = 0; i < projects.length; i++) {
			CategoryElement seqCat = projects[i].getCategory(ViewType.SEQUENCE);
			if (seqCat != null) 
				res.addAll(seqCat.getViews());
		}	
		return res;
	}
	
	/**
	 * Returns all tree views from all projects.
	 * 
	 * @return
	 * 		all tree views from all projects.
	 */
	public List<WorkspaceElement> getTreeViews() {
		List<WorkspaceElement> res = new ArrayList<WorkspaceElement>();
		ProjectElement[] projects = getProjects().toArray(new ProjectElement[getProjects().size()]);
		for (int i = 0; i < projects.length; i++) {
			CategoryElement treeCat = projects[i].getCategory(ViewType.TREE);
			if (treeCat != null) 
				res.addAll(treeCat.getViews());
		}	
		return res;
	}
	
	/**
	 * Returns all arrangement views from all projects.
	 * 
	 * @return
	 * 		all arrangement views from all projects.
	 */
	public List<WorkspaceElement> getDomainViews() {
		
		List<WorkspaceElement> res = new ArrayList<WorkspaceElement>();
		ProjectElement[] projects = getProjects().toArray(new ProjectElement[getProjects().size()]);
		for (int i = 0; i < projects.length; i++) {
			CategoryElement domCat = projects[i].getCategory(ViewType.DOMAINS);
			if (domCat != null) 
				res.addAll(domCat.getViews());
		}	
		return res;
	}
	
	
	/**
	 * FIXME:
	 * Add a null elem to the list of views for use in
	 * comboboxes (such that there is a 'non-selectable' element
	 * 
	 * Returns all arrangement views from all projects.
	 * 
	 * @return
	 * 		all arrangement views from all projects.
	 */
//	public List<WorkspaceElement> getDomainViewsWithEmptyElem() {
//		
//		List<WorkspaceElement> res = new ArrayList<WorkspaceElement>();
//		ProjectElement[] projects = getProjects().toArray(new ProjectElement[getProjects().size()]);
//		
//		for (ProjectElement elem : projects)  {
//			CategoryElement domCat = elem.getCategory(ViewType.DOMAINS);
//			if (domCat != null)
//				res.addAll(domCat.getViews());
//		}
//		
//		res.add(new WorkspaceElement(WorkspaceElement.NULLELEM, "") {
//			public ProjectElement getProject() {return null;}
//			public ImageIcon getIcon() {return null;}
//		});
//		
//		return res;
//		
//	}
	
	
	/**
	 * Returns the view element with the specified ViewInfo
	 * 
	 * @param view
	 * 		the viewInfo used to search for the view
	 * @return
	 * 		the view element found using the specified viewInfo
	 */
	public ViewElement getViewElement(ViewInfo view) {
		for (int i = 0; i < countProjects(); i++) {
			ViewElement viewElt = ((ProjectElement) getChildAt(i)).getView(view);
			if (viewElt != null)
				return viewElt;
		}
		return null;
	}
	
//	public void changeViewName(ViewInfo view, String newName) {
//		ViewElement viewElt = getViewElement(view);
//		if (viewElt != null) {
//			viewElt.setTitle(newName);
//			fireChangeEvent(viewElt, false, true, false);
//		} else {
//			System.out.println("Failed to find view "+view.getName());
//		}
//	}
	
//	public TreeViewI getTreeViewByName(ProjectElement project, String name) {
//		CategoryElement treeCat = project.getCategory(ViewType.TREE);
//		if (treeCat == null) 
//			return null;
//		for (int i = 0; i < treeCat.countViews(); i++) 
//			if (((ViewElement) treeCat.getChildAt(i)).getTitle().equals(name))
//				return ViewHandler.getInstance().getView(((ViewElement) treeCat.getChildAt(i)).getViewInfo());
//		return null;
//	}
//	
	public DomainViewI getDomainViewByName(ProjectElement project, String name) {
		CategoryElement domCat = project.getCategory(ViewType.DOMAINS);
		if (domCat == null) 
			return null;
		for (int i = 0; i < domCat.countViews(); i++) 
			if (((ViewElement) domCat.getChildAt(i)).getTitle().equals(name))
				return ViewHandler.getInstance().getView(((ViewElement) domCat.getChildAt(i)).getViewInfo());
		return null;
	}
//	
//	public SequenceView getSequenceViewByName(ProjectElement project, String name) {
//		CategoryElement seqCat = project.getCategory(ViewType.SEQUENCE);
//		if (seqCat == null) 
//			return null;
//		for (int i = 0; i < seqCat.countViews(); i++) 
//			if (((ViewElement) seqCat.getChildAt(i)).getTitle().equals(name))
//				return ViewHandler.getInstance().getView(((ViewElement) seqCat.getChildAt(i)).getViewInfo());
//		return null;
//	}

}