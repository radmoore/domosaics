package domosaics.ui.tools.dotplot.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.dotplot.DotplotView;
import domosaics.ui.views.ViewType;


/**
 * Action toggling the rendering of domain similarity boxes
 * 
 * @author Andreas Held
 *
 */
public class ShowDomainMatchesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DotplotView view = ViewHandler.getInstance().getTool(ViewType.DOTPLOT);
		view.getDotplotLayoutManager().visualChange();
	}

}
