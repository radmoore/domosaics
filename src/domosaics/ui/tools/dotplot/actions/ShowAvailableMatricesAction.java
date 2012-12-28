package domosaics.ui.tools.dotplot.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.util.BrowserLauncher;




/**
 * Action which opens a brwoser showing the available 
 * substitution matrices
 * 
 * @author Andreas Held
 *
 */
public class ShowAvailableMatricesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		BrowserLauncher.openURL("ftp://ftp.ncbi.nih.gov/blast/matrices/");
	}


}
