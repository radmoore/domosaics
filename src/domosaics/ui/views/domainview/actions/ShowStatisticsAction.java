package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.stats.StatsFrame;
import domosaics.ui.views.domainview.DomainViewI;




public class ShowStatisticsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		ArrangementManager manager = new ArrangementManager();
		manager.add(view.getDaSet());
		
		new StatsFrame(manager);
	}

}
