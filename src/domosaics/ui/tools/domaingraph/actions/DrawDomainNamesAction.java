package domosaics.ui.tools.domaingraph.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domaingraph.DomainGraphView;
import domosaics.ui.tools.domaingraph.components.GraphLayoutManager;
import domosaics.ui.views.ViewType;


/**
 * Action which triggers the domain rendering to 
 * circular shape rendering inclusing the domain names
 * 
 * @author Andreas Held
 *
 */
public class DrawDomainNamesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainGraphView view = ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		
		view.getGraphLayoutManager().setToNames();
		view.getPrefuseGraph().setRenderer(GraphLayoutManager.LABEL_RENDERER);
	}


}
