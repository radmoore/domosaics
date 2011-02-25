package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;

// TODO:
// Allow the undoing of a confirmation
public class ConfirmPutativeDomain extends AbstractAction {

	public ConfirmPutativeDomain (){
		super();
		putValue(Action.NAME, "Confirm Putative Domain");
		putValue(Action.SHORT_DESCRIPTION, "Confirms a CODD detected putative domain");
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
		
		if (selectedDomain.getDomain().isPutative())
			selectedDomain.getDomain().setPutative(false);
			
		view.getDomainLayoutManager().firevisualChange();		
	}
	
}