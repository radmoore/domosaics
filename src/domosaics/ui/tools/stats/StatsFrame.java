package domosaics.ui.tools.stats;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import domosaics.ui.views.domainview.DomainViewI;

/**
 * Frame for the stats module in DoMosaics
 * @author Andreas Held
 *
 */
public class StatsFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	/** the menu file describing the view specific menu structure */
	protected static String MENUFILE = "resources/menu.file";
	public static StatsFrame instance;
	protected StatsTabbedPane tabbedPane;
	protected DomainViewI view;
	
	public StatsFrame(DomainViewI v) {
		super("View stats");
		instance=this;
		view=v;
		ArrangementManager manager = new ArrangementManager();
		manager.add(view.getDaSet());
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
		
		//this.setSize(335, 260);
		pack();
//		System.out.println(this.getSize().toString());
		
		setLocationRelativeTo(null);
		setResizable(true);
		setVisible(true);

        this.addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) {
        		instance=null;
        	}
        	
        	public void windowActivated(WindowEvent e) { }
        	
        	public void windowClosed(WindowEvent e) { 
        		instance=null;
        	}

        	public void windowDeactivated(WindowEvent e) { }

        	public void windowDeiconified(WindowEvent e) { }

        	public void windowIconified(WindowEvent e) { }
        	
        	public void windowOpened(WindowEvent e) { }
		});
		

	}
	
	 public DomainViewI getView()
	 {
		 return view;
	 }
    
}
