package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.tools.changearrangement.ChangeArrangementView;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;




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
		putValue(Action.NAME, "Edit Domain Composition");
		putValue(Action.SHORT_DESCRIPTION, "Change the domain arrangement of a given protein");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();

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
		ArrangementComponent selectedDA = domView.getArrangementSelectionManager().getClickedComp();

		ChangeArrangementView view = ViewHandler.getInstance().createTool(ViewType.CHANGEARRANGEMENT);
		view.setView(domView, selectedDA);
		ViewHandler.getInstance().addTool(view);
	}
}
