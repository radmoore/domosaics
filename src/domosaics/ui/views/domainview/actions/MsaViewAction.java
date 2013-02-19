package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.layout.MSALayout;
import domosaics.ui.views.domainview.renderer.arrangement.MsaArrangementRenderer;




/**
 * Action which switches the domain arrangement renderer of a domain view
 * into the MsaArrangementRenderer, which draws the underlying sequence 
 * as well.
 * 
 * @author Andreas Held
 *
 */
public class MsaViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		if (!view.isSequenceLoaded()) {
			MessageUtil.showWarning("No sequences associated with arrangements");
			setState(!getState());
			return;
		}
		
		// else change the renderer and set the layout flags
		view.setViewLayout(new MSALayout());
		view.getDomainViewRenderer().setArrangementRenderer(new MsaArrangementRenderer());		
		view.getDomainLayoutManager().setToMsaView();
		view.getDomainLayoutManager().deselectAll();
		view.getArrangementSelectionManager().clearSelection();
		view.getDomainSelectionManager().clearSelection();
		view.registerMouseListeners();
	}
}