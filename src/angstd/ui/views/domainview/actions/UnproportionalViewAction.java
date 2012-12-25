package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.layout.UnproportionalLayout;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;

/**
 * Action which switches the DomainView into an unproportional view.
 * 
 * @author Andreas Held
 *
 */
public class UnproportionalViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {		
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		view.setViewLayout(new UnproportionalLayout());
		view.getDomainLayoutManager().setToUnproportionalView();
		view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
		view.getSequenceSelectionMouseController().clearSelection();
		view.registerMouseListeners();
	}
}