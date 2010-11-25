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
 * The context menu action LookupDomainInPfamAction opens a browser
 * window and addresses pfam with the domain id of the triggering domain.
 * To adress pfam the url specified in the configuration is used.
 * 
 * @author Andreas Held
 *
 */
public class LookupDomainInPfamAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupDomainInPfamAction () {
		super();
		putValue(Action.NAME, "Lookup At Pfam");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browserwindow showing the NCBI homepage");
	}
	
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
		
		
		BrowserLauncher.openURL(Configuration.getInstance().getPfamUrl(label)); //"http://www.ncbi.nlm.nih.gov/sites/gquery?term="+label);
		
		// deselect the node within the Selection manager 
		view.getDomainSelectionManager().setMouseOverComp(null);
	}

}
