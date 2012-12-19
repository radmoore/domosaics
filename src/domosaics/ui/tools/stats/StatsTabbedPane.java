package domosaics.ui.tools.stats;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Tabbed pane for different stats to visualize.
 * 
 * @author Andreas Held
 *
 */
public class StatsTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public StatsTabbedPane () {
		super();
	}
	
	public void addTab(JPanel tab) {
		addTab(tab.getName(), null, tab, "Does nothing");
	}
}
