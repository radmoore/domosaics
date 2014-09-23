package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.util.BrowserLauncher;




/**
 * The context menu action LookupDomainInPfamAction opens a browser
 * window and addresses pfam with the domain id of the triggering domain.
 * To adress pfam the url specified in the configuration is used.
 * 
 * @author Andreas Held
 *
 */
public class LookupDomainInSourceDBAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupDomainInSourceDBAction (String domain, String sourceDB) {
		super();
		putValue(Action.NAME, "Look for "+domain+" in "+sourceDB);
		putValue(Action.SHORT_DESCRIPTION, "Opens a browser window");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// the tree must be loaded otherwise the triggering node pup up menu wouldn't exist
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		DomainComponent selectedDomain =  view.getDomainSelectionManager().getClickedComp();
		if (selectedDomain == null) 
			return;
		
		
		// get the nodes label and if its null init it as empty string
		String label = selectedDomain.getLabel();
		if(label == null)
			label = "";		
		
		BrowserLauncher.openURL(view.getDomainSelectionManager().getClickedComp().getDomain().getFamily().getDomainType().getUrl(label)); //"http://www.ncbi.nlm.nih.gov/sites/gquery?term="+label);
		
		// deselect the node within the Selection manager 
		view.getDomainSelectionManager().setMouseOverComp(null);
	}

}
