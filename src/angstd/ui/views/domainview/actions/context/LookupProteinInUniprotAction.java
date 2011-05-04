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
 * The context menu action LookupProteinInUniprotAction opens a browser
 * window and addresses Uniprot with the domain id of the triggering domain.
 * To address Uniprot the url specified in the configuration is used.
 * 
 * @author Andreas Held
 *
 */
public class LookupProteinInUniprotAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public LookupProteinInUniprotAction () {
		super();
		putValue(Action.NAME, "Lookup At Uniprot");
		putValue(Action.SHORT_DESCRIPTION, "Opens a browserwindow showing the Uniprot homepage");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		ArrangementComponent selectedArrangement =  view.getArrangementSelectionManager().getClickedComp();
		if (selectedArrangement == null) 
			return;
		view.getArrangementSelectionManager().getSelection().clear();
		view.getArrangementSelectionManager().getSelection().add(selectedArrangement);
		
		// get the nodes label and if its null init it as empty string
		String label = selectedArrangement.getLabel();
		if(label == null)
			label = "";
		
		
		BrowserLauncher.openURL(Configuration.getInstance().getUniprotUrl(label));
		
		// deselect the node within the Selection manager 
		view.getArrangementSelectionManager().setMouseOverComp(null);
	}

}
