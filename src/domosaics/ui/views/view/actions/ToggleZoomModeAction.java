package domosaics.ui.views.view.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.components.ZoomCompatible;




public class ToggleZoomModeAction extends AbstractMenuAction implements ZoomCompatible {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		View view = ViewHandler.getInstance().getFocussedTool();
		if (view == null) 
			view = ViewHandler.getInstance().getActiveView();
		view.toggleZoomMode();
	}

}