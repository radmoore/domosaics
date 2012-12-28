package angstd.ui.tools.dotplot.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.dotplot.DotplotView;
import angstd.ui.views.ViewType;



/**
 * Action toggling the rendering of domain similarity boxes
 * 
 * @author Andreas Held
 *
 */
public class ShowDomainMatchesAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DotplotView view = ViewHandler.getInstance().getTool(ViewType.DOTPLOT);
		view.getDotplotLayoutManager().visualChange();
	}

}
