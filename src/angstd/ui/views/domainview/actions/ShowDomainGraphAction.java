package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.domaingraph.DomainGraphView;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;

/**
 * Action which opens the domain co occurrence graph.
 * 
 * @author Andreas Held
 *
 */
public class ShowDomainGraphAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = ViewHandler.getInstance().getActiveView();
		DomainGraphView view = ViewHandler.getInstance().createTool(ViewType.DOMAINGRAPH);
		view.setDomainView(domView);
		ViewHandler.getInstance().addTool(view);
	}

}
