package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.HelpManager;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import domosaics.ui.views.domainview.renderer.domain.OrthologousDomainRenderer;




/**
 * Performs the "compare domain sequences" feature
 * for the selected domain only.
 * 
 * @author Andreas Held
 *
 */
public class SearchOrthologousAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	private static DomainComponent queryDom;
	
	/** message shown within a help dialog */
	private static final String HELPMSG = 
			"Uses the Needlman Wunsch algorithm on domain \n" +
			"sequences for each member of the selected domain family. \n " +
			"The identity score is shown in percent as domain label. \n " +
			"To exit this mode disable \"Compare Domain Sequences\" within the" +
			"edit menu.";
	
	public SearchOrthologousAction (){
		super();
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		if (queryDom != null) {
				if(view.isCompareDomainsMode() && queryDom==view.getDomainSelectionManager().getClickedComp()) {
					putValue(Action.NAME, "Unapply Domain Sequence Comparison");
			}else
			{
				putValue(Action.NAME, "Apply Domain Sequence Comparison");
			}
		}else
		{
			putValue(Action.NAME, "Apply Domain Sequence Comparison");
		}
		putValue(Action.SHORT_DESCRIPTION, "Compares all domain sequences for this family using Needleman Wunsch.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		if(queryDom!=null)
			if ((view.isCompareDomainsMode() && queryDom==view.getDomainSelectionManager().getClickedComp()) ) {
				view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
				view.setCompareDomainsMode(false);
				view.getDomainLayoutManager().toggleCompareDomainsMode(view.isCompareDomainsMode());
				view.getDomainLayoutManager().deselectAll();
				view.getArrangementSelectionManager().clearSelection();
				view.getDomainSearchOrthologsManager().reset();
				view.getViewComponent().repaint();
				return;
			}
		
		if (!view.isSequenceLoaded()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No sequences associated with arrangements");
			return;
		}
		
		queryDom = view.getDomainSelectionManager().getClickedComp();
		if (queryDom == null)
			return;
		if(queryDom.getDomain().getSequence()==null){
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.setCompareDomainsMode(false);
			view.getDomainLayoutManager().toggleCompareDomainsMode(view.isCompareDomainsMode());
			view.getDomainLayoutManager().deselectAll();
			view.getArrangementSelectionManager().clearSelection();
			view.getDomainSearchOrthologsManager().reset();
			view.getViewComponent().repaint();
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No sequence associated with this domain");
			return;
		}
		
		view.getDomainSearchOrthologsManager().reset();
	
		// manually set the correct mode within the main menu
		view.setCompareDomainsMode(true);
		view.getDomainLayoutManager().toggleCompareDomainsMode(view.isCompareDomainsMode());
		view.getDomainViewRenderer().getArrangementRenderer().setDomainRenderer(new OrthologousDomainRenderer());
		view.getDomainSearchOrthologsManager().process(view, queryDom);
		
		HelpManager.showHelpDialog("Domain_Compare", HELPMSG);
	}
	
}
