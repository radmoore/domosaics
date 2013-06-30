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
 * Action which opens a browser window and looks up the selected
 * protein in google.
 * 
 * @author Andreas Held
 *
 */
public class LookupProteinInGoogle extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupProteinInGoogle (String protein) {
		super();
		putValue(Action.NAME, "Look for protein "+protein+" in Google");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browser to search for the current protein");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		ArrangementComponent selectedArrangement =  view.getArrangementSelectionManager().getClickedComp();
		if (selectedArrangement == null) 
			return;
		// get the label and if its null initialize it as empty string
		String label = selectedArrangement.getLabel();
		if(label == null)
			return;
		
		// open the browser window
		BrowserLauncher.openURL(Configuration.getInstance().getGoogleUrl(label)); //"http://www.ncbi.nlm.nih.gov/sites/gquery?term="+label);
		
		// deselect the node within the Selection manager 
		view.getArrangementSelectionManager().setMouseOverComp(null);
	}

}
