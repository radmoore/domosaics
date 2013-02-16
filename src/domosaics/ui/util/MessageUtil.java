package domosaics.ui.util;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import domosaics.ui.DoMosaicsUI;




/**
 * An utility class defining methods which make it possible 
 * to show basic warning or an informative message dialogs from any point 
 * within DoMosaicS.
 * 
 * @author Andreas Held
 *
 */
public class MessageUtil {
	
	private static final String DOMOSAICS = "DoMosaicS";
	private static final String ERRORTITLE = "DoMosaicS - Error";
	private static final String INFORMTITLE = "DoMosaicS - Information";
	
	/**
     * Shows a basic error message dialog with a specified message.
     * 
     * @param msg
     * 		the error message to display
     */
    public static void showWarning(String msg) {
    	showWarning(DoMosaicsUI.getInstance(), msg);
    }
    
    /**
     * Shows a basic error message dialog with a specified message.
     * 
     * @param parent
     * 		the component invoking the error message
     * @param msg
     * 		the error message to display
     */
    public static void showWarning(Component parent, String msg) {
    	JOptionPane.showMessageDialog(parent, msg, ERRORTITLE, JOptionPane.ERROR_MESSAGE); 
    }
    
    /** 
     * Shows a generic information dialog with the specified message
     * @param msg
     */
    public static void showInformation(JFrame parent, String msg) {
    	if (parent == null)
    		parent = DoMosaicsUI.getInstance();
    	JOptionPane.showMessageDialog(parent, msg, INFORMTITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows a basic dialog containing a message which can be answered with
     * yes or no.
     * 
     * @param msg
     * 		the question to ask
     * @return
     * 		no (false) or yes (true)
     */
    public static boolean showDialog(String msg) {
    	Object[] options = {"Yes", "No"};
    	int choice = JOptionPane.showOptionDialog(DoMosaicsUI.getInstance(), msg, DOMOSAICS, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    	if (choice == JOptionPane.NO_OPTION)
    		return false;
    	return true;
    }
    
    /**
     * Shows a basic dialog containing a message which can be answered with
     * yes or no. This dialog will always be on top of all other
     * GUI components.
     * 
     * @param msg
     * 		the question to ask
     * @return
     * 		no (false) or yes (true)
     */
    public static boolean showDialog(Component parent, String msg) {
    	Object[] options = {"Yes", "No"};
    	int choice = JOptionPane.showOptionDialog(parent, msg, DOMOSAICS, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    	if (choice == JOptionPane.NO_OPTION)
    		return false;
    	return true;
    }
    
    
    /**
     * A dialog which gives the user three or more choices, for instance "Overwrite", 
     * "Rename" and "Cancel". The question (message) as well as the choices
     * are free configurable.
     * 
     * @param msg
     * 		the question the user has to answer
     * @param options
     * 		three or more options for answering the question
     * @return
     * 		the users choice corresponding to order of given choices as integer
     */
    public static int show3ChoiceDialog(String msg, Object[] options) {
    	return JOptionPane.showOptionDialog(DoMosaicsUI.getInstance(), msg, DOMOSAICS, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    }
    
}
