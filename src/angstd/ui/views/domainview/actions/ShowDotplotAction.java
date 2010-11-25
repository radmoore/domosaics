package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.dotplot.DotplotView;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;

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
			MessageUtil.showWarning("Please select 2 proteins first.");
			return;
		}
		
		// check if the two arrangements have sequences associated with them
		Collection<ArrangementComponent> das = domView.getArrangementSelectionManager().getSelection();
		Iterator<ArrangementComponent> iter = das.iterator();
		while(iter.hasNext())
			if (iter.next().getDomainArrangement().getSequence() == null) {
				MessageUtil.showWarning("The arrangements have no sequence associated.");
				return;
			}
		
		DotplotView view = ViewHandler.getInstance().createTool(ViewType.DOTPLOT);
		view.setDomainView(domView);
		ViewHandler.getInstance().addTool(view);
	}

}
