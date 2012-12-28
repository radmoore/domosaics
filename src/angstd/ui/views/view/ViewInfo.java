package angstd.ui.views.view;


import javax.swing.ImageIcon;

import angstd.ui.views.AngstdViewFactory;
import angstd.ui.views.ViewType;
import angstd.ui.views.view.io.ViewPropertyReader;



/**
 * Class containing all needed information to create a new view or tool.
 * The information are gathered by reading a xml-formatted property file
 * using the {@link ViewPropertyReader} within {@link AngstdViewFactory}.
 * <p>
 * This class makes attributes like the unique view id, its name, 
 * various icons and additional information available for a view.
 * <p>
 * For instance to check if a view is also a tool the view infos isTool() 
 * method would be used.
 * 
 * 
 * @author Andreas Held
 *
 */
public class ViewInfo {	

	/** the views type specified in {@link ViewType} used to start the view creation process */
	protected ViewType type;	
	
	/** the views unique id (created in {@link AngstdViewFactory}) */
	protected int id;				
	
	/** the views specific name, e.g. TreeView 1 (from properties.file) */
	protected String name;		
	
	/** the views icon used within the workspace (from properties.file) */
	protected ImageIcon defaultIcon;	
	
	/** the views associated icon (e.g. if domainview is associated with a sequence view) used within the workspace (from properties.file) */
	protected ImageIcon associatedIcon;	
	
	/** the actual used icon */
	protected ImageIcon usedIcon;	
	
	/** the workspace folder icon */
	protected ImageIcon workSpaceFolderIcon;	
	
	/** the icon used within the data import wizard */
	protected ImageIcon dataImportIcon;	
	
//	protected ImageIcon changedIcon;
//	
//	protected ImageIcon assocChangedIcon;
//	
	/** the workspace folder name */
	protected String workSpaceFolderName;	
	
	/** flag if the view is a tool and therefore is embedded in its own frame (from properties.file) */
	protected boolean isTool;	
		
	/** the toolFrame class if the view is a tool (from properties.file) */
	protected Class<?> frameClazz;
	
	/** the views action manager giving access to the available view menu actions (filled during menu creation) */
	protected ViewActionManager actionManager = new ViewActionManager(); 
	
	
	/**
	 * Compares two views based on their unique id.
	 * 
	 * @return
	 * 		true if the unique id of both views is the same
	 */
	public boolean equals(ViewInfo info) {
		return getID() == info.getID();
	}
	
	
	/* ******************************************************************* *
	 *   						SET ATTRIBUTE METHODS	 				   *
	 * ******************************************************************* */

	/**
	 * Set the action manager used to control the actions within 
	 * the view specific menu.
	 * 
	 * @param actionManager
	 * 		the action manager used during the menu creation process
	 */
	public void setViewActionManager(ViewActionManager actionManager) {
		this.actionManager = actionManager;
	}
	
	/**
	 * Set the views type to identify its type.
	 * 
	 * @param type
	 * 		the views type
	 */
	public void setViewType(ViewType type) {
		this.type = type;
	}
	
	/**
	 * Set the unique view id assigned by the AngstdViewFactory
	 * 
	 * @param id
	 * 		unique view id
	 */
	public void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Sets the view name
	 * 
	 * @param name
	 * 		the views name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Set the views default icon
	 * 
	 * @param icon
	 * 		view default icon
	 */
	public void setDefaultIcon(ImageIcon icon) {
		this.defaultIcon = icon;
	}
	
	/**
	 * changed view icon (e.g. if sequences where loaded into a domain view)
	 * 
	 * @param icon
	 * 		the non default icon (e.g. used when sequences loaded into domain vie)
	 */
	public void setAssociatedIcon(ImageIcon icon) {
		this.associatedIcon = icon;
	}
	
//	public void setChangedIcon(ImageIcon icon) {
//		this.changedIcon = icon;
//	}
//	
//	public void setAssocChangedIcon(ImageIcon icon) {
//		this.assocChangedIcon = icon;
//	}
//	
//	public ImageIcon getChangedIcon() {
//		return changedIcon;
//	}
//	
//	public ImageIcon getAssocChangedIcon() {
//		return assocChangedIcon;
//	}
	
	/**
	 * Sets the actually used icon
	 * 
	 * @param icon
	 * 		the actual icon used by the view within the workspace
	 */
	public void setUsedIcon(ImageIcon icon) {
		this.usedIcon = icon;
	}
	
	/**
	 * Return whether or not the view is also a tool
	 * 
	 * @param isTool
	 * 		 whether or not the view is also a tool
	 */
	public void setIsTool(boolean isTool) {
		this.isTool = isTool;
	}
	
	/**
	 * Sets the ToolFrame class
	 * 
	 * @param frameClazz
	 * 		the tools frame class (if isTool equals true)
	 */
	public void setFrameClazz(Class<?> frameClazz) {
		this.frameClazz = frameClazz;
	}
	
	/**
	 * Sets the workspace folder icon
	 * 
	 * @param icon
	 * 		icon used within the workspace to display the view category
	 */
	public void setWorkspaceFolderIcon(ImageIcon icon) {
		this.workSpaceFolderIcon = icon;
	}
	
	/**
	 * Set the icon used within the import data wizard
	 * 
	 * @param icon
	 * 		icon used within the import data wizard
	 */
	public void setDataImportIcon(ImageIcon icon) {
		this.dataImportIcon = icon;
	}
	
	/**
	 * Sets the workspace category folder name
	 * 
	 * @param name
	 * 		workspace category folder name
	 */
	public void setWorkspaceFolderName(String name) {
		this.workSpaceFolderName = name;
	}
	
	/* ******************************************************************* *
	 *   						GET ATTRIBUTE METHODS	 				   *
	 * ******************************************************************* */
	
	/**
	 * Return the action manager used to control the view specific menu actions
	 * 
	 * @return
	 * 		action manager used to control the view specific menu actions
	 */
	public ViewActionManager getActionManager() {
		return actionManager;
	}
	
	/**
	 * Return the views type
	 * 
	 * @return
	 * 		views type (e.g. DOMAINS)
	 */
	public ViewType getType () {
		return type;
	}
	
	/**
	 * Return the unique view id
	 * 
	 * @return
	 * 		unique view id
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Return the views name
	 * 
	 * @return
	 * 		views name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the views actually used icon within the workspace
	 * 
	 * @return
	 * 		actually used icon within the workspace
	 */
	public ImageIcon getIcon() {
		return usedIcon;
	}
	
	public ImageIcon getDefaultIcon() {
		return defaultIcon;
	}
	
	/**
	 * Return the altered icon for the workspace if something happens 
	 * to the view (e.g. sequences loaded into the Domain view).
	 * 
	 * @return
	 * 		altered icon for the workspace 
	 */
	public ImageIcon getAssociatedIcon() {
		return associatedIcon;
	}
	
	/**
	 * Return whether or not the view is also a toll
	 * 
	 * @return
	 * 		whether or not the view is also a tool
	 */
	public boolean isTool() {
		return isTool;
	}
	
	/**
	 * Return the tool frame class if the view is also a tool
	 * 
	 * @return
	 * 		tool frame class if the view is also a tool
	 */
	public Class<?> getFrameClazz() {
		return frameClazz;
	}
	
	/**
	 * Return workspace folder icon
	 * 
	 * @return
	 * 		 workspace folder icon
	 */
	public ImageIcon getWorkspaceFolderIcon() {
		return workSpaceFolderIcon;
	}
	
	/**
	 * Return icon used within the data import wizard
	 * 
	 * @return
	 * 		icon used within the data import wizard
	 */
	public ImageIcon getDataImportIcon() {
		return dataImportIcon;
	}
	
	/**
	 * Return workspace folder name
	 * 
	 * @return
	 * 		workspace folder name
	 */
	public String getWorkspaceFolderName() {
		return workSpaceFolderName;
	}

}
