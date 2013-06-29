package domosaics.ui.actions;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JFrame;

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
	
	public void actionPerformed(ActionEvent e) {

		if (Configuration.getInstance().getFrame() != null) {
			Configuration.getInstance().getFrame().setState(Frame.NORMAL);
			Configuration.getInstance().getFrame().setVisible(true);
		} else {
			Configuration.getInstance().setFrame(new ConfigurationFrame());
		}
	}

}
