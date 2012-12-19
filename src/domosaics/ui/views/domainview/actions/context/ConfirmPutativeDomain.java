package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;


// TODO:
// Allow the undoing of a confirmation
public class ConfirmPutativeDomain extends AbstractAction {

	public ConfirmPutativeDomain (){
		super();
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		DomainComponent selectedDomain =  view.getDomainSelectionManager().getClickedComp();
		if (selectedDomain.getDomain().isPutative()) {
			putValue(Action.NAME, "Confirm Putative Domain");
			putValue(Action.SHORT_DESCRIPTION, "Confirms a putative domain)");		
		}else
		{
			putValue(Action.NAME, "Change status to Putative");
			putValue(Action.SHORT_DESCRIPTION, "Consider a domain as putative");		
		
		}
	}
	
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();

		if (view.getDomainSimilarityManager().isActive())
			view.getDomainSimilarityManager().end(view);
		
		// get the selected node from the SelectionManager
		DomainComponent selectedDomain =  view.getDomainSelectionManager().getClickedComp();
		if (selectedDomain == null) 
			return;
		
		selectedDomain.getDomain().setPutative(!selectedDomain.getDomain().isPutative());
			
			
		view.getDomainLayoutManager().firevisualChange();		
	}
	
}