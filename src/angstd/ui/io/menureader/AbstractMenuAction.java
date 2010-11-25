package angstd.ui.io.menureader;

import java.awt.event.ActionEvent;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


/**
 * The abstract class AbstractMenuAction makes it possible to set all 
 * Action specific settings for {@link MenuEntry}s.
 * <br>
 * In other words a MenuEntry like a JMenuItem is based on a defined
 * Action, which holds the settings for its name, shortcut, image, tooltip, ...
 * <br>
 * To trigger specific methods just implement the {@link #actionPerformed(ActionEvent)}
 * method in a subclass of AbstractMenuAction and use the path to this subclass
 * within the xml formatted menu file, as action url. <br>
 * Example: "main.ui.actions.ImportDataAction" <br>
 * 
 * @author Andreas Held
 *
 */
public abstract class AbstractMenuAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The user defined method which will be triggered after activating
	 * the menu item.
	 */
	public abstract void actionPerformed(ActionEvent e);
	
	/**
	 * sets the name for the action / menu item
	 * 
	 * @param name
	 * 		name of the action
	 */
	public void setName(String name) {
		if (name != null)
			putValue(Action.NAME, name);
	}
	
	/**
	 * sets the icon for the action / menu item
	 * 
	 * @param iconUrl
	 * 		icon url to the icon which should be loaded for the action
	 */
	public void setIcon(String iconUrl) {
		if (iconUrl != null) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(iconUrl);
			try {
				ImageIcon icon = new ImageIcon(ImageIO.read(is));
				putValue(Action.SMALL_ICON, icon);
				is.close();
			} catch(Exception e) {
				
			}
		}
	}
	
	/**
	 * sets the tooltip text for the action / menu item
	 * 
	 * @param description
	 * 		 tooltip text for the action
	 */
	public void setDescription(String description) {
		if (description != null)
			putValue(Action.SHORT_DESCRIPTION, description);
	}
	
	/**
	 * sets the accelerator shortcut for the action / menu item
	 * 
	 * @param accelerator
	 * 		accelerator shortcut for the action
	 */
	public void setAccelerator(KeyStroke accelerator) {
		if (accelerator != null)
			putValue(Action.ACCELERATOR_KEY, accelerator);
	}

	/**
	 * sets the shortcut for the action / menu item
	 * 
	 * @param mnemonic
	 * 		shortcut for the action
	 */
	public void setMnemonic(KeyStroke mnemonic) {
		if (mnemonic != null)
			putValue(Action.MNEMONIC_KEY, mnemonic);
	}
	
	/**
	 * sets the inital state for the action / menu item
	 * 
	 * @param state
	 * 		state for the action 
	 */
	public void setState(boolean state) {
		putValue(Action.SELECTED_KEY, state);
	}

	/**
	 * Return the actions state
	 * 
	 * @return
	 * 		actions state
	 */
	public boolean getState() {
		return (Boolean) getValue(Action.SELECTED_KEY);
	}

}
