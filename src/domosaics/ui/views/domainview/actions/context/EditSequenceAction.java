package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.wizards.WizardManager;




public class EditSequenceAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public EditSequenceAction (){
		super();
		putValue(Action.NAME, "Edit Sequence");
		putValue(Action.SHORT_DESCRIPTION, "Changes the associated sequence");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// get the selected arrangement
		ArrangementComponent selectedDA = domView.getArrangementSelectionManager().getClickedComp();
		WizardManager.getInstance().startChangeSequenceWizard(domView, selectedDA);
	}

}
