package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.model.arrangement.ArrangementManager;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.stats.StatsFrame;
import angstd.ui.views.domainview.DomainViewI;



public class ShowStatisticsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		ArrangementManager manager = new ArrangementManager();
		manager.add(view.getDaSet());
		
		new StatsFrame(manager);
	}

}
