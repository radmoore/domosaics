package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.localservices.hmmer3.ui.Hmmer3Frame;
import domosaics.ui.io.menureader.AbstractMenuAction;




/**
 * Method triggering the hmmscan panel
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class Hmmer3Action extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	
	protected Hmmer3Frame hmmer3 = null;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		hmmer3 = Hmmer3Frame.getFrame();
	}
	
}
