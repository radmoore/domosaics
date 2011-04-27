package angstd.model.workspace;

import java.util.List;

import javax.swing.ImageIcon;

import angstd.ui.views.ViewType;
import angstd.ui.views.view.ViewInfo;

/**
 * CategoryElement describes Workspace elements, representing categories
 * for views respectively datasets.
 * <p>
 * In general {@link ViewElement}s are assigned to those workspace elements
 * as children and the ViewType is the backend structure to specify the
 * view category.
 * <p>
 *
 * @author Andreas Held
 *
 */
public class CategoryElement extends WorkspaceElement{

	/** backend view type represented by this category */
	protected ViewType type;
	
	/**
	 * Constructor for a new category workspace element based on the information
	 * from a view.
	 * 
	 * @param info
	 * 		information from a view
	 */
	public CategoryElement(ViewInfo info) {
		super(WorkspaceElement.CATEGORY, info.getWorkspaceFolderName());
		this.type = info.getType();
		this.icon = info.getWorkspaceFolderIcon();
	}
	
	/**
	 * Returns the ViewType which is represented by this category.
	 * 
	 * @return
	 * 		ViewType which is represented by this category.
	 */
	public ViewType getType() {
		return type;
	}
	
	/**
	 * Adds a new view to the workspace.
	 * 
	 * @param viewInfo
	 * 		information about the view to be added
	 */
	public ViewElement addView(ViewInfo viewInfo) {
		ViewElement child = new ViewElement(viewInfo);
		children.add(child);
		child.setParent(this);
		return child;
	}
	
	/**
	 * Removes a view from the category
	 * 
	 * @param view
	 * 		view to be removed
	 */
	public void removeView(ViewElement view) {
		children.remove(view);
		view.setParent(null);
	}
	
	/**
	 * Returns the icon for the category depending on the ViewType.
	 * 
	 * @return
	 * 		icon for the category depending on its ViewType.
	 */
	public ImageIcon getIcon() {
		return icon;
	}
	
	/**
	 * Returns the number of views assigned to this category
	 * 
	 * @return
	 * 		number of views assigned to this category
	 */
	public int countViews() {
		return getChildCount();
	}
	
	/**
	 * Returns a list of all assigned views to this element
	 * 
	 * @return
	 * 		list of all assigned views to this element 
	 */
	public List<WorkspaceElement> getViews() {
		return children;
	}
	
	/**
	 * @see WorkspaceElement
	 */
	public ProjectElement getProject() {
		return (ProjectElement) getParent();
	}
	
	/**
	 * Returns the view element with the specified ViewInfo
	 * 
	 * @param view
	 * 		the viewInfo used to search for the view
	 * @return
	 * 		the view element found using the specified viewInfo
	 */
	public ViewElement getView(ViewInfo info) {
		for (int i = 0; i < getChildCount(); i++) {
			ViewElement act = (ViewElement) getChildAt(i);
			if (info.equals(act.getViewInfo())) 
				return act;
		}
		return null;
	}
	
}
