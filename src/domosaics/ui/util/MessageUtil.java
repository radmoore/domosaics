package domosaics.ui.util;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import domosaics.DoMosaics;
import domosaics.ui.DoMosaicsUI;




/**
 * An utility class defining methods which make it possible 
 * to show basic warning or an informative message dialogs from any point 
 * within DoMosaics.
 * 
 * @author Andreas Held
 *
 */
public class MessageUtil {
	
	private static final String DOMOSAICS = "DoMosaics";
	private static final String ERRORTITLE = "DoMosaics - Error";
	private static final String INFORMTITLE = "DoMosaics - Information";
	
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
     * 		If no specific parent, DoMosaicsUI.getInstance()
     *      should be pass as the parameter value
     * @param msg
     * 		the error message to display
     */
    public static void showWarning(JFrame parent, String msg) {
    	boolean stackingIssue=false;
    	if(parent==null)
    		parent=DoMosaicsUI.getInstance();
		if(parent.isAlwaysOnTop()) {
			stackingIssue=true;
			parent.setAlwaysOnTop(false);
		}
 		JOptionPane.showMessageDialog(parent, msg, ERRORTITLE, JOptionPane.ERROR_MESSAGE);
 		if(stackingIssue)
			parent.setAlwaysOnTop(true);
    }
    
    /** 
     * Shows a generic information dialog with the specified message
     * @param parent
     * 		the component invoking the error message
     * 		If no specific parent, DoMosaicsUI.getInstance()
     *      should be pass as the parameter value
     * @param msg
     * 		the information message to display
     */
    public static void showInformation(JFrame parent, String msg) {
    	boolean stackingIssue=false;
    	if(parent==null)
    		parent=DoMosaicsUI.getInstance();
		if(parent.isAlwaysOnTop()) {
			stackingIssue=true;
			parent.setAlwaysOnTop(false);
		}
 		JOptionPane.showMessageDialog(parent, msg, INFORMTITLE, JOptionPane.INFORMATION_MESSAGE);
 		if(stackingIssue)
			parent.setAlwaysOnTop(true);
    }
    
    /**
     * Shows a basic dialog containing a message which can be answered with
     * yes or no. This dialog will always be on top of all other
     * GUI components.
     * @param parent
     * 		the component invoking the error message
     * 		If no specific parent, DoMosaicsUI.getInstance()
     *      should be pass as the parameter value
     * @param msg
     * 		the question to display
     * @return
     * 		no (false) or yes (true)
     */
    public static boolean showDialog(JFrame parent, String msg) {
    	boolean stackingIssue=false;
    	if(parent==null)
    		parent=DoMosaicsUI.getInstance();
		if(parent.isAlwaysOnTop()) {
			stackingIssue=true;
			parent.setAlwaysOnTop(false);
		}
 		Object[] options = {"Yes", "No"};
    	int choice = JOptionPane.showOptionDialog(parent, msg, DOMOSAICS, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    	if(stackingIssue)
			parent.setAlwaysOnTop(true);
    	if (choice == JOptionPane.NO_OPTION)
    		return false;
    	return true;
    }
    
    
    /**
     * A dialog which gives the user three or more choices, for instance "Overwrite", 
     * "Rename" and "Cancel". The question (message) as well as the choices
     * are free configurable.
     * 
     * @param parent
     * 		the component invoking the error message
     * 		If no specific parent, DoMosaicsUI.getInstance()
     *      should be pass as the parameter value
     * @param msg
     * 		the question the user has to answer
     * @param options
     * 		three or more options for answering the question
     * @return
     * 		the users choice corresponding to order of given choices as integer
     */
    public static int show3ChoiceDialog(JFrame parent, String msg, Object[] options) {
    	boolean stackingIssue=false;
    	if(parent==null)
    		parent=DoMosaicsUI.getInstance();
		if(parent.isAlwaysOnTop()) {
			stackingIssue=true;
			parent.setAlwaysOnTop(false);
		}
 		int choice = JOptionPane.showOptionDialog(parent, msg, DOMOSAICS, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    	if(stackingIssue)
			parent.setAlwaysOnTop(true);
    	if (choice == JOptionPane.NO_OPTION)
    		return choice;
    	return choice;
    }
    
}
