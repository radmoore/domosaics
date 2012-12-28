package domosaics.ui.io.menureader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * Class MenuEntry represents a JComponent within a JMenu. 
 * This could for example be a JMenuBar, Menu, MenuItem, JSeparator or 
 * JCheckboxItem. For a list of all supported menu item types see enumeration
 * class {@link MenuEntryType}.
 * <p>
 * A MenuEntry can hold a name, shortcut, tooltip, image and action.
 * <p>
 * To add new types of menu entry do the following: <br>
 * 1. add the new type to {@link MenuEntryType}. <br>
 * 2. Extend the {@link #create(MenuActionManager)} method, so that the correct JComponent 
 *    for the new type is returned. <br>
 * 3. Use the new type in the menu XML format <br>
 * 4. Add an entry for the new type in the menu parser {@link MenuReader} <br>
 * 
 * @author Andreas Held
 *
 */
public class MenuEntry {

	/** Type of the MenuEntry, e.g. JMenuItem, JMenu, JMenuBar, ... */
	private MenuEntryType type;
	
	/** sub menu items, only used for menus */
	private List<MenuEntry> items;
	
	/** the name of the menu component */
	private String name;
	
	/** the icon URL to an image which is used for representing this component */
	private String iconURL;
	
	/** the shortcut defined for this component */
	private String mnemonic;
	
	/** the tooltip message defined for this component */
	private String tooltip;
	
	/** the action class URL defined for this component */
	private String actionClassURL;
	
	/** the created action based on the actionClassURL */
	private AbstractMenuAction action;
	
	/** flag whether or not the JMenuItem is selected, e.g. for CheckBoxes */
	private boolean isSelected;
	
	
	/**
	 * Constructor for a new MenuEntry object. 
	 */
	public MenuEntry(MenuEntryType type) {
		this.type = type;
		this.action = null;
		
		// if the type can hold sub menues the list items must be initialized
		switch (type) {
			case JMENUBAR: items = new ArrayList<MenuEntry>(); break;
			case JMENU: items = new ArrayList<MenuEntry>(); break;
		}
	}
	
	
	/* ******************************************************************* *
	 *   						 creating JMenu objects					   *
	 * ******************************************************************* */
	
	/**
	 * Creates a JComponent which can be used as MenuItem based on
	 * the specified type of the menu entry.
	 * 
	 * @return 
	 * 		Component to add in a JMenu or JMenuBar
	 */
	public JComponent create(MenuActionManager manager) throws CreateActionException {
		switch (type) {
			case JSEPARATOR: return createJSeparator();
			case JMENUITEM: return createJMenuItem(manager);
			case JCHECKBOX: return createJCheckBoxMenuItem(manager);
			case JMENU: return createJMenu(manager);
			case JMENUBAR: return createJMenuBar(manager);
			default: return new JMenuItem();
		}	
	}
	
	/**
	 * Creates a new JSeparator menu item
	 * 
	 * @return
	 * 		a new JSeparator
	 */
	private JSeparator createJSeparator() {
		return new JSeparator();
	}
	
	/**
	 * Creates a new JMenuItem menu item
	 * 
	 * @return 
	 * 		a new JMenuItem
	 */
	private JMenuItem createJMenuItem (MenuActionManager manager) throws CreateActionException {
		JMenuItem res = new JMenuItem(name);
		if (actionClassURL != null && !actionClassURL.isEmpty()) {
			action = createAction(manager);
			if (action != null) 
				res.setAction(action);
		}
		return res;
	}
	
	/**
	 * Creates a new JCheckBoxMenuItem menu item
	 * 
	 * @return 
	 * 		a new JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem createJCheckBoxMenuItem (MenuActionManager manager) throws CreateActionException {
		JCheckBoxMenuItem res = new JCheckBoxMenuItem(name);
		if (actionClassURL != null && !actionClassURL.isEmpty()) {
			action = createAction(manager);
			if (action != null) 
				res.setAction(action);
		}
		return res;
	}
	
	/**
	 * Creates a new JMenu menu item
	 * 
	 * @return 
	 * 		a new JMenu
	 */
	private JMenu createJMenu(MenuActionManager manager) throws CreateActionException{
		JMenu res = new JMenu(name);
		Iterator<MenuEntry> iter = items.iterator();
		while (iter.hasNext()) 
			res.add(iter.next().create(manager));
		return res;
	}
	
	/**
	 * Creates a new JMenuBar menu item
	 * 
	 * @return 
	 * 		a new JMenuBar
	 */
	private JMenuBar createJMenuBar(MenuActionManager manager) throws CreateActionException {
		JMenuBar res = new JMenuBar();
		Iterator<MenuEntry> iter = items.iterator();
		while (iter.hasNext()) 
			res.add(iter.next().create(manager));
		return res;
	}
	
	/**
	 * Tries to create a {@link AbstractMenuAction} from the action class URL.
	 * 
	 * @return 
	 * 		the created action object
	 */
	private AbstractMenuAction createAction(MenuActionManager manager) throws CreateActionException {
		try {
			Class<?> actionClass = Class.forName(actionClassURL);
			AbstractMenuAction action = (AbstractMenuAction) actionClass.newInstance();
			action.setIcon(iconURL);
			action.setName(name);
			action.setDescription(tooltip);
			action.setState(isSelected);
			action.setAccelerator(KeyStroke.getKeyStroke(mnemonic));
			manager.addAction(actionClass, action);
			return action;
		} catch (ClassCastException cce) {
			throw new CreateActionException("Class cast exception: Actionclass "+ actionClassURL +" could not be casted");
		} catch (ClassNotFoundException cnfe) {
			throw new CreateActionException("Actionclass "+ actionClassURL +" could not be found");
		} catch (IllegalAccessException iae) {
			throw new CreateActionException("Not able to access action class: "+actionClassURL);
		} catch (InstantiationException ie) {
			throw new CreateActionException("Not able to instantiate action class: "+actionClassURL);
		}
	}

	
	/* ******************************************************************* *
	 *   							 Set methods						   *
	 * ******************************************************************* */
	
	/**
	 * sets the name for this component
	 */
	public void setName (String name) {
		this.name = name;
	}
	
	/**
	 * sets the icon URL for this component
	 */
	public void setIcon (String iconURL) {
		this.iconURL = iconURL;
	}

	/**
	 * sets the tooltip for this component
	 */
	public void setTooltip (String tooltip) {
		this.tooltip = tooltip;
	}
	
	/**
	 * sets the shortcut for this component
	 */
	public void setMnemonic (String mnemonic) {
		this.mnemonic = mnemonic;
	}
	
	/**
	 * sets the action class URL for this component
	 */
	public void setActionclassURL(String actionClassURL) {
		this.actionClassURL = actionClassURL;
	}
	
	/**
	 * adds a new MenuEntry to this component
	 */
	public void addEntry (MenuEntry entry) {
		items.add(entry);
	}
	
	/**
	 * Sets the Selection flag for CheckBox items
	 * 
	 * @param flag 
	 * 		flag indicating whether or not the check box button is enabled
	 */
	public void setSelected(String flag) {
		if(flag.toUpperCase().equals("TRUE"))
			isSelected = true;
		else
			isSelected = false;

	}
	
}
