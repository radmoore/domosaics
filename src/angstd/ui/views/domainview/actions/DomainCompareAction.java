package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.HelpManager;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import angstd.ui.views.domainview.renderer.domain.OrthologousDomainRenderer;

/**
 * Uses the needleman Wunsch algorithm on all domains of the
 * families within the arrangement.
 * 
 * @author Andreas Held
 *
 */
public class DomainCompareAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	/** message shown within a help dialog */
	private static final String HELPMSG = 
			"Uses the Needlman Wunsch algorithm on domain \n" +
			"sequences for each specified domain family within the \n" +
			"arrangement. \n" +
			"The identity score is shown in percent as domain label. ";

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// excited the mode
		if (!view.getDomainLayoutManager().isCompareDomainsMode()) {
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.getDomainLayoutManager().toggleCompareDomainsMode();
			view.getArrangementSelectionManager().clearSelection();
			view.getDomainSearchOrthologsManager().reset();
			view.getViewComponent().repaint();
			return;
		}
		
		if (!view.isSequenceLoaded()) {
			MessageUtil.showWarning("No sequences associated with arrangements");
			setState(!getState());
			return;
		}
			
		if(view.getArrangementSelectionManager().getSelection().size() != 1) {
			MessageUtil.showWarning("Select exact one arrangement");
			setState(!getState());
			return;
		}
		
		// get the selected arrangement	and its query domains
		ArrangementComponent dac = view.getArrangementSelectionManager().getSelection().iterator().next();
		
		if(!view.getArrangementComponentManager().getDomains(dac).iterator().hasNext()) {
			MessageUtil.showWarning("Selected arrangement does not contain any query domains");
			return;
		}
	
		// set the layout and renderer settings
		view.getDomainLayoutManager().toggleCompareDomainsMode();
		view.getDomainViewRenderer().getArrangementRenderer().setDomainRenderer(new OrthologousDomainRenderer());
	
		// for each domain run the orthologous manager and repaint
		for(DomainComponent dc : view.getArrangementComponentManager().getDomains(dac))
			view.getDomainSearchOrthologsManager().process(view, dc);
		
		HelpManager.showHelpDialog("Domain_Compare", HELPMSG);
	}

}
