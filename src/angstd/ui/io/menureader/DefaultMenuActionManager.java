package angstd.ui.io.menureader;

import java.util.HashMap;
import java.util.Map;

/**
 * Class DefaultMenuActionManager implements {@link MenuActionManager} and
 * defines all needed methods to add and retrieve loaded actions.
 * <br>
 * Actions are defined in a formatted menu file and parsed into 
 * AbstractMenuActions, which are after creation automatically stored in 
 * the action manager.
 * <br>
 * To get access to this actions the getMethod can be used, for instance as 
 * followed: <br>
 * if (manager.getAction(ShowEdgeLabelsAction.class) != null) <br>
 *    // do something <br>
 * 
 * @author Andreas Held
 *
 */
public class DefaultMenuActionManager implements MenuActionManager{

	/** a HashMap mapping the action classes to instanciated action objects	 */
	protected Map<Class<?>, AbstractMenuAction> actions;
	
	/**
	 * Constructor for a DefaultMenuActionManager
	 */
	public DefaultMenuActionManager() {
		actions = new HashMap<Class<?>, AbstractMenuAction>();
	}
	
	/**
	 * Adds an action to the manager defined within a xml formatted menu file.
	 * 
	 * @param clazz
	 * 		the url to the file, e.g. "main.ui.actions.ImportDataAction"
	 * @param action
	 * 		the automatically created action
	 */
	public void addAction(Class<?> clazz, AbstractMenuAction action) {
		actions.put(clazz, action);
	}
	
	/**
	 * Gives access to all loaded actions.
	 * 
	 * @param clazz
	 * 		the url to the file, e.g. "main.ui.actions.ImportDataAction"
	 * @return
	 * 		the instanciated AbstractMenuAction for the class
	 */	
	public AbstractMenuAction getAction (Class<?> clazz) {
		return actions.get(clazz);
	}
	
}
