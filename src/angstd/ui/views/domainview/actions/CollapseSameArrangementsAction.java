package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import angstd.ui.AngstdUI;
import angstd.ui.HelpManager;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.manager.CollapseSameArrangementsManager;



/**
 * This action changes the collapse same arrangements mode.
 * If the mode is set to true, same arrangements are collapsed
 * by making them invisible. <br>
 * If the mode was set back to disabled, all arrangements are made
 * visible again. The processing is done by the 
 * {@link CollapseSameArrangementsManager}.
 * 
 * @author Andreas Held
 *
 */
public class CollapseSameArrangementsAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	
	/** message shown within a help dialog */
	private static final String HELPMSG = 
			"Arrangements with identical domain composition got \n" +
			"collapsed. The number behind each arrangement indicates \n" +
			"the number of collapsed arrangements with the same " +
			"domain composition. \n";

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		if (view.getDaSet().length == 0) {
			setState(!getState());
			return;
		}
		
		if (view instanceof DomainTreeViewI && ((DomainTreeViewI) view).getCSAInSubtreeManager().isActive()) {
			MessageUtil.showWarning(AngstdUI.getInstance(), "This option is not available as long as nodes are collapsed");
			setState(!getState());
			return;
		}

		view.getDomainLayoutManager().toggleCollapseSameArrangements();
		
		// delegate to CollapseSameArrangementsManager
		if (view.getDomainLayoutManager().isCollapseSameArrangements()) {
			view.getCollapseSameArrangementsManager().collapseDaSet(view, view.getDaSet());
		
		} else { // reset everything
		
			// make DAs visible again
			Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
			while(iter.hasNext()) 
				view.getArrangementComponentManager().setVisible(iter.next(), true);
	
			// and reset the manager
			view.getCollapseSameArrangementsManager().reset();
		}

		HelpManager.showHelpDialog("Collapse_Same", HELPMSG);
	}

}
