package domosaics.ui.tools.domaingraph.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domaingraph.DomainGraphView;
import domosaics.ui.tools.domaingraph.components.GraphLayoutManager;
import domosaics.ui.views.ViewType;




/**
 * Action which triggers the domain rendering to 
 * their shapes used within the backend domain view
 * 
 * @author Andreas Held
 *
 */
public class DrawDomainShapesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DomainGraphView view = ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		
		view.getGraphLayoutManager().setToDomainShapes();
		view.getPrefuseGraph().setRenderer(GraphLayoutManager.DOMAINSHAPE_RENDERER);
	}

}
