package angstd.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import angstd.model.configuration.Configuration;



/**
 * The HelpManager manages the showing of advice dialogs, which can be 
 * disabled within the configurations menu. To ensure that each advice is 
 * only shown once, this class keeps track about the already used
 * help messages in form of a hash map.
 * 
 * @author Andreas Held
 *
 */
public class HelpManager {

	/** title displayed in the dialogs frame border */
	private static final String HELP = "Angstd - Advice";
	
	/** track messages which were already shown */
	private static Map<String, String> tracker = new HashMap<String, String>();
	
	
    /**
     * Shows a help dialog with a given message.
     * 
     * @param msg
     * 		the message being displayed as advice
     */
    public static void showHelpDialog(String key, String msg) {
    	if (!Configuration.getInstance().isShowAdvices())
    		return;
    	
    	if (tracker.get(key) != null)
    		return;
    	
    	tracker.put(key, msg);
    	JOptionPane.showMessageDialog(AngstdUI.getInstance(), msg, HELP, JOptionPane.INFORMATION_MESSAGE);
    }
	
}

/**
 * @TODO implement a help dialog which is better looking.
 * 
 * @author Andreas Held
 *
 */
class HelpDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
}
