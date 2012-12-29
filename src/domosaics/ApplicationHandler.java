package domosaics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.WorkspaceManager;
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
 * @author Andrew Moore, Andreas Held
 * 
 */
public class ApplicationHandler {

	protected String workspace_dir = System.getProperty("user.home")+"/domosaics-workspace";
	protected static ApplicationHandler instance;
	
	protected StartupPage startUpProgress;
	
	
	public static ApplicationHandler getInstance() {
		if (instance == null)
			instance = new ApplicationHandler();
		return instance;
	}
	
	public void end () {
		
		if (Configuration.getInstance().isServiceRunning()) {
			boolean close = MessageUtil.showDialog("You are running a service and will loose the results if you close. Are you sure?");
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
				choice = MessageUtil.show3ChoiceDialog("Restore workspace in next session?", options);
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
				File workspaceFile = new File (Configuration.getInstance().getWorkspaceDir()+"/lastusedworkspace.file");
				if (workspaceFile.exists()) {
					if (!workspaceFile.delete()) {
						MessageUtil.showWarning("Could not delete workspace file");
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
		ConfigurationWriter.write(Configuration.getInstance().getConfigFile());
		
		// remove lockfile
		Configuration.getInstance().removeLockFile();
		
		if (DoMosaicsUI.getInstance().isShowing())
			DoMosaicsUI.getInstance().dispose();
		
		Configuration.getLogger().info("INFO: closing DoMosaicS");
		LastUsedWorkspaceWriter.write();
		System.exit(0);		
	}

	
	private boolean handelProjectExport() {
		
    	String workspaceDir = Configuration.getInstance().getWorkspaceDir();
    	boolean overwriteAll = false;
    	
		for (ProjectElement project : WorkspaceManager.getInstance().getProjects()) {
			// only export projects that have views
			if (project.getChildCount() < 1)
				continue;
			
			String projectDirPath = workspaceDir+"/"+project.getTitle();
        	File projectDir = new File(projectDirPath);
			
        	
			// default project is not exported
			if (project.getTitle().equals("Default Project")) {
				
				// skip option?
				if (MessageUtil.showDialog("The Default Project cannot be exported. Export to a different name?")) {
				
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
        		
				Object[] options = {"Yes", "No", "Overwrite all", "Cancel"};
				int choice = 0; 
				
				// ask if we are to overwrite...
				choice = MessageUtil.show3ChoiceDialog("Project "+project.getTitle()+" exists in workspace. Do you want to overwrite?", options);
        		
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
				if (!MessageUtil.showDialog("Unable to export "+project.getTitle()+". Continue?"))
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
		
		startUpProgress.setProgress("Loading DoMosaicS", 5);
		initPreferences();
		
		startUpProgress.setProgress("Initiating workspace", 25);
		initWorkspaceDir();
		
		startUpProgress.setProgress("Checking Java version", 50);
		initGUI();
		
		startUpProgress.setProgress("Checking Java version", 60);
		
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
		erronousJava.put("1.6.0_18", true);
		erronousJava.put("1.6.0_19", true);
		erronousJava.put("1.6.0_20", true);
		erronousJava.put("Sun", true);
		if (erronousJava.containsKey(jversion)) {
			startUpProgress.dispose();
			String msg = "Some of DoMosaicS's functionalities will not work correctly\n";
			msg += "with your version of Java. Please update your \nJava Runtime Environment to 1.6.0_21.";
			msg += "\nJava version: "+ jversion + " \nVendor: "+ jvendor;
			JOptionPane.showMessageDialog(startUpProgress, msg);
			//System.exit(-1);
		}	
		
		//Reading the gathering thresholds
		startUpProgress.setProgress("Reading data files", 70);
		GatheringThresholdsReader.read();
		Pfam2GOreader.readFile();
		
		
		// END of workaround
		startUpProgress.setProgress("Restoring projects", 85);
		initLastWorkspace();


		
		startUpProgress.setProgress("Enjoy... ", 100);
		startUpProgress.dispose();
		DoMosaicsUI.getInstance().enableFrame();
		
		
	}
	
	
	protected void initLastWorkspace() {
    	// check for the project within the workspace directory, create it if necessary
    	String workspaceDir = Configuration.getInstance().getWorkspaceDir();
    	
    	// get the workspace file 
    	File workspaceFile = new File (workspaceDir+"/lastusedworkspace.file");
    	if (!workspaceFile.exists()) 
    		return;
    	
    	// Import the elements from the workspace file
    	LastUsedWorkspaceImporter.initWorkspace(workspaceFile);
//    	ProjectImporter.initWorkspace();
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
	 * Helper method checking if the DoMosaicS workspace already exists.
	 */
	protected void initWorkspaceDir() {
		File workspace = new File(workspace_dir);
		// if default directory does not exist choose and create a workspace dir
		if (!workspace.exists()) {
			workspace = WizardManager.getInstance().showWorkingDirectoyWizard(startUpProgress, workspace_dir);
			if (workspace == null) // user aborted wizard
				System.exit(0);
			workspace.mkdir();
		}
		
		// TODO In next version 
//		else {
//			// check if alread in use
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
		
		File configFile = new File(workspace.getAbsolutePath()+"/"+Configuration.CONFIGFILE);
		if (!configFile.exists())
			ConfigurationWriter.write(configFile);
		else
			ConfigurationReader.read(configFile);
		
		Configuration.getInstance().setWorkspaceDir(workspace_dir);
		if (!Configuration.getInstance().workspaceInUse()) { 
			Configuration.getInstance().setLockFile();
		}
		else {
			MessageUtil.showWarning(startUpProgress, "The default workspace is in use. Please close all DoMosaicS instances and try again. ");
			System.exit(0);
		}
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

	private class StartupPage extends Frame {
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
			} catch (IOException e) {
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
				Configuration.getLogger().debug(e.toString());
			}
		}
	}
}


