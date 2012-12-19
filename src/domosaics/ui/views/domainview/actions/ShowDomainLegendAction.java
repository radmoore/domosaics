package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domainlegend.DomainLegendView;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;


/**
 * Action which opens the domain legend.
 * 
 * @author Andreas Held
 *
 */
public class ShowDomainLegendAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = ViewHandler.getInstance().getActiveView(); 
		DomainLegendView view = ViewHandler.getInstance().createTool(ViewType.DOMAINLEGEND);
		view.setDomainView(domView);
		ViewHandler.getInstance().addTool(view);
	}
	

}