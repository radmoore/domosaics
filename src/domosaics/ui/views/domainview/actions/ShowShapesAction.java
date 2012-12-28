package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.view.components.ZoomCompatible;


/**
 * Changes the drawing of domains into shape rendering. This action is
 * only allowed within the proportional view.
 * 
 * @author Andreas Held
 *
 */
public class ShowShapesAction extends AbstractMenuAction implements ZoomCompatible{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainLayoutManager().fireStructuralChange();
	}
	
}