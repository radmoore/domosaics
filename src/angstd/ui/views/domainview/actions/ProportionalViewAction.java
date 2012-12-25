package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.layout.ProportionalLayout;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;

/**
 * Action which switches the domain arrangement renderer of a domain view
 * into the DefaultArrangementRenderer, which draws arrangements as a
 * composition of their domains and with backbone representing the sequence.
 * 
 * @author Andreas Held
 *
 */
public class ProportionalViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		view.setViewLayout(new ProportionalLayout());
		view.getDomainLayoutManager().setToProportionalView();
		view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
		view.getSequenceSelectionMouseController().clearSelection();
		view.registerMouseListeners();
	}
}