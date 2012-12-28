package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.DomainViewI;




/**
 * Action which resets all shift positions which were made by the user.
 * 
 * @author Andreas Held
 *
 */
public class ResetShiftAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		view.getDomainShiftManager().reset();
	}

}
