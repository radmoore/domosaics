package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.tools.changearrangement.ChangeArrangementView;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;

/**
 * Changes the arrangement attributes
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ChangeArrangementAction (){
		super();
		putValue(Action.NAME, "Change Domain Composition");
		putValue(Action.SHORT_DESCRIPTION, "Changes the domain arrangement attributes");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// get the selected arrangement to display it 
		ArrangementComponent selectedDA = domView.getArrangementSelectionManager().getClickedComp();

		ChangeArrangementView view = ViewHandler.getInstance().createTool(ViewType.CHANGEARRANGEMENT);
		view.setView(domView, selectedDA);
		ViewHandler.getInstance().addTool(view);
	}
}
