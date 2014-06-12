package domosaics.ui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import domosaics.localservices.hmmer3.ui.Hmmer3Frame;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.webservices.RADS.ui.RADSFrame;
import domosaics.webservices.RADS.ui.RADSScanPanel;


/**
 * Method triggering the RADSCAN panel
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class RADSAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	
	protected Hmmer3Frame hmmer3 = null;
	
	@Override
	public void actionPerformed(ActionEvent e) {

		JFrame radsFrame = RADSScanPanel.getCurrentRADSFrame();
		if (radsFrame == null)
			radsFrame = new RADSFrame();
		radsFrame.setState(Frame.NORMAL); 
		radsFrame.setVisible(true);

	}
	
}
