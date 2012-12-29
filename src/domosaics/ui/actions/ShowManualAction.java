package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;




/**
 * Opens the DoMosaics Documentation website
 * 
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ShowManualAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	private static final String DOMOSDOCS = "http://domosdocs.uni-muenster.de";

	public void actionPerformed(ActionEvent e) {

        if( !java.awt.Desktop.isDesktopSupported() ) {
        	MessageUtil.showWarning("The website could not be opened. Navigate to "+ShowManualAction.DOMOSDOCS+ "manually.");
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
        	MessageUtil.showWarning("The website could not be opened. Navigate to "+ShowManualAction.DOMOSDOCS+ "manually.");
        }
        try {
        	java.net.URI uri = new java.net.URI( ShowManualAction.DOMOSDOCS );
            desktop.browse( uri );
        }
        catch ( Exception excp ) {
        	System.err.println( excp.getMessage() );
         }
    }

}
