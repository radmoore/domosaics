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

public class ShowFastaAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowFastaAction(){
		super();
		putValue(Action.NAME, "Show Fasta Sequence");
		putValue(Action.SHORT_DESCRIPTION, "Show the fasta sequence of the current protein");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		// get the selected arrangement
		ArrangementComponent selectedDAComp = domView.getArrangementSelectionManager().getClickedComp();
		DomainArrangement selectedDA = selectedDAComp.getDomainArrangement();
		new ShowDataDialog(selectedDA, ShowDataDialog.FASTA).showDialog(
				DoMosaicsUI.getInstance(), "Fasta for "+selectedDA.getName());
	}
	

}
