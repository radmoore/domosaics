package angstd.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import angstd.ApplicationHandler;
import angstd.model.configuration.Configuration;
import angstd.ui.docking.AngstdDesktop;
import angstd.ui.io.menureader.DefaultMenuActionManager;
import angstd.ui.io.menureader.JMenuBarFactory;
import angstd.ui.io.menureader.MenuActionManager;
import angstd.ui.io.menureader.MenuReader;
import angstd.ui.views.view.View;

/**
 * The user interface class showing the Angstd main frame. <br>
 * When calling the constructor which follows the singleton pattern
 * using getInstance(), the main window builds up and the application is 
 * ready to run. 
 * <p>
 * A docking desktop is added to the main frame using the external 
 * library VLDocking. Within this docking desktop the workspace and the view 
 * space are managed (if you are interested in the details take a look at 
 * {@link AngstdDesktop}). 
 * <p>
 * Also an important point here is the menu creation. The {@link JMenuBarFactory}
 * class is used to automatically create menus from xml formatted files.
 * The menu items are defined by actions. To get control over the status 
 * of an action, an {@link MenuActionManager} is used. Therefore it is possible
 * to manually alter a check box status if an invalid option was triggered.
 * For details on extending menus see {@link JMenuBarFactory} and
 * {@link MenuReader}. For just adding new entrieslook into a 
 * xml formatted menu file within a resource folder.
 * <p>
 * If you are searching information on how to add views into the mainframe 
 * the {@link ViewHandler} will be of interest to you.
 * 
 * 
 * @author Andreas Held
 *
 */
public class AngstdUI extends JFrame implements WindowListener{
	private static final long serialVersionUID = 1L;
	
	/** the location of the main menu file read by the menuReader */
	protected static final String MENUFILE = "resources/mainmenu.file";
	
	/** the instance of the object itself (singleton pattern) */
	protected static AngstdUI instance;
	
	/** the actionManager to manipulate the main menu entries */
	protected DefaultMenuActionManager actionManager;
	
	/** the Angstd desktop managing the workspace and the view dockings */
	protected AngstdDesktop desktop;

	private JPanel glassPane;
	
	/**
     * Constructor which creates a new AngstdUI instance. 
     * This is a protected constructor to support the singleton pattern. 
     * To get an instance of AngstdUI, use the static {@link #getInstance()}
     * method. 
     */
    protected AngstdUI() {
		super("[AnGSTD]");
		 
		// add the docking desktop (the workspace is created in here)
		desktop = new AngstdDesktop();
		getContentPane().add(desktop);

		// set frame attributes (e.g. fullscreen)
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width, screenSize.height);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //.EXIT_ON_CLOSE);
		addWindowListener(this);
		setVisible(true);
		
		// for disabling input
		glassPane = new JPanel();
	    glassPane.setOpaque(false);
	    glassPane.setLayout(null);
	    // catch all mouse actions
	    glassPane.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
	    this.setGlassPane(glassPane);
		
		
		// create MenuBar after frame is visible
		actionManager = new DefaultMenuActionManager();
		try {
			URL menuURL = this.getClass().getResource(MENUFILE);
			setJMenuBar(JMenuBarFactory.createMenuBar(menuURL, actionManager));
		} catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
			JOptionPane.showMessageDialog(this, e.toString(), "Menu loading FAILED!", JOptionPane.ERROR_MESSAGE); 
		} 
	}
    
    /**
     * Returns the current AngstdUI instance. If not initialized, 
     * it creates a new instance.
     * <p>
     * This is the method you should use to initialize the UI.
     * 
     * @return 
     * 		AngstdUI instance
     */
	public static AngstdUI getInstance() {
		if(instance == null) {
			instance = new AngstdUI();
			//HelpManager.showHelpDialog("AngstdUI", "You can disable advice dialogs in the configuration menu.");
		}
		return instance;
	}   
   
    /**
     * Disables the main frame by enabled a glasspane
     * with an attached mouse listener
     */
	public void disableFrame(){
		glassPane.setVisible(true);
	}
	
	/**
     * Enables the main frame by disabling the glasspane
     */
	public void enableFrame() {
		glassPane.setVisible(false);
	}
	
	
    /**
     * Adds a new view to {@link AngstdDesktop}. This one is invoked 
     * by the ViewHandler and works only as wrapper for the addView method
     * within AngstdDesktop.
     * 
     * @param view
     * 		the view to be added
     * 		
     */
    public void addView(View view) {
    	desktop.addView(view);
    }
    
    /**
     * Removes a view from the {@link AngstdDesktop}. This one is invoked 
     * by the ViewHandler and works only as wrapper for the addView method
     * within AngstdDesktop.     
     */
    public void removeView() {
    	desktop.removeView();
    }
   
    /**
     * Wrapper around the rename method for views within AngstdDektop.
     * This method allows the the view name update within the dockable
     * view component.
     * 
     * @param newName
     * 		the new view name
     */
    public void changeViewName(String newName) {
    	desktop.changeViewName(newName);
    }

	
	public void windowClosed(WindowEvent arg0) {
		ApplicationHandler.getInstance().end();
	}
	
	public void windowActivated(WindowEvent arg0) {}

	public void windowClosing(WindowEvent arg0) {
		ApplicationHandler.getInstance().end();
	}
	
	public void windowDeactivated(WindowEvent arg0) {}

	public void windowDeiconified(WindowEvent arg0) {}

	public void windowIconified(WindowEvent arg0) {}

	public void windowOpened(WindowEvent arg0) {}
  
}
