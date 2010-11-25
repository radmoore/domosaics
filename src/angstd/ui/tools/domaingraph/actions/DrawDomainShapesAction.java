package angstd.ui.tools.domaingraph.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.domaingraph.DomainGraphView;
import angstd.ui.tools.domaingraph.components.GraphLayoutManager;
import angstd.ui.views.ViewType;

/**
 * Action which triggers the domain rendering to 
 * their shapes used within the backend domain view
 * 
 * @author Andreas Held
 *
 */
public class DrawDomainShapesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainGraphView view = ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		
		view.getGraphLayoutManager().setToDomainShapes();
		view.getPrefuseGraph().setRenderer(GraphLayoutManager.DOMAINSHAPE_RENDERER);
	}

}
