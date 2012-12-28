package angstd.ui.tools.stats.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.tools.stats.StatsFrame;



public class CloseStatsAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	protected StatsFrame frame;

	public CloseStatsAction (StatsFrame frame) {
		super();
		this.frame = frame;
		putValue(Action.NAME, "Close");
		putValue(Action.SHORT_DESCRIPTION, "Closes the stats window");
	}
	
	public void actionPerformed(ActionEvent e) {
		frame.dispose();
	}

}
