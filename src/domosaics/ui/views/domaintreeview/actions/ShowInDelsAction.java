package domosaics.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.components.ReconstructionTool;




/**
 * Action used to show insertion deletions along a domain tree
 * 
 * @author Andreas Held
 *
 */
public class ShowInDelsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	protected ReconstructionTool tool = null;
	
	public void actionPerformed(ActionEvent e) {
		final DomainTreeViewI view = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();
		
		if (getState() == false) {
			if (tool != null) {
				tool.dispose();
				tool = null;
			}
			view.getDomainTreeLayoutManager().structuralChange();
			return;
		}
		
		if (tool == null || !tool.isVisible()) {
			tool = new ReconstructionTool(view);
			tool.showDialog(DoMosaicsUI.getInstance(), "Ancestral Arrangement Reconstruction");
		}

	}
}
