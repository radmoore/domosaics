package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.wizards.WizardManager;



public class EditSequenceAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public EditSequenceAction (){
		super();
		putValue(Action.NAME, "Edit Sequence");
		putValue(Action.SHORT_DESCRIPTION, "Changes the associated sequence");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// get the selected arrangement
		ArrangementComponent selectedDA = domView.getArrangementSelectionManager().getClickedComp();
		domView.getArrangementSelectionManager().getSelection().clear();
		domView.getArrangementSelectionManager().getSelection().add(selectedDA);
		
		WizardManager.getInstance().startChangeSequenceWizard(domView, selectedDA);
	}

}
