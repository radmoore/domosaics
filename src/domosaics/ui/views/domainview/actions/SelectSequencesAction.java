package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.HelpManager;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;




/**
 * Action which makes it possible to select underlying sequences for
 * domain arrangements. This action is only supported for the proportional
 * view.
 * 
 * @author Andreas Held
 *
 */
public class SelectSequencesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	/** message shown within a help dialog */
	private static final String HELPMSG = 
			"Select underlying sequences via selection rectangle \n" +
			"which expands when dragging the mouse. Each new selection \n" +
			"is added to the previous one. To clear the current \n" +
			"selection use a single left click. To export the \n" +
			"selected sequences use right click.";

	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
	
		if(!view.isSequenceLoaded()) {
			setState(!getState());
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No sequences associated with the domain arrangements.");
			return;
		}
		view.getDomainLayoutManager().toggleSelectSequences();
		
		// the mouse listeners have to be changed within the view
		view.registerMouseListeners();
		
		// the selection manager has to be reset => dont draw highlight artifacts
		view.getDomainLayoutManager().deselectAll();
		view.getArrangementSelectionManager().clearSelection();
		view.getDomainSelectionManager().clearSelection();
		view.getParentPane().repaint();
		
		HelpManager.showHelpDialog("Select_Seqs", HELPMSG);
		
	}
	
}
