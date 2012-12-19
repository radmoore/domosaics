package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;


public class DeleteArrangementContextAction extends AbstractAction{
	private static final long serialVersionUID = 1L;

	
	public DeleteArrangementContextAction () {
		super();
		putValue(Action.NAME, "Delete Arrangement"); 
		putValue(Action.SHORT_DESCRIPTION, "Deletes the arrangement from the view");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		ArrangementComponent selectedDA = view.getArrangementSelectionManager().getClickedComp();
		
//		// ask the user if he is sure about this action?
//		if (!MessageUtil.showDialog("Delete arrangements? This action cannot be undone."))
//			return;
			
		view.removeArrangement(selectedDA.getDomainArrangement());
		
		view.getArrangementSelectionManager().clearSelection();
		view.getDomainSelectionManager().clearSelection();
			
		view.getDomainLayoutManager().structuralChange();
	}

}
