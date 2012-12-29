package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.layout.UnproportionalLayout;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;




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
		view.registerMouseListeners();
	}
}