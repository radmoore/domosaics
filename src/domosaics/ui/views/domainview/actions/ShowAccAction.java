package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.model.configuration.Configuration;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.view.components.ZoomCompatible;


public class ShowAccAction extends AbstractMenuAction implements ZoomCompatible{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainLayoutManager().changeNameOrAccView();
		view.getDomainLayoutManager().firevisualChange();
	}

}