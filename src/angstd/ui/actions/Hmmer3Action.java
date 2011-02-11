package angstd.ui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import angstd.localservices.hmmer3.ui.Hmmer3Frame;
import angstd.ui.io.menureader.AbstractMenuAction;

/**
 * Method triggering the hmmscan panel
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class Hmmer3Action extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	
	protected Hmmer3Frame hmmer3 = null;
	
	public void actionPerformed(ActionEvent e) {
		if (hmmer3 == null || !hmmer3.isVisible())
			hmmer3 = new Hmmer3Frame();
		else 
			hmmer3.setState(Frame.NORMAL);
	}
	
}
