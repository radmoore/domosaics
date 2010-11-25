package angstd.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.actions.CollapseSameArrangementsAtNodeAction;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.wizards.WizardManager;

/**
 * CreateSpeciesTreeAction opens the wizard for creating a species trees
 * based on the associated descriptions of domain arrangements.
 * 
 * @author Andreas Held
 *
 */
public class CreateSpeciesTreeAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent event) {
		boolean created = WizardManager.getInstance().startCreateSpeciesTreeWizard();
		
		if (!created)
			return;
		
		//  and finally collapse organisms
		DomainTreeViewI view = ViewHandler.getInstance().getActiveView();
		
		Iterator<NodeComponent> iter = view.getTreeComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			NodeComponent nc = iter.next();
			if (!nc.getNode().isLeaf() && nc.getNode().getChildAt(0).isLeaf()) 
				CollapseSameArrangementsAtNodeAction.collapse(view, nc);
		}
		
	}
}
