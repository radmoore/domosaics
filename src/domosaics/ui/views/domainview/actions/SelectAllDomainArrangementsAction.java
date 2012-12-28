package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;




/**
 * Action which selects all arrangements at once
 * 
 * @author Andreas Held
 *
 */
public class SelectAllDomainArrangementsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {	
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		if (view.getDomainSimilarityManager().isActive())
			view.getDomainSimilarityManager().end(view);
		
		Collection<ArrangementComponent> toSelection = new ArrayList<ArrangementComponent>();
		for (int i = 0; i < view.getDaSet().length; i++) {
			ArrangementComponent dac = view.getArrangementComponentManager().getComponent(view.getDaSet()[i]);
			if(!dac.isVisible())
				continue;
			toSelection.add(dac);
		}
		
		view.getArrangementSelectionManager().setSelection(toSelection);
	}

}