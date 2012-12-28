package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;




/**
 * Action which selects all arrangements containing the specified domain
 * 
 * @author Andreas Held
 *
 */
public class SelectArrangementsContainingDomAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public SelectArrangementsContainingDomAction () {
		super();
		putValue(Action.NAME, "Select arrangements containing domain");
		putValue(Action.SHORT_DESCRIPTION, "Marks all arrangements which contain the domain");
	}
	
	
	public void actionPerformed(ActionEvent e) {
		// the arrangements must be loaded otherwise the triggering node pup up menu wouldn't exist
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();

		if (view.getDomainSimilarityManager().isActive())
			view.getDomainSimilarityManager().end(view);
		
		// get the selected node from the SelectionManager
		DomainComponent selectedDomain =  view.getDomainSelectionManager().getClickedComp();
		if (selectedDomain == null) 
			return;
		
		Collection<ArrangementComponent> toSelection = new ArrayList<ArrangementComponent>();
		for (int i = 0; i < view.getDaSet().length; i++) 
			if (view.getDaSet()[i].contains(selectedDomain.getDomain())) {
				ArrangementComponent dac = view.getArrangementComponentManager().getComponent(view.getDaSet()[i]);
				if(!dac.isVisible())
					continue;
				toSelection.add(dac);
			}
		
		view.getArrangementSelectionManager().setSelection(toSelection);
	}

}
