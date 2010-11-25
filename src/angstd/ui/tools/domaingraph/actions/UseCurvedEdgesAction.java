package angstd.ui.tools.domaingraph.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.domaingraph.DomainGraphView;
import angstd.ui.views.ViewType;

/**
 * Action which toggles the option to draw edges curved or straight.
 * 
 * @author Andreas Held
 *
 */
public class UseCurvedEdgesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainGraphView view =  ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		view.getPrefuseGraph().toggleUseCurvedEdges();
	}

}
