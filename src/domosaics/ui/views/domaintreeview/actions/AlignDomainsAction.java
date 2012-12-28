package domosaics.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import domosaics.algos.align.ConsensusAlignment;
import domosaics.model.arrangement.DomainVector;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.layout.UnproportionalLayout;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;




public class AlignDomainsAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainTreeViewI view = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();
		
		if (!view.getDomainLayoutManager().isUnproportionalView()) {
			view.setViewLayout(new UnproportionalLayout());
			view.getDomainLayoutManager().setToUnproportionalView();
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.registerMouseListeners();
		}
		
		DomainVector consensus = new ConsensusAlignment().align(view.getDomTree());
		ConsensusAlignment.alignInView(consensus, view);
		
		view.getDomainLayoutManager().structuralChange();
	}
	
}
