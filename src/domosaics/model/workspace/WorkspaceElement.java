package domosaics.model.workspace;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * Abstract WorkspaceElement defines the basic methods which apply to all
 * kinds of workspace elements. For instance the methods used by the TreeModel.
 * <p>
 * In general there exist 4 types of workspace elements, the workspace itself,
 * projects, view categories and views.
 * 
 * @author Andreas Held
 *
 */
public abstract class WorkspaceElement {
	private static final long serialVersionUID = 1L;
	
	/** type flag indicating that the element is a workspace element */
	public static final int WORKSPACE = 0; 
	
	/** type flag indicating that the element is a project element */
	public static final int PROJECT = 1;
	
	/** type flag indicating that the element is a view element */
	public static final int VIEW = 2;
	
	/** type flag indicating that the element is a category element */
	public static final int CATEGORY = 3;
	
	public static final int NULLELEM = 4;
	
	/** string to address project types within a message */
	public static final String PROJECT_NAME = "project";
	
	/** string to address category types within a message */
	public static final String CATEGORY_NAME = "category";
	
	/** string to address view types within a message */
	public static final String VIEW_NAME = "view";
	
	/** type of the workspace element */
	protected int type;
	
	/** title of the workspace element */
	protected String title;
	
	/** icon for the workspace element */
	protected ImageIcon icon;
	
	/** list of all assigned children for this element */
	protected List<WorkspaceElement> children = new ArrayList<WorkspaceElement>();
	
	/** parent element of this workspace element */
	protected WorkspaceElement parent;
	
	
	/**
	 * Constructor for a new Workspace element
	 * 
	 * @param type
	 * 		type of the workspace element
	 * @param title
	 * 		title of the workspace element
	 */
	public WorkspaceElement(int type, String title) {
		this.type = type;
		this.title = title;
	}
	
	/**
	 * Returns the icon for the workspace element.
	 * 
	 * @return
	 * 		icon for the workspace element
	 */
	public abstract ImageIcon getIcon();

	/**
	 * Return the project where the element is assigned to.
	 * 
	 * @return
	 * 		project where the element is assigned to
	 */
	public abstract ProjectElement getProject();
	
	
	/**
	 * Return the name of the element. This can for instance be used 
	 * within message dialogs when the message is created automatically.
	 * E.g. for a delete dialog.
	 * 
	 * @return
	 * 		name of the workspace element type
	 */
	public String getTypeName() {
		switch (type) {
			case PROJECT: return PROJECT_NAME;
			case CATEGORY: return CATEGORY_NAME;
			case VIEW: return VIEW_NAME;
		}
		return null;
	}
	
	/**
	 * Checks whether or not the Workspace element is a view 
	 * 
	 * @return
	 * 		whether or not the Workspace element is a view 
	 */
	public boolean isView() {
		return type == VIEW;
	}
	
	/**
	 * Checks whether or not the Workspace element is a view category
	 * 
	 * @return
	 * 		whether or not the Workspace element is a view category
	 */
	public boolean isCategory() {
		return type == CATEGORY;
	}
	
	/**
	 * Checks whether or not the Workspace element is a project
	 * 
	 * @return
	 * 		whether or not the Workspace element is a project
	 */
	public boolean isProject() {
		return type == PROJECT;
	}
	
	/**
	 * Returns the title from the workspace element
	 * 
	 * @return
	 * 		title from the workspace element
	 */
	public String getTitle() {
		return title;
	}
	
	
	/**
	 * Returns a short version of the title of the 
	 * workspace element
	 * 
	 * @param
	 *      length of short title in characters
	 * 
	 * @return
	 * 		shortend title of the workspace element
	 */
	public String getShortTitle(int length) {
		if (title.length() <= length)
			return title;
			
		return title.substring(0, length)+"... ";
	}
	
	
	
	/**
	 * Sets the title of the workspace element
	 * 
	 * @param newTitle
	 * 		new title
	 */
	public void setTitle(String newTitle) {
		title = newTitle;
	}
	
	/**
	 * Returns parent element
	 * 
	 * @return
	 * 		parent element
	 */
	public WorkspaceElement getParent() {
		return parent;
	}
	
	/**
	 * Sets the parent element for this element
	 * 
	 * @param parent
	 * 		new parent element
	 */
	public void setParent(WorkspaceElement parent) {
		this.parent = parent;
	}

	/**
	 * Return the child element at the specified index
	 * 
	 * @param index
	 * 		index of the requested child element
	 * @return
	 * 		child element at the specified index
	 */
	public WorkspaceElement getChildAt(int index){
		if(index >=0 && index < children.size())
			return children.get(index);
		return null;
	}

	/**
	 * Returns the number of assigned children
	 * 
	 * @return
	 * 		number of assigned children
	 */
	public int getChildCount() {
		return children.size();
	}
	
	/**
	 * Returns the index of a child
	 * 
	 * @param child
	 * 		child which index was requested
	 * @return
	 * 		index of a child
	 */
	public int getIndex(WorkspaceElement child) {
		return children.indexOf(child);
	}

	/**
	 * Adds a new child to the element
	 * 
	 * @param newChild
	 * 		child to add
	 * @return
	 * 		added child
	 */
    public WorkspaceElement addChild(WorkspaceElement newChild) {   
    	// remove child from old parent if it has one
    	if(newChild.getParent() != null && newChild.getParent() != this)
    		newChild.getParent().removeChild(newChild); 
    		
    	// add the child to the new parent
		if(children.add(newChild))		
			newChild.setParent(this);
		
		return newChild;
    }  
    
    /** 
     * Removes a child from the element
     * 
     * @param c
     * 		child to remove
     */
	public void removeChild(WorkspaceElement c) {	
		if(children.remove(c))
			c.setParent(null);
	}
	
	/**
	 * Return a list of all children as array
	 * 
	 * @return
	 * 		all children as array
	 */
	public List<WorkspaceElement> getChildren() {
		return children;
	}

}
