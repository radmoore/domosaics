package domosaics.ui.views.domainview.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.stats.StatsFrame;
import domosaics.ui.views.domainview.DomainViewI;




public class ShowStatisticsAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		if(StatsFrame.instance==null)
		 new StatsFrame(view);
		else
			if(StatsFrame.instance.getState()==Frame.ICONIFIED){
				StatsFrame.instance.dispose();
				new StatsFrame(view);		
			} else
			{
				if(StatsFrame.instance.getView() !=  view){
					StatsFrame.instance.dispose();
					new StatsFrame(view);
				}	else
					StatsFrame.instance.setVisible(true);
			}
	}

}
