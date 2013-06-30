package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.configuration.Configuration;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.util.BrowserLauncher;




/**
 * The context menu action LookupProteinInUniprotAction opens a browser
 * window and addresses Uniprot with the domain id of the triggering domain.
 * To address Uniprot the url specified in the configuration is used.
 * 
 * @author Andreas Held
 *
 */
public class LookupProteinInUniprotAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupProteinInUniprotAction (String protein) {
		super();
		putValue(Action.NAME, "Look for protein "+protein+" in Uniprot");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browser to search for the current protein");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		ArrangementComponent selectedArrangement =  view.getArrangementSelectionManager().getClickedComp();
		if (selectedArrangement == null) 
			return;
		
		// get the nodes label and if its null init it as empty string
		String label = selectedArrangement.getLabel();
		if(label == null)
			label = "";
		
		
		BrowserLauncher.openURL(Configuration.getInstance().getUniprotUrl(label));
		
		// deselect the node within the Selection manager 
		view.getArrangementSelectionManager().setMouseOverComp(null);
	}

}
