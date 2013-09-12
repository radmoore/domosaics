package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.model.configuration.Configuration;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domainlegend.DomainLegendView;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.view.components.ZoomCompatible;




public class ShowNameAction extends AbstractMenuAction implements ZoomCompatible{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainLayoutManager().changeNameOrAccView();
		view.getDomainLayoutManager().firevisualChange();
		
		// reload legend if it is visible 
		DomainLegendView legendView = ViewHandler.getInstance().getTool(ViewType.DOMAINLEGEND);		
		if ( legendView.isShowing() ) {
			ViewHandler.getInstance().removeTool(legendView);
			legendView = ViewHandler.getInstance().createTool(ViewType.DOMAINLEGEND);
			legendView.setDomainView(view);
			ViewHandler.getInstance().addTool(legendView);
		}
		
	}

}