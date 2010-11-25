package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.HelpManager;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.manager.DomainLayoutManager.DomainAction;
import angstd.ui.views.domainview.renderer.domain.OrthologousDomainRenderer;

/**
 * Performs the "compare domain sequences" feature
 * for the selected domain only.
 * 
 * @author Andreas Held
 *
 */
public class SearchOrthologousAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	/** message shown within a help dialog */
	private static final String HELPMSG = 
			"Uses the Needlman Wunsch algorithm on domain \n" +
			"sequences for each member of the selected domain family. \n " +
			"The identity score is shown in percent as domain label. \n " +
			"To exit this mode disable \"Compare Domain Sequences\" within the" +
			"edit menu.";
	
	public SearchOrthologousAction (){
		super();
		putValue(Action.NAME, "Domain Sequence Comparison");
		putValue(Action.SHORT_DESCRIPTION, "Compares all domain sequences for this family using Needleman Wunsch.");
	}

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();

		if (!view.isSequenceLoaded()) {
			MessageUtil.showWarning("No sequences associated with arrangements");
			return;
		}
		
		DomainComponent queryDom = view.getDomainSelectionManager().getClickedComp();
		if (queryDom == null)
			return;
		
		view.getDomainSearchOrthologsManager().reset();
	
		// manually set the correct mode within the main menu
		view.getDomainLayoutManager().setState(DomainAction.COMPARE_DOMSEQUENCES, true);
		view.getDomainLayoutManager().setCompare4Domain(true); // needed 4 mouse listener
		
		view.getDomainLayoutManager().toggleCompareDomainsMode();
		view.getDomainViewRenderer().getArrangementRenderer().setDomainRenderer(new OrthologousDomainRenderer());
		view.getDomainSearchOrthologsManager().process(view, queryDom);
		
		HelpManager.showHelpDialog("Domain_Compare", HELPMSG);
	}
	
}
