package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainViewI;



/**
 * Action which toggles the SelectArrangements mode which allows the
 * user to manually select arrangements.
 * 
 * @author Andreas Held
 *
 */
public class SelectDomainArrangementsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {	
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		view.getDomainLayoutManager().toggleSelectArrangements();
		
		// the mouse listeners have to be changed within the view
		view.registerMouseListeners();
		
		// the selection manager has to be reset => dont draw highlight artifacts
		view.getSequenceSelectionMouseController().clearSelection();
		view.getParentPane().repaint();
	}

}
