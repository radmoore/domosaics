package domosaics.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import domosaics.ApplicationHandler;
import domosaics.localservices.hmmer3.ui.Hmmer3Frame;
import domosaics.model.configuration.Configuration;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.io.ProjectImporter;
import domosaics.ui.actions.ShowConfigurationAction;
import domosaics.ui.docking.DoMosaicsDesktop;
import domosaics.ui.io.menureader.DefaultMenuActionManager;
import domosaics.ui.io.menureader.JMenuBarFactory;
import domosaics.ui.io.menureader.MenuActionManager;
import domosaics.ui.io.menureader.MenuReader;
import domosaics.ui.tools.configuration.ConfigurationFrame;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.view.View;
import domosaics.ui.wizards.WizardManager;
import domosaics.webservices.RADS.RADSService;
import domosaics.webservices.RADS.ui.RADSFrame;
import domosaics.webservices.RADS.ui.RADSScanPanel;
import domosaics.webservices.interproscan.ui.AnnotatorFrame;




/**
 * The user interface class showing the DoMosaicS main frame. <br>
 * When calling the constructor which follows the singleton pattern
 * using getInstance(), the main window builds up and the application is 
 * ready to run. 
 * <p>
 * A docking desktop is added to the main frame using the external 
 * library VLDocking. Within this docking desktop the workspace and the view 
 * space are managed (if you are interested in the details take a look at 
 * {@link DoMosaicsDesktop}). 
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
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class DoMosaicsUI extends JFrame implements WindowListener{
	private static final long serialVersionUID = 1L;
	
	/** the location of the main menu file read by the menuReader */
	protected static final String MENUFILE = "resources/mainmenu.file";

	/** the instance of the object itself (singleton pattern) */
	protected static DoMosaicsUI instance;
	
	/** the actionManager to manipulate the main menu entries */
	protected DefaultMenuActionManager actionManager;
	
	/** the DoMosaicS desktop managing the workspace and the view dockings */
	protected DoMosaicsDesktop desktop;

	private JPanel glassPane;
	
	private JToolBar toolBar;
	
	private Hmmer3Frame hmmer3 = null;
	
	private AnnotatorFrame annotatorFrame = null;
	
	private JFrame radsFrame = null;
	
	//protected ConfigurationFrame configFrame = null;
	
	
	
	/**
     * Constructor which creates a new DoMosaicsUI instance. 
     * This is a protected constructor to support the singleton pattern. 
     * To get an instance of DoMosaicsUI, use the static {@link #getInstance()}
     * method. 
     */
    protected DoMosaicsUI() {
		super("DoMosaicS");
		 
		// add the docking desktop (the workspace is created in here)
		desktop = new DoMosaicsDesktop();
		getContentPane().add(desktop);

		// set frame attributes (e.g. fullscreen)
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width, screenSize.height);
		//Next line create a bug under MAC OS
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
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
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			JOptionPane.showMessageDialog(this, e.toString(), "Menu loading FAILED!", JOptionPane.ERROR_MESSAGE); 
		}
		// create ToolBar
		toolBar = new JToolBar();
		initToolBar();
		add(toolBar, BorderLayout.PAGE_START);
		
	}
    
    /**
     * Returns the current DoMosaicsUI instance. If not initialized, 
     * it creates a new instance.
     * <p>
     * This is the method you should use to initialize the UI.
     * 
     * @return 
     * 		DoMosaicsUI instance
     */
	public static DoMosaicsUI getInstance() {
		if(instance == null) {
			instance = new DoMosaicsUI();
			//HelpManager.showHelpDialog("DoMosaicsUI", "You can disable advice dialogs in the configuration menu.");
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
     * Adds a new view to {@link DoMosaicsDesktop}. This one is invoked 
     * by the ViewHandler and works only as wrapper for the addView method
     * within DoMosaicsDesktop.
     * 
     * @param view
     * 		the view to be added
     * 		
     */
    public void addView(View view) {
    	desktop.addView(view);
    }
    
    /**
     * Removes a view from the {@link DoMosaicsDesktop}. This one is invoked 
     * by the ViewHandler and works only as wrapper for the addView method
     * within DoMosaicsDesktop.     
     */
    public void removeView() {
    	desktop.removeView();
    }
   
    // TODO: consider moving into seperate class and
    // making use of AbstractMenuActions
    private void initToolBar() {
    	
    	ImageIcon newProjectIcon = null, 
    	loadFastaIcon = null,
    	openProjectIcon = null, 
    	saveProjectIcon = null, 
    	importViewIcon = null, 
    	exportViewIcon = null,
    	hmmscanIcon = null, 
    	iprscanIcon = null,
    	radsIcon = null,
    	settingsIcon = null,
    	treeIcon = null,
    	domainTreeIcon = null;
    	
		
    	InputStream is;

    	// create icons
    	try {
    		is = this.getClass().getResourceAsStream("resources/icons/newproject.png");
    		newProjectIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/testLoadFasta.png");
    		loadFastaIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/openproject.png");
    		openProjectIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/saveproject.png");
    		saveProjectIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/importview.png");
    		importViewIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/exportview.png");
    		exportViewIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/hmmscan.png");
    		hmmscanIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/iprscan.png");
    		iprscanIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/rads.png");
    		radsIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/tree.png");
    		treeIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/domtree.png");
    		domainTreeIcon = new ImageIcon(ImageIO.read(is));
    		
    		is = this.getClass().getResourceAsStream("resources/icons/settings.png");
    		settingsIcon = new ImageIcon(ImageIO.read(is));
    	}
    	catch (Exception e) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
    	}
    	
    	// new project
    	JButton newProject = new JButton();
    	newProject.setIcon(newProjectIcon);
    	newProject.setToolTipText("Create a new project");
    	newProject.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			WizardManager.getInstance().startImportDataWizard();
    		}
		});
    	toolBar.add(newProject);
    	

    	// new project
    	JButton loadFasta = new JButton();
    	loadFasta.setIcon(loadFastaIcon);
    	loadFasta.setToolTipText("Load a fasta file");
    	loadFasta.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			WizardManager.getInstance().startLoadFastaWizard();
    		}
		});
    	toolBar.add(loadFasta);
    	
    	
    	// open project
    	JButton openProject = new JButton();
    	openProject.setIcon(openProjectIcon);
    	openProject.setToolTipText("Open a saved project");
    	openProject.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			File file = FileDialogs.openChooseDirectoryDialog(DoMosaicsUI.getInstance());
    			if (file == null)
    				return;
    		
    			ProjectImporter.read(file);
    		}
		});
    	toolBar.add(openProject);
    	
    	// save project
    	JButton saveProject = new JButton();
    	saveProject.setIcon(saveProjectIcon);
    	saveProject.setToolTipText("Save a project");
    	saveProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WizardManager.getInstance().startSaveProjectWizard(null);
			}
		});
    	toolBar.add(saveProject);
    	
    	// import view
    	JButton importView = new JButton();
    	importView.setIcon(importViewIcon);
    	importView.setToolTipText("Import a view into a project");
    	importView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WizardManager.getInstance().startImportViewWizard(null);
			}
		});
    	toolBar.add(importView);
    	
    	// export view
    	JButton exportView = new JButton();
    	exportView.setIcon(exportViewIcon);
    	exportView.setToolTipText("Export a view from a project");
    	exportView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WizardManager.getInstance().startSaveViewWizard(null);
			}
		});
    	toolBar.add(exportView);
    	
    	toolBar.addSeparator();
    	// hmmscan
    	JButton hmmscan = new JButton();
    	hmmscan.setIcon(hmmscanIcon);
    	hmmscan.setToolTipText("Search for domains in sequences using local HMMSCAN installation");
    	hmmscan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				hmmer3 = Hmmer3Frame.getFrame();
				if (hmmer3 == null || !hmmer3.isVisible())
					hmmer3 = new Hmmer3Frame();
				else 
					hmmer3.setState(Frame.NORMAL);
			}
		});
    	toolBar.add(hmmscan);
    	
    	// iprscan
    	JButton iprscan = new JButton();
    	iprscan.setIcon(iprscanIcon);
    	iprscan.setToolTipText("Search for domains in sequences using iprscan (requires internet) ");
    	iprscan.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			
    			annotatorFrame = AnnotatorFrame.getFrame();
    			
    			if (annotatorFrame == null || !annotatorFrame.isVisible())
    				annotatorFrame = new AnnotatorFrame();
				else 
				annotatorFrame.setState(Frame.NORMAL);
				
			}
		});
    	toolBar.add(iprscan);
    	
    	// rads
    	JButton radsBtn = new JButton();
    	radsBtn.setIcon(radsIcon);
    	radsBtn.setToolTipText("Find arrangements similar to a query arrangement or sequence (requires internet) ");
    	radsBtn.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			radsFrame = RADSScanPanel.getCurrentRADSFrame();
    			if (radsFrame == null || !radsFrame.isVisible())
    				radsFrame = new RADSFrame();
    			else
    				radsFrame.setState(Frame.NORMAL);
    			
			}
		});
    	toolBar.add(radsBtn);
    	
    	toolBar.addSeparator();
    	
    	// domainTree
    	JButton domainTreeBtn = new JButton();
    	domainTreeBtn.setIcon(domainTreeIcon);
    	domainTreeBtn.setToolTipText("Merge a domain and sequence view");
    	domainTreeBtn.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
				WizardManager.getInstance().startCreateDomTreeWizard();
			}
		});
    	toolBar.add(domainTreeBtn);
    	
    	// Tree
    	JButton treeBtn = new JButton();
    	treeBtn.setIcon(treeIcon);
    	treeBtn.setToolTipText("Build a tree based on domains or sequences");
    	treeBtn.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
				WizardManager.getInstance().startCreateTreeWizard();
			}
		});
    	toolBar.add(treeBtn);
    	
    	
    	toolBar.addSeparator();
    	
    	// configuration
    	JButton settings = new JButton();
    	settings.setIcon(settingsIcon);
    	settings.setToolTipText("Settings");
//    	settings.setAction(new ShowConfigurationAction());
    	settings.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			if (Configuration.getInstance().getFrame() != null)
    				Configuration.getInstance().getFrame().dispose();
    			Configuration.getInstance().setFrame(new ConfigurationFrame());
    			/*configFrame = Configuration.getInstance().getFrame();
    			
    			if (configFrame == null)
    				configFrame = new ConfigurationFrame();
    			
    			else if (configFrame.getExtendedState() == Frame.ICONIFIED)
    				configFrame.setExtendedState(Frame.NORMAL);
    			
    			else
    				configFrame.setVisible(true);
    				*/

    		}	
		});
    	toolBar.add(settings);
    	
    	toolBar.setFloatable(false);
    	toolBar.setRollover(true);
    }
    
    /**
     * Wrapper around the rename method for views within DoMosaicsDesktop.
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
