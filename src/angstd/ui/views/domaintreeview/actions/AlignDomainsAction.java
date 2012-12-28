package angstd.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import angstd.algos.align.ConsensusAlignment;
import angstd.model.arrangement.DomainVector;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.layout.UnproportionalLayout;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;



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
