package angstd.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.components.TreeSpaceSlider;



public class ChangeTreeSpaceAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainTreeViewI view = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();
		new TreeSpaceSlider(view).showDialog(AngstdUI.getInstance(), "Tree space slider");
		
		
		
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
