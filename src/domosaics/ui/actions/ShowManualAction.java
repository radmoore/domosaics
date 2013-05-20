package domosaics.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.util.BrowserLauncher;




/**
 * Opens the DoMosaics Documentation
 * 
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ShowManualAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
				
		if ( Configuration.getInstance().getDocuPath(false) == "" ) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "No help file sepecified - please check the settings.");
		}
		else {
			File helpFile = new File(Configuration.getInstance().getDocuPath(false));
			if ( (!helpFile.exists()) )
				MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Sepecified help file does not exist - please check the settings.");
			else
				BrowserLauncher.openURL(Configuration.getInstance().getDocuPath(true));
		}
	}
}
