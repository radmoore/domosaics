package angstd.ui.tools.domainlegend.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.domainlegend.DomainLegendView;
import angstd.ui.views.ViewType;



/**
 * Sets the sorting of domains to alphabetical order
 * 
 * @author Andreas Held
 *
 */
public class SortAlphabeticallyAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainLegendView view = ViewHandler.getInstance().getTool(ViewType.DOMAINLEGEND);
		
		if (!getState() && !view.getLegendLayoutManager().isSortByFrequency()) {
			setState(!getState());
			return;
		}
		
		view.getLegendLayoutManager().setToSortAlphabetically();
	}
}
