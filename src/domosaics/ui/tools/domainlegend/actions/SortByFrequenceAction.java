package domosaics.ui.tools.domainlegend.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domainlegend.DomainLegendView;
import domosaics.ui.views.ViewType;




/**
 * Sets the sorting of domains to ordered by number of occurrences within
 * the dataset
 * 
 * @author Andreas Held
 *
 */
public class SortByFrequenceAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainLegendView view = ViewHandler.getInstance().getTool(ViewType.DOMAINLEGEND);
		
		if (!getState() && !view.getLegendLayoutManager().isSortAlphabetically()) {
			setState(!getState());
			return;
		}
		
		view.getLegendLayoutManager().setToSortByFrequence();
	}
}