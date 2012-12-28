package domosaics.ui.tools.domaingraph.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domaingraph.DomainGraphView;
import domosaics.ui.views.ViewType;




/**
 * Action which toggles the force driven layout.
 * 
 * @author Andreas Held
 *
 */
public class UseForceLayoutAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainGraphView view =  ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		view.getGraphLayoutManager().toggleUseForceLayout();
		view.getPrefuseGraph().toggleUseForceLayout();
		view.getPrefuseGraph().setForces(!view.getGraphLayoutManager().isDisableForces());
	}

}
