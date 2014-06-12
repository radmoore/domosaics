package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.configuration.Configuration;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.util.BrowserLauncher;




/**
 * Action which opens a browser window and looks up the selected
 * domain in google.
 * 
 * @author Andreas Held
 *
 */
public class LookupDomainInUniprot extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupDomainInUniprot (String domain) {
		super();
		putValue(Action.NAME, "Look for "+domain+" in Uniprot");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browser to search for the current domain");
	}
	
	@Override
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
		
		BrowserLauncher.openURL(Configuration.getInstance().getUniprotUrl(label));
		
		// deselect the node within the Selection manager 
		view.getDomainSelectionManager().setMouseOverComp(null);
	}

}
