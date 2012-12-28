package angstd.ui.views.view.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.view.View;
import angstd.ui.views.view.components.ZoomCompatible;



public class ToggleZoomModeAction extends AbstractMenuAction implements ZoomCompatible {
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		View view = ViewHandler.getInstance().getFocussedTool();
		if (view == null) 
			view = ViewHandler.getInstance().getActiveView();
		view.toggleZoomMode();
	}

}