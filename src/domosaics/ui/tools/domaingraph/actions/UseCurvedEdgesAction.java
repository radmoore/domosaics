package domosaics.ui.tools.domaingraph.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domaingraph.DomainGraphView;
import domosaics.ui.views.ViewType;




/**
 * Action which toggles the option to draw edges curved or straight.
 * 
 * @author Andreas Held
 *
 */
public class UseCurvedEdgesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DomainGraphView view =  ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		view.getPrefuseGraph().toggleUseCurvedEdges();
	}

}
