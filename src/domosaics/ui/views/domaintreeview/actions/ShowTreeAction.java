package domosaics.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.layout.CSAModeDomainTreeLayout;
import domosaics.ui.views.domaintreeview.layout.DefaultDomainTreeLayout;
import domosaics.ui.views.domaintreeview.layout.DomainTreeLayout;
import domosaics.ui.views.domaintreeview.renderer.DefaultDomainTreeViewRenderer;
import domosaics.ui.views.domainview.layout.DomainLayout;
import domosaics.ui.views.domainview.layout.MSALayout;
import domosaics.ui.views.domainview.layout.ProportionalLayout;
import domosaics.ui.views.domainview.layout.UnproportionalLayout;
import domosaics.ui.views.domainview.renderer.DefaultDomainViewRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.ArrangementRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.MsaArrangementRenderer;
import domosaics.ui.views.treeview.layout.DendogramLayout;
import domosaics.ui.views.treeview.renderer.DefaultTreeViewRenderer;
import domosaics.ui.views.view.layout.ViewLayout;
import domosaics.ui.views.view.renderer.Renderer;




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
