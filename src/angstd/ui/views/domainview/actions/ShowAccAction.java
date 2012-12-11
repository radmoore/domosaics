package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.model.configuration.Configuration;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.view.components.ZoomCompatible;

public class ShowAccAction extends AbstractMenuAction implements ZoomCompatible{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainLayoutManager().changeNameOrAccView();
		view.getDomainLayoutManager().firevisualChange();
	}

}