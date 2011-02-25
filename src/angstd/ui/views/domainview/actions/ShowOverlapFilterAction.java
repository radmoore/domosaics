package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.EvalueSliderTool;
import angstd.ui.views.domainview.components.OverlapFilter;

public class ShowOverlapFilterAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	protected OverlapFilter overlapFilter = null;
	
	public void actionPerformed(ActionEvent e) {
		if (overlapFilter == null || !overlapFilter.isVisible()) {
			DomainViewI view = ViewHandler.getInstance().getActiveView();
			overlapFilter = new OverlapFilter(view);
			overlapFilter.showDialog(AngstdUI.getInstance(), "Evalue Resolver");
		}
	}

}