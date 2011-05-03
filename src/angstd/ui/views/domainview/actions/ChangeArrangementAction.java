package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.changearrangement.ChangeArrangementView;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;

/**
 * Changes the arrangement attributes
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();		
		if (domView.getArrangementSelectionManager().getSelection().isEmpty() || domView.getArrangementSelectionManager().getSelection().size()>1) {
			MessageUtil.showWarning("Please select exactly 1 protein.");
			return;
		}
		
		// Exit from the sequence comparison mode
		if (domView.isCompareDomainsMode())
		{
			domView.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			domView.setCompareDomainsMode(false);
			domView.getDomainLayoutManager().toggleCompareDomainsMode(domView.isCompareDomainsMode());
			domView.getDomainSearchOrthologsManager().reset();
			domView.getViewComponent().repaint();
		}
		
		
		// get the selected arrangement to display it 
		ArrangementComponent selectedDA = domView.getArrangementSelectionManager().getSelection().iterator().next();

		ChangeArrangementView view = ViewHandler.getInstance().createTool(ViewType.CHANGEARRANGEMENT);
		view.setView(domView, selectedDA);
		ViewHandler.getInstance().addTool(view);
	}
}
