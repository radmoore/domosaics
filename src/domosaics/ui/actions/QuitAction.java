package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.ApplicationHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;


/**
 * Closes the application
 * 
 * @author Andreas Held
 *
 */
public class QuitAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		ApplicationHandler.getInstance().end();
	}
}
