package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.ShowDataDialog;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;

public class ShowXdomAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowXdomAction(){
		super();
		putValue(Action.NAME, "Show XDOM entry");
		putValue(Action.SHORT_DESCRIPTION, "Show the XDOM entry of the current protein");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		// get the selected arrangement
		ArrangementComponent selectedDAComp = domView.getArrangementSelectionManager().getClickedComp();
		DomainArrangement selectedDA = selectedDAComp.getDomainArrangement();
		new ShowDataDialog(selectedDA, ShowDataDialog.XDOM).showDialog(
				DoMosaicsUI.getInstance(), "XDOM for "+selectedDA.getName());
	}
	
}
