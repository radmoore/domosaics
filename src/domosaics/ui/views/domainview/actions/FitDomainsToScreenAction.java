package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.DomainViewI;




/**
 * Class which fits all domains into the screen size.
 * 
 * @author Andreas Held
 *
 */
public class FitDomainsToScreenAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainLayoutManager().fireStructuralChange();
	}
}