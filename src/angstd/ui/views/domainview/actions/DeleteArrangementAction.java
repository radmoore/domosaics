package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;



/**
 * Action which deletes the selected arrangements from the view.
 * 
 * @author Andreas Held
 *
 */
public class DeleteArrangementAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// check the number of selected proteins, if its zero warn the user
		int numDAs = view.getArrangementSelectionManager().getSelection().size();
		if (numDAs == 0) {
			MessageUtil.showWarning("No preoteins selected, please select at least one arrangement");
			return;
		}
		
		// ask the user if he is sure about this action?
		if (!MessageUtil.showDialog("Delete arrangements? This action cannot be undone."))
			return;
			
		// delete arrangements
		for (ArrangementComponent dac : view.getArrangementSelectionManager().getSelection()) {
			view.removeArrangement(dac.getDomainArrangement());
		}
		
		view.getArrangementSelectionManager().clearSelection();
		view.getDomainSelectionManager().clearSelection();
			
		view.getDomainLayoutManager().structuralChange();
//		view.setChanged(true);
	}

}
