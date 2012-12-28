package angstd.ui.tools.domaingraph.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.domaingraph.DomainGraphView;
import angstd.ui.tools.domaingraph.components.GraphLayoutManager;
import angstd.ui.views.ViewType;



/**
 * Action which triggers the domain rendering to circular shape rendering
 * 
 * @author Andreas Held
 *
 */
public class DrawCirclesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainGraphView view = ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		
		view.getGraphLayoutManager().setToCircularShapes();
		view.getPrefuseGraph().setRenderer(GraphLayoutManager.CIRCULAR_RENDERER);
	}

}
