package domosaics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.tools.Diagnostic;

import org.apache.log4j.Logger;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.RGBColor;

import domosaics.model.arrangement.io.GatheringThresholdsReader;
import domosaics.model.arrangement.io.Pfam2GOreader;
import domosaics.model.configuration.Configuration;
import domosaics.model.configuration.ConfigurationReader;
import domosaics.model.configuration.ConfigurationWriter;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.io.LastUsedWorkspaceImporter;
import domosaics.model.workspace.io.LastUsedWorkspaceWriter;
import domosaics.model.workspace.io.ProjectExporter;
import domosaics.model.workspace.io.ProjectImporter;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.DocumentationHandler;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.wizards.WizardManager;

/**
 * 
 * ApplicationHandler actually initialized the DoMosaicsUI frame, and
 * triggers the restoration of configuration settings and workspaces.
 * 
 * In rv. 174 we also check the java version as part of the start-up procedure.
 * 
 * 
 * @author Andreas Held
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 * 
 */
public class ApplicationHandler {

	protected static ApplicationHandler instance;	
	protected StartupPage startUpProgress;
	
	public File configFile = new File(Configuration.getDefaultConfig());
	
	
	public static ApplicationHandler getInstance() {
		if (instance == null)
			instance = new ApplicationHandler();
		return instance;
	}
	
	public void end () {

		// dispose the configuration frame  
		// (to be sure that dialogs do not appear behind the tool frame, effectively freezing DoMosaics)
		// try catch as there is no frame if the configuration window has never been opened
		try {
			if ( Configuration.getInstance().getFrame().isShowing() )
				Configuration.getInstance().getFrame().dispose();
			
		} 
		catch ( Exception e) { }
		
		if (Configuration.getInstance().isServiceRunning()) {
			boolean close = MessageUtil.showDialog(DoMosaicsUI.getInstance(),"You are running a service and will loose the results if you close. Are you sure?");
			if (!close)
				return;
		}
			
		// only attempt to export if at least one project has a view
		boolean export = false;
		for (ProjectElement project : WorkspaceManager.getInstance().getProjects())
			if (project.getChildCount() > 0) {
				export = true;
				break;
			}
		
		if (export) {
			int choice = 0;
			// if the user has not set default save in configurator
			// ask (defaults to save)
			if (!(Configuration.getInstance().saveOnExit())) {
				Object[] options = {"Yes", "No", "Cancel"};
				choice = MessageUtil.show3ChoiceDialog(DoMosaicsUI.getInstance(),"Restore workspace in next session?", options);
				Configuration.getInstance().setSaveOnExit((boolean)(choice==0));
			}
			
			// save workspace
			if (choice == 0){
				 if (!handelProjectExport()) {// if something goes wrong during export, do not exit program
					 return; 
				 }
			}
			// delete previous stored workspace and exit
			// (that is, do not save state)
			else if (choice == 1) {
				File workspaceFile = new File (Configuration.getInstance().getWorkspaceDir()+File.separator+"lastusedworkspace.file");
				if (workspaceFile.exists()) {
					if (!workspaceFile.delete()) {
						MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Could not delete workspace file");
					}
				}
				Configuration.getInstance().removeLockFile();
				System.exit(0);
			}
			// cancel
			else if (choice == 2) {
				return;
			}
		}

		// Save Configuration
		ConfigurationWriter.write();
		
		// remove lockfile
		Configuration.getInstance().removeLockFile();
		
		if (DoMosaicsUI.getInstance().isShowing())
			DoMosaicsUI.getInstance().dispose();
		
		LastUsedWorkspaceWriter.write();
		logProgramEnd();
		System.exit(0);		
	}

	
	private boolean handelProjectExport() {
		
    	String workspaceDir = Configuration.getInstance().getWorkspaceDir();
    	boolean overwriteAll = false;
    	
		for (ProjectElement project : WorkspaceManager.getInstance().getProjects()) {
			// only export projects that have views
			if (project.getChildCount() < 1)
				continue;
			
			String projectDirPath = workspaceDir+File.separator+project.getTitle();
        	File projectDir = new File(projectDirPath);
			
        	
			// default project is not exported
			if (project.getTitle().equals("Default Project")) {
				
				// skip option?
				if (MessageUtil.showDialog(DoMosaicsUI.getInstance(),"The Default Project cannot be exported. Export to a different name?")) {
				
					// first rename project
					String newName = WizardManager.getInstance().renameWizard(project.getTitle(), project.getTypeName(), project);
        			if(newName == null)  // canceled
        				return false;
        	
        			WorkspaceManager.getInstance().changeElementName(project, newName);  

        			// then export project under that name
        			ProjectExporter.write(new File(workspaceDir), project, newName);
				
        			// ... and then recreate an empty Default Project (incase the exiting is stopped)
        			WorkspaceManager.getInstance().addProject("Default Project", false);
        		
				}
				continue;
			}
			// project dir exists and we have not 'always overwrite'... 
        	else if (projectDir.exists() && 
        			(!overwriteAll) && 
        			(!Configuration.getInstance().getOverwriteProjects())) {
        		
				Object[] options = {"Yes", "No", "Always overwrite", "Cancel"};
				int choice = 0; 
				
				// ask if we are to overwrite...
				choice = MessageUtil.show3ChoiceDialog(DoMosaicsUI.getInstance(),"Project "+project.getTitle()+" exists in workspace. Do you want to overwrite?", options);
    			Configuration.getInstance().setOverwriteProjects((boolean)(choice==2));
    			
        		//... if not, 
        		if (choice == 1) {
        			// get new name for project
	        		String newTitle = WizardManager.getInstance().renameWizard(project.getTitle(), project.getTypeName(), project);
	        		if(newTitle == null)  // canceled
	        			return false;
	        		
	        		// change the name in the workspace
	        		WorkspaceManager.getInstance().changeElementName(project, newTitle);  			
        		}
        		// otherwise dont ask again (always overwrite)
        		else if(choice == 2) {
        			overwriteAll = true;
            	}
        		else if (choice == 3) {
        			return false;
        		}
        	}
			// then export (check if it worked)
			if (!ProjectExporter.write(project)) {
				if (!MessageUtil.showDialog(DoMosaicsUI.getInstance(),"Unable to export "+project.getTitle()+". Continue?"))
					return false;
			}
				
		} // end of each project
		
		
//		LastUsedWorkspaceWriter.write();
		return true;
	} 


	/**
	 * 
	 */
	public void start() {
		startUpProgress = new StartupPage();
		
		startUpProgress.setProgress("Loading DoMosaics", 5);
		initPreferences();
		
		/**
		 * TODO
		 * This is a hack related to Java Bug 
		 * #6880336 (1.6.0_18 - 1.6.0_20) that
		 * renders the annotator non-functional.
		 * Starting 1.6.0_21 the annotator works fine again.
		 * (ADM)  
		 */
		String jversion = System.getProperty("java.version");
		String jvendor = System.getProperty("java.vendor");
		HashMap<String, Boolean> erronousJava = new HashMap<String, Boolean>();
		erronousJava.put("1.4", true);
		erronousJava.put("1.5", true);
		erronousJava.put("1.6", true);
		erronousJava.put("Sun", true);
		if (erronousJava.containsKey(jversion.substring(0,3))) {
			startUpProgress.dispose();
			String msg = "DoMosaics' functionalities will not work correctly\n";
			msg += "with your current version of Java. Please update your\n";
	    	msg += "Java Runtime Environment to 1.7+";
			msg += "\nJava version: "+ jversion + " \nVendor: "+ jvendor;
			JOptionPane.showMessageDialog(startUpProgress, msg);
			//System.exit(-1);
		}	

		startUpProgress.setAlwaysOnTop(false);
		startUpProgress.setProgress("Initiating workspace", 25);
		initWorkspaceDir();
		startUpProgress.setAlwaysOnTop(true);
		
		startUpProgress.setProgress("Checking Java version", 50);
		initGUI();
		
		startUpProgress.setProgress("Checking Java version", 60);
		
		//Reading the gathering thresholds
		startUpProgress.setProgress("Reading data files", 70);
		startUpProgress.setAlwaysOnTop(false);
		GatheringThresholdsReader.read();
		Pfam2GOreader.readGOFile();
		startUpProgress.setAlwaysOnTop(true);
		
		// END of workaround
		startUpProgress.setProgress("Restoring projects", 85);
		startUpProgress.setAlwaysOnTop(false);
		initLastWorkspace();
		startUpProgress.setAlwaysOnTop(true);

		startUpProgress.setProgress("Enjoy... ", 100);
		startUpProgress.dispose();
		DoMosaicsUI.getInstance().enableFrame();
		
	}
	
	
	protected void initLastWorkspace() {
    	// check for the project within the workspace directory, create it if necessary
    	String workspaceDir = Configuration.getInstance().getWorkspaceDir();
//        System.out.println(workspaceDir);
    	
    	// get the workspace file 
    	File workspaceFile = new File (workspaceDir+File.separator+"lastusedworkspace.file");
    	if (!workspaceFile.exists()) { 
    		File projectDir = new File(workspaceDir);
    		String[] projectFiles = projectDir.list();
    		for (String elem : projectFiles) {
        		File elemDir = new File(workspaceDir+File.separator+elem);
    			if(elemDir.isDirectory() && !elem.equals("logs"))
    				ProjectImporter.read(elemDir);
    		}
    	} else
    	{
    		// Import the elements from the workspace file
    		LastUsedWorkspaceImporter.initWorkspace(workspaceFile);
    	}
	}
	
	/**
	 * Helper method initializing the preferences such as the default locale
	 * and the configuration, workspace directory and so on.
	 */
	protected void initPreferences() {
		// set locale to english
	    Locale.setDefault(Locale.ENGLISH);
	}
	
	/**
	 * Helper method checking if the DoMosaics workspace already exists.
	 */
	protected void initWorkspaceDir() {

		boolean workspaceCreated = false;
		File workspace = null;		
		File configFile = Configuration.getInstance().getConfigFile();
		
		// we do not have a configuration file
		if ( !configFile.exists() ) {
				
			// if default directory does not exist choose and create a workspace dir
			while (!workspaceCreated) {
				workspace = WizardManager.getInstance().showWorkingDirectoyWizard(
					startUpProgress, Configuration.DEF_HOMEFOLDER_LOCATION);

				// user cancelled
				if ( workspace == null )
					System.exit(0);
				
				if( !workspace.exists() )
					workspaceCreated=workspace.mkdir();
				else
					workspaceCreated=true;
			}
			
			Configuration.getInstance().setWorkspaceDir(workspace.getPath());

			// create the program dir
			initProgramDir();
			
			// create a new config file
			ConfigurationWriter.write();
		} 
		else {
			ConfigurationReader.read();
			workspace = new File(Configuration.getInstance().getWorkspaceDir());
			if( !workspace.exists() )
				workspaceCreated=workspace.mkdir();
			else
				workspaceCreated=true;
			while (!workspaceCreated) {
				workspace = WizardManager.getInstance().showWorkingDirectoyWizard(
						startUpProgress, Configuration.DEF_HOMEFOLDER_LOCATION);
				if( !workspace.exists() )
					workspaceCreated=workspace.mkdir();
				else
					workspaceCreated=true;
				Configuration.getInstance().setWorkspaceDir(workspace.getPath());
				ConfigurationWriter.write();						
			}
		}
		
		// TODO In next version 
//		else {
//			// check if already in use
//			boolean choose = MessageUtil.showDialog("The default workspace is in use. Would you like to choose a new workspace?");
//			if (choose){
//				workspace = WizardManager.getInstance().showWorkingDirectoyWizard(startUpProgress, workspace_dir);
//				if (workspace == null) // user aborted wizard
//					System.exit(0);
//				workspace.mkdir();
//			}
//			else{
//				System.exit(0);
//			}
//			
//		}
		// check for configuration file 
		startUpProgress.setProgress("", 35);
//		startUpProgress.setProgress("Configure proclivities", 35);
		
		if (!Configuration.getInstance().workspaceInUse()) { 
			Configuration.getInstance().setLockFile();
		}
		else {
			boolean removeLock = MessageUtil.showDialog(startUpProgress, "Your workspace seems to be in use. " +
					"We can try to remove the lock file.\n" +
					"Please note that this is only recommended if DoMosaics is not running - \n" +
					"running two instances can corrupt your project folders. Remove lock?");
			
			if ( removeLock )
				Configuration.getInstance().removeLockFile();
			else
				System.exit(1);
			
			Configuration.getInstance().setLockFile();
		}
	}
	
	/**
	 * Creates a program dir if it does not exsit, and copies
	 * the help files to this location. Finally, creates the config
	 * file in this folder	
	 */
	protected void initProgramDir() { 
		File programDir = new File(Configuration.DEF_PROGRAM_FOLDER);
		
		if ( !programDir.exists() ) {
			// could not create the program dir, inform and close
			if (!programDir.mkdir()) {
				MessageUtil.showWarning(startUpProgress, "Unable to create Program directory in "+Configuration.DEF_PROGRAM_FOLDER+". Exiting.");
				System.exit(1);
			}
		}	
		
		boolean worked = DocumentationHandler.extractDocumentation();
		if (!worked)
			MessageUtil.showWarning(startUpProgress, "Unable to extract documentation!");
	}
	
	
	/**
	 * Helper method initializing the main window
	 */
	protected void initGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				DoMosaicsUI domosaics = DoMosaicsUI.getInstance();
				domosaics.disableFrame();
				
			 }
		 });
	}
	
	
	private void logProgramEnd() {
		Date cDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String cDateStr = sdf.format(cDate);
		
		
		if ( Configuration.isDebug() ) {
			Configuration.getLogger().debug("=============================================================");
			Configuration.getLogger().debug("*** CLOSING DOMOSAICS ***");
			Configuration.getLogger().debug("[ "+ cDateStr+" ]");
			Configuration.getLogger().debug("=============================================================");
		}
		else {
			Configuration.getLogger().info("=============================================================");
			Configuration.getLogger().info("*** CLOSING DOMOSAICS ***");
			Configuration.getLogger().info("[ "+ cDateStr+" ]");
			Configuration.getLogger().info("=============================================================");
		}
	}
	
	

	private class StartupPage extends JFrame {
		private static final long serialVersionUID = 1L;
		
		private static final String LOGOPATH = "ui/resources/DoMosaics_splash_1_small.png";
		
		protected JProgressBar progressBar;
		
		public StartupPage() {
			// init components
			JPanel startupPanel = new JPanel(new BorderLayout());
			
			// load and display logo
			InputStream is = this.getClass().getResourceAsStream(LOGOPATH);
			ImageIcon logo = null;
			try {
				logo = new ImageIcon(ImageIO.read(is));
				startupPanel.add(new JLabel(logo), BorderLayout.CENTER);
			} 
			catch (IOException e) {
				if (Configuration.getReportExceptionsMode(true))
					Configuration.getInstance().getExceptionComunicator().reportBug(e);
				else			
					Configuration.getLogger().debug(e.toString());
			}
			
			// create and display progressbar
			progressBar = new JProgressBar(0, 100);
			progressBar.setForeground(new Color(30, 108, 182));
			progressBar.setBackground(new Color(213, 212, 212));
			progressBar.setIndeterminate(false);
			progressBar.setStringPainted(true);
			progressBar.setBorderPainted(false);
			startupPanel.add(progressBar, BorderLayout.SOUTH);

			// set up the frame
			add(startupPanel);
			setUndecorated(true);
			pack();
			setLocationRelativeTo(null);
			setAlwaysOnTop(true);
			setVisible(true);

		}
	
		public void setProgress(final String msg, final int percent) {
		  SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	progressBar.setValue(percent);
	    			progressBar.setString(msg);
	            }
	        });
		  wait4me(500);
		}
		
		public void wait4me(long ms) {
			try {
				Thread.sleep(ms);
			} 
			catch (Exception e) {
				if (Configuration.getReportExceptionsMode(true))
					Configuration.getInstance().getExceptionComunicator().reportBug(e);
				else			
					Configuration.getLogger().debug(e.toString());
			}
		}
	}
	
	
	
}


