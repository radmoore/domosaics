package domosaics.ui.tools.stats;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.ui.tools.stats.actions.CloseStatsAction;
import domosaics.ui.tools.stats.actions.SaveStatsToCSVAction;




/**
 * Frame for the stats module in DoMosaicS
 * @author Andreas Held
 *
 */
public class StatsFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	/** the menu file describing the view specific menu structure */
	protected static String MENUFILE = "resources/menu.file";
	
	protected StatsTabbedPane tabbedPane;
	protected ArrangementManager manager;
	
	public StatsFrame(ArrangementManager manager) {
		super("View stats");

		// create MenuBar
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new SaveStatsToCSVAction(manager)));
		fileMenu.add(new JSeparator());
		fileMenu.add(new CloseStatsAction(this));
		JMenuBar bar = new JMenuBar();
		bar.add(fileMenu);
		setJMenuBar(bar);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(new OverviewPanel(manager));
		tabbedPane.add("Domain Occurrencies", new JScrollPane(new DomOccurrenciesPanel(manager)));
		getContentPane().add(tabbedPane);
		
		this.setSize(335, 260);
//		pack();
//		System.out.println(this.getSize().toString());
		
		setLocationRelativeTo(null);
		setResizable(true);
		setVisible(true);
	}
    
}
