package angstd.ui.views.view.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.view.View;



/**
 * 
 * @author Andreas Held
 *
 */
public class CloseAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent e) {
		View tool = ViewHandler.getInstance().getFocussedTool();
		if (tool != null)
			ViewHandler.getInstance().removeTool(tool);
		else 
			ViewHandler.getInstance().disableActiveView();
	}

}
