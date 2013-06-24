package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.dotplot.DotplotView;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;




/**
 * Action which opens the domain dotplot.
 * 
 * @author Andreas Held
 *
 */
public class ShowDotplotAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = ViewHandler.getInstance().getActiveView();
		
		if (domView.getArrangementSelectionManager().getSelection().isEmpty()) {
			if(domView.getDomainLayoutManager().isSelectSequences())
			{
				domView.getDomainLayoutManager().toggleSelectArrangements();
				domView.getSequenceSelectionMouseController().clearSelection();
				domView.registerMouseListeners();
			}
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please select 2 proteins first.");
			return;
		}
		
		// check if the two arrangements have sequences associated with them
		Collection<ArrangementComponent> das = domView.getArrangementSelectionManager().getSelection();
		Iterator<ArrangementComponent> iter = das.iterator();
		while(iter.hasNext())
			if (iter.next().getDomainArrangement().getSequence() == null) {
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"The arrangements have no sequence associated.");
				return;
			}
		
		DotplotView view = ViewHandler.getInstance().createTool(ViewType.DOTPLOT);
		view.setDomainView(domView);
		ViewHandler.getInstance().addTool(view);
	}

}
