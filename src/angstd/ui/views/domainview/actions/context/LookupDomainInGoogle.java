package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.model.configuration.Configuration;
import angstd.ui.ViewHandler;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.util.BrowserLauncher;



/**
 * Action which opens a browser window and looks up the selected
 * domain in google.
 * 
 * @author Andreas Held
 *
 */
public class LookupDomainInGoogle extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupDomainInGoogle (String domain) {
		super();
		putValue(Action.NAME, "Lookup for "+domain+" in Google");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browserwindow showing the Google homepage");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		DomainComponent selectedDomain =  view.getDomainSelectionManager().getClickedComp();
		if (selectedDomain == null) 
			return;
		
		// get the nodes label and if its null init it as empty string
		String label = selectedDomain.getLabel();
		if(label == null)
			return;
		
		BrowserLauncher.openURL(Configuration.getInstance().getGoogleUrl(label));
		
		// deselect the node within the Selection manager 
		view.getDomainSelectionManager().setMouseOverComp(null);
	}

}
