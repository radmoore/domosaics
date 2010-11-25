package angstd.ui.views.treeview.manager;

import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.view.manager.DefaultFontManager;
import angstd.ui.views.view.manager.ViewManager;

/**
 * A class creating all needed {@link ViewManager} which are needed
 * to provide the complete manager driven functionality for a TreeView.
 * <p>
 * When initializing the view, it will iterate over all enumeration entries
 * defined in this factory and creates an instance to it. <br>
 * Therefore to add additional manager to the DomainView you just need
 * to make two entries in this factory and add the getMethod to the TreeView.
 * 
 * @author Andreas Held
 *
 */
public class TreeViewManagerFactory {

	/**
	 * Enumeration of all manager which should be added to the view
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum TreeViewManager {
		TREECOMPONENTMANAGER,
		TREECOLORMANAGER 	,	
		TREESELECTIONMANAGER,	
		TREESTROKEMANAGER	,	
		TREEFONTMANAGER		,	
	};
	
	/**
	 * Method called to create an instance of a specific manager.
	 * 
	 * @param type
	 * 		the managers type
	 * @param view
	 * 		the view to be managed
	 * @return
	 * 		instance of the specified manager
	 */
	public static ViewManager create(TreeViewManager type, TreeViewI view) {
		switch (type) {
			case TREECOMPONENTMANAGER: 		return new TreeComponentManager();
			case TREECOLORMANAGER: 			return new TreeColorManager();
			case TREESELECTIONMANAGER: 		return new TreeSelectionManager(view);
			case TREESTROKEMANAGER: 		return new TreeStrokeManager();
			case TREEFONTMANAGER: 			return new DefaultFontManager<NodeComponent>();
		}
		return null;
	}
}
