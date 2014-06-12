package domosaics.model.workspace;

import javax.swing.ImageIcon;

import domosaics.ui.views.view.ViewInfo;




/**
 * ViewElement describes workspace elements, representing views which 
 * were loaded.
 * <p>
 * The backend view is stored as {@link ViewInfo} and can be accessed via a
 * ViewManager by getViewID().
 * 
 * @author Andreas Held
 *
 */
public class ViewElement extends WorkspaceElement {

	/** view info for the stored view */
	protected ViewInfo viewInfo;
	
	/**
	 * Constructor for a new ViewElement based on the ViewInfo about a view.
	 * 
	 * @param viewInfo
	 * 		ViewInfo specifying the view
	 */
	public ViewElement(ViewInfo viewInfo) {
		super(WorkspaceElement.VIEW, viewInfo.getName());
		this.viewInfo = viewInfo;
	}
	
	/**
	 * Returns the ViewInfo about the view represented by this workspace element.
	 * 
	 * @return
	 * 		ViewInfo about the view represented by this workspace element.
	 */
	public ViewInfo getViewInfo() {
		return viewInfo;
	}
	
	/**
	 * Sets the title for this view element.
	 * 
	 * @param newTitle
	 * 		new title for this element
	 */
//	public void setTitle(String newTitle) {
//		super.setTitle(newTitle);
//		viewInfo.setName(newTitle);
//		ViewHandler.getInstance().getView(viewInfo).getParentPane().setName(newTitle);
//	}
	
	/**
	 * Returns the view id for the represented view. This can be used to 
	 * access the view from a ViewManager.
	 * 
	 * @return
	 * 		view id for the represented view
	 */
	public int getViewID() {
		return viewInfo.getID();
	}
	
	/**
	 * Sets the ViewInfo about the represented view for this element.
	 * 
	 * @param info
	 * 		ViewInfo about the represented view for this element.
	 */
	public void setViewInfo(ViewInfo info) {
		this.viewInfo = info;
	}

	/**
	 * Returns the icon for the view element.
	 * 
	 * @return
	 * 		icon for the view element 
	 */
	@Override
	public ImageIcon getIcon() {
		return viewInfo.getIcon();
	}
	
	/**
	 * Returns the project associated with this view
	 * 
	 * @return
	 * 		the project associated with this view
	 */
	@Override
	public ProjectElement getProject() {
		return (ProjectElement) getParent().getParent();
	}

}
