package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domaingraph.DomainGraphView;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;




/**
 * Action which opens the domain co occurrence graph.
 * 
 * @author Andreas Held
 *
 */
public class ShowDomainGraphAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = ViewHandler.getInstance().getActiveView();
		DomainGraphView view = ViewHandler.getInstance().createTool(ViewType.DOMAINGRAPH);
		view.setDomainView(domView);
		ViewHandler.getInstance().addTool(view);
	}

}
