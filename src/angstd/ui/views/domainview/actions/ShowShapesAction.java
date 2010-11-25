package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.view.components.ZoomCompatible;

/**
 * Changes the drawing of domains into shape rendering. This action is
 * only allowed within the proportional view.
 * 
 * @author Andreas Held
 *
 */
public class ShowShapesAction extends AbstractMenuAction implements ZoomCompatible{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		view.getDomainLayoutManager().fireStructuralChange();
	}
	
}