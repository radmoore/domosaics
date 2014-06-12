package domosaics.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.domaintreeview.components.TreeSpaceSlider;




public class ChangeTreeSpaceAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();
		
		if(TreeSpaceSlider.instance==null)
			 new TreeSpaceSlider(view).showDialog(DoMosaicsUI.getInstance(), "Tree space slider");
			else
				if(TreeSpaceSlider.instance.getView() !=  view){
					TreeSpaceSlider.instance.dispose();
					new TreeSpaceSlider(view).showDialog(DoMosaicsUI.getInstance(), "Tree space slider");
				} else
						TreeSpaceSlider.instance.setVisible(true);
		
		
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
