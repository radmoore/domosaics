package angstd.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.layout.DefaultDomainTreeLayout;
import angstd.ui.views.domaintreeview.renderer.DefaultDomainTreeViewRenderer;
import angstd.ui.views.domainview.layout.DomainLayout;
import angstd.ui.views.domainview.layout.MSALayout;
import angstd.ui.views.domainview.layout.ProportionalLayout;
import angstd.ui.views.domainview.layout.UnproportionalLayout;
import angstd.ui.views.domainview.renderer.DefaultDomainViewRenderer;
import angstd.ui.views.domainview.renderer.arrangement.ArrangementRenderer;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import angstd.ui.views.domainview.renderer.arrangement.MsaArrangementRenderer;
import angstd.ui.views.treeview.layout.DendogramLayout;
import angstd.ui.views.treeview.renderer.DefaultTreeViewRenderer;



/**
 * Displays the domain arrangements within the domain tree view
 * 
 * @author Andreas Held
 *
 */
public class ShowArrangementsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainTreeViewI view = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainTreeLayoutManager().toggleShowArrangements();
		
		view.registerMouseListeners();
		view.registerAdditionalDomainTreeRenderer(view);
		
		// set the correct layout and renderer
		if (view.getDomainTreeLayoutManager().isShowTree() && view.getDomainTreeLayoutManager().isShowArrangements()) {
			// set corresponding domain layout
			DefaultDomainTreeLayout domTreeLayout = new DefaultDomainTreeLayout();
			domTreeLayout.setDomainLayout(getActuallyUsedDomainLayout(view));
			view.setViewLayout(domTreeLayout);
			
			// set corresponding domain renderer
			DefaultDomainTreeViewRenderer domTreeRenderer = new DefaultDomainTreeViewRenderer(view);
			domTreeRenderer.setArrangementRenderer(getActuallyUsedDomainRenderer(view));
			view.setViewRenderer(domTreeRenderer);
		}
		else if (view.getDomainTreeLayoutManager().isShowTree() && !view.getDomainTreeLayoutManager().isShowArrangements()) {
			view.setViewLayout(new DendogramLayout());
			view.setViewRenderer(new DefaultTreeViewRenderer(view));
		}
		else if (!view.getDomainTreeLayoutManager().isShowTree() && view.getDomainTreeLayoutManager().isShowArrangements()) {
			view.setViewLayout(getActuallyUsedDomainLayout(view));
			view.setViewRenderer(new DefaultDomainViewRenderer(view, getActuallyUsedDomainRenderer(view)));
		}
			
		view.getDomainTreeLayoutManager().structuralChange();
	}
	
	private DomainLayout getActuallyUsedDomainLayout(DomainTreeViewI view) {
		if (view.getDomainLayoutManager().isProportionalView())
			return new ProportionalLayout();
		else if(view.getDomainLayoutManager().isUnproportionalView())
			return new UnproportionalLayout();
		else if(view.getDomainLayoutManager().isMsaView())
			return new MSALayout();
		return null;
	}
	
	private ArrangementRenderer getActuallyUsedDomainRenderer(DomainTreeViewI view) {
		if (view.getDomainLayoutManager().isProportionalView())
			return new BackBoneArrangementRenderer();
		else if(view.getDomainLayoutManager().isUnproportionalView())
			return new BackBoneArrangementRenderer();
		else if(view.getDomainLayoutManager().isMsaView())
			return new MsaArrangementRenderer();
		return null;
	}

}
