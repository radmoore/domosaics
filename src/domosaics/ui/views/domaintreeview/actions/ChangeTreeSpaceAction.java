package domosaics.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.components.TreeSpaceSlider;


public class ChangeTreeSpaceAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainTreeViewI view = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();
		new TreeSpaceSlider(view).showDialog(DoMosaicsUI.getInstance(), "Tree space slider");
		
		
		
//		view.getDomainTreeLayoutManager().toggleShowTree();
//
//		view.setViewLayout(getCorrectLayout(view));
//		view.setViewRenderer(getCorrectRenderer(view));
//		
//		view.registerMouseListeners();
//		view.registerAdditionalDomainTreeRenderer(view);
//		
//		view.getDomainTreeLayoutManager().structuralChange();
	}

}
