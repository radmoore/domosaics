package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.tools.radscan.RadScanView;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RadScanAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public RadScanAction() {
		super();
		putValue(Action.NAME, "RadScan this arrangement");
		putValue(Action.SHORT_DESCRIPTION, "Performes RADS search (againt UniProt)");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();

		domView.getArrangementSelectionManager().getSelection().clear();
		domView.getArrangementSelectionManager().getSelection().add(domView.getArrangementSelectionManager().getClickedComp());
		domView.getViewComponent().repaint();
		
		ArrangementComponent selectedDA = domView.getArrangementSelectionManager().getClickedComp();
		RadScanView view = ViewHandler.getInstance().createTool(ViewType.RADSCAN);
//		System.out.println("This is the arrangements: "+selectedDA.toString());
		view.setView(selectedDA);
		ViewHandler.getInstance().addTool(view);
	}

}
