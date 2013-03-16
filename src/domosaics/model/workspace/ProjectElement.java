package domosaics.model.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import domosaics.model.configuration.Configuration;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.view.ViewInfo;




/**
 * ProjectElement describes Workspace elements, representing whole projects.
 * <p>
 * In general {@link CategoryElement}s are assigned to those workspace elements
 * as children.
 * <p>
 * When a new view is added, it is checked, if the category specifying the view
 * was already added, if not its done automatically.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ProjectElement extends WorkspaceElement{

	/** path to the filled folder image */
	protected String fullIcon = "resources/folder.png";
	
	/** path to the empty folder image */
	protected String emptyIcon = "resources/folder_empty.png";
	
	
	/**
	 * Constructor for a new project element within a workspace.
	 * 
	 * @param defaultName
	 * 		initial name for the project
	 */
	public ProjectElement(String defaultName) {
		super(WorkspaceElement.PROJECT, defaultName);
	}
	
	/**
	 * Adds a Category element to the project. This is done when a view
	 * is added to the project. The correct category is determined by the 
	 * information storing object of the added view: {@link ViewInfo}.
	 * 
	 * @param info
	 * 		the information holding object for the view which is added
	 * @return
	 * 		the added Category element
	 */
	public CategoryElement addCategory(ViewInfo info) {
		return (CategoryElement) addChild(new CategoryElement(info));
	}
	
	/**
	 * Create a list containing all deleted views, which can then be 
	 * further processed by the DoMosaics internal view management.
	 * While generating this list, the workspace elements representing
	 * the category and views are deleted.
	 * 
	 * @param cat
	 * 		the category element to delete
	 * @return
	 *	 all deleted view elements
	 * 
	 */
	public ViewElement[] removeCategory(CategoryElement cat) {
		// create array with all view elements of the category
		ViewElement[] viewElem = (ViewElement[]) cat.getChildren().toArray(new ViewElement[cat.countViews()]);
		
		// check first if the category is empty, if not make it empty	
		for (int i =0; i< viewElem.length; i++) 
			cat.removeView(viewElem[i]);

		// now remove the category from the project
		children.remove(cat);
		cat.setParent(null);
		return viewElem;
	}
	
	/**
	 * Returns the category based on a {@link ViewType}, which is stored in 
	 * a {@link ViewInfo}.
	 * 
	 * @param category
	 * 		the Category for the view.
	 * @return
	 * 		the workspace element for this category.
	 */
	public CategoryElement getCategory(ViewType category) {
		for (int i = 0; i < getChildCount(); i++)
			if (((CategoryElement) getChildAt(i)).getType() == category)
				return (CategoryElement) getChildAt(i);
		return null;
	}
	
	/**
	 * Adds a view to the project, specified by {@link ViewInfo}. If no
	 * category element was added to the project specifying this type of view,
	 * the category is added.
	 * 
	 * @param viewInfo
	 * 		information about the view to be added.
	 */
	public ViewElement addView(ViewInfo viewInfo) {
		// check if category is already created
		CategoryElement catElem = getCategory(viewInfo.getType());
		if (catElem == null) 
			catElem = addCategory(viewInfo); 	// add category to project		
		
		// add view to the category
		return catElem.addView(viewInfo);
	}
	
	public boolean viewExists(String name, CategoryElement category) {
				
		if (!(category == null)) {
			for (WorkspaceElement elem : category.getViews()) {
				if (elem.getTitle().equals(name))
					return true;
			}
		}
		return false;
	}
	
	
	/**
	 * @see WorkspaceElement
	 */
	public ProjectElement getProject() {
		return this;
	}

	/**
	 * Returns the icon for the project depending on its state.
	 * 
	 * @return
	 * 		icon for the project depending on its state.
	 */
	public ImageIcon getIcon() {
		InputStream is;
		if (getChildCount() == 0)
			is  = this.getClass().getResourceAsStream(emptyIcon);
		else
			is  = this.getClass().getResourceAsStream(fullIcon);
		
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(ImageIO.read(is));
		} 
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return icon;
	}
	
	/**
	 * Returns the view element with the specified ViewInfo
	 * 
	 * @param view
	 * 		the viewInfo used to search for the view
	 * @return
	 * 		the view element found using the specified viewInfo
	 */
	public ViewElement getView(ViewInfo view) {
		for (int i = 0; i < this.getChildCount(); i++) {
			ViewElement viewElt = ((CategoryElement) getChildAt(i)).getView(view);
			if (viewElt != null)
				return viewElt;
		}
		return null;
	}
	
	
	
	public List<WorkspaceElement> getViews() {
		List<WorkspaceElement> views = new ArrayList<WorkspaceElement>();	
		for (int i = 0; i < this.getChildCount(); i++) {
			for (WorkspaceElement elem : getChildAt(i).getChildren())
				views.add(elem);
		}
		if ( views.isEmpty() )
			views = null;
		return views;
	}
	
	
//	public boolean hasSeqs() {
//	for (int i = 0; i < getChildCount(); i++)
//		if (((CategoryElement) getChildAt(i)).getType() == ViewType.SEQUENCE)
//			return true;
//	return false;
//}

//public boolean hasXdom() {
//	for (int i = 0; i < getChildCount(); i++)
//		if (((CategoryElement) getChildAt(i)).getType() == ViewType.DOMAINS)
//			return true;
//	return false;
//}

//public boolean hasTree() {
//	for (int i = 0; i < getChildCount(); i++)
//		if (((CategoryElement) getChildAt(i)).getType() == ViewType.TREE)
//			return true;
//	return false;
//}

//public boolean hasDomTree() {
//	for (int i = 0; i < getChildCount(); i++)
//		if (((CategoryElement) getChildAt(i)).getType() == ViewType.DOMAINTREE)
//			return true;
//	return false;
//}
	


}
