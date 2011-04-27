package angstd.ui.io.menureader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Stack;

import javax.swing.JMenuBar;

import angstd.model.configuration.Configuration;

/**
 * Class MenuReader defines the methods to load a {@link JMenuBar} 
 * from a XML formatted file.
 * <p>
 * The Parser is used within the {@link JMenuBarFactory}.
 * <p>
 * To extend the Menu Reader by new menu entry types, just add: <br>
 * if (type.toUpperCase().equals("YOURTYPE")) <br>
 * 		menuItem = new MenuEntry(MenuEntryType.YOURTYPE); <br>
 * at the specified position within {@link #getMenuFromFile(String)}.
 * 
 * @author Andreas Held
 *
 */
public class MenuReader {
	
	/**
	 * Creates the input stream for the xml formatted menu file 
	 * from a file url
	 * 
	 * @param fileURL
	 * 		file url
	 * @return
	 * 		JMenuBar as MenuEntry
	 * @throws MenuParsingException
	 * 		exception which can occur during menu parsing
	 */
	public MenuEntry getMenuFromURL(URL fileURL) throws MenuParsingException {
		if (fileURL == null) // the tool has no menu
			return null;
		
		try {
			InputStream stream = fileURL.openStream();
			return getMenu(stream);
		} catch (IOException e) {
			Configuration.getLogger().debug(e.toString());
		}
		return null;
	}
	
	/**
	 * Creates the input stream  for the xml formatted menu file from a 
	 * file path
	 * 
	 * @param fileURL
	 * 		file path
	 * @return
	 * 		JMenuBar as MenuEntry
	 * @throws MenuParsingException
	 * 		exception which can occur during menu parsing
	 */
	public MenuEntry getMenuFromFile(String fileURL) throws MenuParsingException {
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileURL);
		return getMenu(stream);
	}

	/**
	 * Parser method which parses a XML-formatted file which holds a 
	 * menu structure for a JMenuBar, which can be created using 
	 * {@link MenuEntry#create(MenuActionManager)}
	 * 
	 * @param stream
	 * 		The input stream for the xml file
	 * @return
	 * 		The JMenuBar as MenuEntry
	 * @throws MenuParsingException
	 * 		exception which can occur during menu parsing
	 */
	public MenuEntry getMenu(InputStream stream) throws MenuParsingException {
		try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(stream));	
	        
			String line;
			
			MenuEntry menuBar = null;
			Stack<MenuEntry> menuStack = new Stack<MenuEntry>();
			MenuEntry menu = null;
			MenuEntry menuItem = null;
			int depth = 0; // depth within the menu structure (e.g. sub menus)
			
			while((line = in.readLine()) != null) {	
				// skip comments
				if (line.contains("<!--"))
					continue;
				
				// create new MenuBar
				if (line.toUpperCase().contains("<MENUBAR>")) {
					menuBar = new MenuEntry(MenuEntryType.JMENUBAR);
					continue;
				}
				
				//  stop reading file
				if (line.toUpperCase().contains("</MENUBAR>")) {
					break;
				}	
				
				// create new Menu respectively sub menu object 
				// depending on the depth within the menu structure
				if (line.toUpperCase().contains("<MENU ")) {
					if (depth > 0) 
						menuStack.push(menu);

					menu = new MenuEntry(MenuEntryType.JMENU);
					menu.setName(getID(line));
					depth++;
					continue;
				}
				
				// store actual menu into the menu or menu bar 
				// depending on the depth within the menu structure
				if (line.toUpperCase().contains("</MENU>")) {
					if (depth > 1) {
						MenuEntry submenue = menu;
						menu = menuStack.pop();
						menu.addEntry(submenue);
					} else
						menuBar.addEntry(menu);
					depth--;
					continue;
				}
				
				// store actual MenuItem into the actual menu
				if (line.toUpperCase().contains("</MENUITEM>")) {
					menu.addEntry(menuItem);
					continue;
				}
			
				// create new MenuItem object 
				// NOTE For developers: add new types here!
				if (line.toUpperCase().contains("<MENUITEM ")) {
					String type = getID(line);
					if (type.toUpperCase().equals("JMENUITEM"))
						menuItem = new MenuEntry(MenuEntryType.JMENUITEM);
					else if (type.toUpperCase().equals("JCHECKBOX"))
						menuItem = new MenuEntry(MenuEntryType.JCHECKBOX);
					else if (type.toUpperCase().equals("JSEPARATOR")) {
						menuItem = new MenuEntry(MenuEntryType.JSEPARATOR);
						menu.addEntry(menuItem);
					}
					else
						menuItem = null;
					continue;
				}	
				
				// set a parameter for a menu item
				if (line.toUpperCase().contains("<PARAMETER ")) {
					
					// name parameter
					if (getID(line).toUpperCase().equals("NAME")) {
						menuItem.setName(getValue(line));
						continue;
					}
					
					// action class parameter
					if (getID(line).toUpperCase().equals("ACTIONCLASS")) {
						menuItem.setActionclassURL(getValue(line));
						continue;
					}
					
					// tooltip parameter
					if (getID(line).toUpperCase().equals("TOOLTIP")) {
						menuItem.setTooltip(getValue(line));
						continue;
					}
					
					// icon URL parameter
					if (getID(line).toUpperCase().equals("ICON")) {
						menuItem.setIcon(getValue(line));
						continue;
					}
					
					// Mnemonic parameter
					if (getID(line).toUpperCase().equals("MNEMONIC")) {
						menuItem.setMnemonic(getValue(line));
						continue;
					}
					
					// isSelected parameter
					if (getID(line).toUpperCase().equals("ISSELECTED")) {
						menuItem.setSelected(getValue(line));
						continue;
					}
				}
			}
	
			in.close();	
			stream.close();
			return menuBar;	
		} catch (FileNotFoundException fnfe) {
			throw new MenuParsingException("Menu file not found");
		}catch (IOException ioe) {
			throw new MenuParsingException("Menu couldnt be parsed - I/O exception");
		}
	}
	
	/**
	 * Extracts an ID of an item.
	 * 
	 * @param str 
	 * 		the line where the id has to be extracted
	 * @return 
	 * 		the extracted id of a item
	 */
	protected static String getID(String str) {
		int startPos = str.indexOf("id=\"")+4;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
	
	/**
	 * Extracts an Value of an parameter.
	 * 
	 * @param str 
	 * 		the line where the value has to be extracted
	 * @return 
	 * 		the extracted value of a parameter
	 */
	protected static String getValue(String str) {
		int startPos = str.indexOf("value=\"")+7;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
	
	
}
