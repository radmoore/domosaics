package domosaics.ui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import domosaics.model.configuration.Configuration;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.configuration.ConfigurationFrame;




/**
 * Opens the configuration frame to specify lookup adresses
 * 
 * @author Andreas Held
 *
 */
public class ShowConfigurationAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	protected ConfigurationFrame configFrame = null;
	
	public void actionPerformed(ActionEvent e) {
			
		configFrame = Configuration.getInstance().getFrame();
		
		if (configFrame == null)
			configFrame = new ConfigurationFrame();
		
		else if (configFrame.getExtendedState() == Frame.ICONIFIED)
			configFrame.setExtendedState(Frame.NORMAL);
		
		else
			configFrame.setVisible(true);
		
//		if (!Configuration.getInstance().isVisible()) {
//			configFrame = new ConfigurationFrame();
//		}
//		/**
//		* TODO
//		* the bug with the configuration window not opening
//		* after it was canceled once is here. For now,
//		* we only catch a minimized window (as the config window
//		* is always on top if maximized). If the extended state is not
//		* availabe, or the configFrame is not minimized,
//		* we will just kill the instance and create a new one.
//		**/
//		else {
//			if (configFrame.getExtendedState() == Frame.ICONIFIED)
//				configFrame.setExtendedState(Frame.NORMAL);
//
//			else
//				configFrame.dispose(); // just want to be sure that there is no instance around anywhere
//				configFrame = new ConfigurationFrame();
//		}
	}

}