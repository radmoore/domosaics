package domosaics.ui.tools.domainlegend.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domainlegend.DomainLegendView;
import domosaics.ui.views.ViewType;




/**
 * Sets the sorting of domains to alphabetical order
 * 
 * @author Andreas Held
 *
 */
public class SortAlphabeticallyAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {		
		if (!getState()) {
			setState(!getState());
		} else {
			DomainLegendView view = ViewHandler.getInstance().getTool(ViewType.DOMAINLEGEND);
			view.getLegendLayoutManager().setToSortAlphabetically();
		}
	}
}
