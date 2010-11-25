package angstd.ui.io.menureader;

import java.net.URL;

import javax.swing.JMenuBar;

/**
 * Class JMenuBarFactory uses {@link MenuReader} to parse a xml 
 * formatted menu file and by invoking 
 * {@link MenuEntry#create(MenuActionManager)} it makes
 * the menu bar usable as JMenuBar. 
 * <p>
 * To set up the menuBar in a main frame its enough to call: <br>
 * setJMenuBar(new JMenuBarFactory().createMenuBar(String fileURL, 
 * MenuActionManager manager)); <br>
 * Where MenuActionManager can be an instance of {@link DefaultMenuActionManager} to
 * hold the defined actions and make them accessible in other program contexts.
 * 
 * @author Andreas Held
 *
 */
public class JMenuBarFactory {

	/**
	 * creates an instanciated JMenuBar from a file path.
	 * 
	 * @param fileURL
	 * 		file containing the menu xml
	 * @param actionManager
	 * 		manager which stores all actions for the menu items
	 * @return
	 * 		the instanciated JMenuBar
	 */
	public static JMenuBar createMenuBar(String fileURL, MenuActionManager actionManager) throws MenuParsingException, CreateActionException{
		MenuEntry menuBar = new MenuReader().getMenuFromFile(fileURL);
		return (JMenuBar) menuBar.create(actionManager);
	}
	
	/**
	 * creates an instanciated JMenuBar from a file URL.
	 * 
	 * @param fileURL
	 * 		url to the menu xml
	 * @param actionManager
	 * 		manager which stores all actions for the menu items
	 * @return
	 * 		the instanciated JMenuBar
	 */
	public static JMenuBar createMenuBar(URL fileURL, MenuActionManager actionManager) throws MenuParsingException, CreateActionException{
		if (fileURL == null)
			return null;
		
		MenuEntry menuBar = new MenuReader().getMenuFromURL(fileURL);
		return (JMenuBar) menuBar.create(actionManager);
	}
}
