package angstd.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.layout.CSAModeDomainTreeLayout;
import angstd.ui.views.domaintreeview.layout.DefaultDomainTreeLayout;
import angstd.ui.views.domaintreeview.layout.DomainTreeLayout;
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
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;



/**
 * Displays the tree within the domain tree view
 * 
 * @author Andreas Held
 *
 */
public class ShowTreeAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainTreeViewI view = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainTreeLayoutManager().toggleShowTree();

		view.setViewLayout(getCorrectLayout(view));
		view.setViewRenderer(getCorrectRenderer(view));
		
		view.registerMouseListeners();
		view.registerAdditionalDomainTreeRenderer(view);
		
		view.getDomainTreeLayoutManager().structuralChange();
	}
	
	// TODO MOVE IT
	public static ViewLayout getCorrectLayout(DomainTreeViewI view) {
		ViewLayout layout = null;
		
		// set the correct layout and renderer
		if (view.getDomainTreeLayoutManager().isShowTree() && view.getDomainTreeLayoutManager().isShowArrangements()) {
			layout = (view.getCSAInSubtreeManager().isActive()) ? new CSAModeDomainTreeLayout() : new DefaultDomainTreeLayout();
			((DomainTreeLayout) layout).setDomainLayout(getActuallyUsedDomainLayout(view));
		}
		else if (view.getDomainTreeLayoutManager().isShowTree() && !view.getDomainTreeLayoutManager().isShowArrangements()) {
			layout = new DendogramLayout();
		}
		else if (!view.getDomainTreeLayoutManager().isShowTree() && view.getDomainTreeLayoutManager().isShowArrangements()) {
			layout = getActuallyUsedDomainLayout(view);
		}
		return layout;
	}
	
	// TODO MOVE IT
	public static Renderer getCorrectRenderer(DomainTreeViewI view) {
		Renderer renderer = null;
		if (view.getDomainTreeLayoutManager().isShowTree() && view.getDomainTreeLayoutManager().isShowArrangements()) {
			// set corresponding domain renderer
			renderer = new DefaultDomainTreeViewRenderer(view);
			((DefaultDomainTreeViewRenderer)renderer).setArrangementRenderer(getActuallyUsedDomainRenderer(view));
		}
		else if (view.getDomainTreeLayoutManager().isShowTree() && !view.getDomainTreeLayoutManager().isShowArrangements()) {
			renderer = new DefaultTreeViewRenderer(view);
		}
		else if (!view.getDomainTreeLayoutManager().isShowTree() && view.getDomainTreeLayoutManager().isShowArrangements()) {
			renderer = new DefaultDomainViewRenderer(view, getActuallyUsedDomainRenderer(view));
		}
		return renderer;
	}
	
	private static DomainLayout getActuallyUsedDomainLayout(DomainTreeViewI view) {
		if (view.getDomainLayoutManager().isProportionalView())
			return new ProportionalLayout();
		else if(view.getDomainLayoutManager().isUnproportionalView())
			return new UnproportionalLayout();
		else if(view.getDomainLayoutManager().isMsaView())
			return new MSALayout();
		return null;
	}
	
	private static ArrangementRenderer getActuallyUsedDomainRenderer(DomainTreeViewI view) {
		if (view.getDomainLayoutManager().isProportionalView())
			return new BackBoneArrangementRenderer();
		else if(view.getDomainLayoutManager().isUnproportionalView())
			return new BackBoneArrangementRenderer();
		else if(view.getDomainLayoutManager().isMsaView())
			return new MsaArrangementRenderer();
		return null;
	}

}
