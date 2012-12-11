package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.model.configuration.Configuration;
import angstd.ui.ViewHandler;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.util.BrowserLauncher;

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
		putValue(Action.NAME, "Lookup for protein "+protein+" in Google");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browserwindow showing the Google homepage");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		ArrangementComponent selectedArrangement =  view.getArrangementSelectionManager().getClickedComp();
		if (selectedArrangement == null) 
			return;
		view.getArrangementSelectionManager().getSelection().clear();
		view.getArrangementSelectionManager().getSelection().add(selectedArrangement);
		
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
