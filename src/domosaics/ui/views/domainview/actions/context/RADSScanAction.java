package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.tools.RADSTool.RADSScanView;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.ArrangementPopupMenu;
import domosaics.webservices.RADS.RADSService;




/**
 * This class defines the RADSScanAction, which is invoked from the 
 * Arrangement context menu.
 * 
 * see {@link ArrangementPopupMenu} and {@link AbstractAction} for more details
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSScanAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new instance of RADSScanAction
	 */
	public RADSScanAction() {
		super();
		putValue(Action.NAME, "RadScan this arrangement");
		putValue(Action.SHORT_DESCRIPTION, "Performes RADS/RAMPAGE search (againt UniProt)");
	}
	
	/**
	 * Triggered when the RADS is chosen from the arrangement context menu
	 * This will check whether RADS is currently running, and will warn if so
	 */
	public void actionPerformed(ActionEvent e) {
		if (RADSService.isRunning()) {
			MessageUtil.showWarning("RADS/RAMPAGE is currently running. Terminate or wait to complete.");
			return;
		}
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();

		ArrangementComponent selectedDA = domView.getArrangementSelectionManager().getClickedComp();
		RADSScanView view = ViewHandler.getInstance().createTool(ViewType.RADSCAN);
		view.setView(selectedDA);
		ViewHandler.getInstance().addVisibleTool(view);
	}

}
