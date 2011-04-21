package angstd;

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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import angstd.model.arrangement.io.GatheringThresholdsReader;
import angstd.model.arrangement.io.Pfam2GOreader;
import angstd.model.configuration.Configuration;
import angstd.model.configuration.ConfigurationReader;
import angstd.model.configuration.ConfigurationWriter;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.model.workspace.io.LastUsedWorkspaceImporter;
import angstd.model.workspace.io.LastUsedWorkspaceWriter;
import angstd.model.workspace.io.ProjectExporter;
import angstd.ui.AngstdUI;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;
import angstd.ui.wizards.WizardManager;


/**
 * 
 * ApplicationHandler actually initialized the AngstdUI frame, and
 * triggers the restoration of configuration settings and workspaces.
 * 
 * In rv. 174 we also check the java version as part of the start-up procedure.
 * 
 * 
 * @author Andrew Moore, Andreas Held
 * 
 */
public class ApplicationHandler {

	protected String workspace_dir = System.getProperty("user.home")+"/.angstd-workspace/";
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
			if (choice == 0)
				handelProjectExport();
			
			// delete previous stored workspace
			else if (choice == 1) {
				File workspaceFile = new File (Configuration.getInstance().getWorkspaceDir()+"/lastusedworkspace.file");
	        	if (workspaceFile.exists())
	        		workspaceFile.delete();
			}
			// cancel
			else if (choice == 2)
				return;
		}
			
		// remove lockfile
		Configuration.getInstance().removeLockFile();
		
		if (AngstdUI.getInstance().isShowing())
			AngstdUI.getInstance().dispose();
		
		System.exit(0);		
	}

	
	private void handelProjectExport() {
		
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
				
				MessageUtil.showWarning("The Default Project cannot be exported. Please export to different name.");
				
				// first rename project
				String newName = WizardManager.getInstance().selectRenameWizard(project.getTitle(), project.getTypeName(), project);
        		if(newName == null)  // canceled
        			return;
        	
        		WorkspaceManager.getInstance().changeElementName(project, newName);  

        		// then export project under that name
				ProjectExporter.write(new File(workspaceDir), project, newName);
				
				// ... and then recreate an empty Default Project (incase the exiting is stopped)
				WorkspaceManager.getInstance().addProject("Default Project", false);
				
				continue;
			}
			// project dir exists and we have not 'always overwrite'... 
        	else if (projectDir.exists() && 
        			(!overwriteAll) && 
        			(!Configuration.getInstance().getOverwriteProjects())) {
        		
				Object[] options = {"Yes", "No", "Overwrite all"};
				int choice = 0; 
				
				// ask if we are to overwrite...
				choice = MessageUtil.show3ChoiceDialog("Project "+project.getTitle()+" exists in workspace. Do you want to overwrite?", options);
        		
        		//... if not, 
        		if (choice == 1) {
        			// get new name for project
	        		String newTitle = WizardManager.getInstance().selectRenameWizard(project.getTitle(), project.getTypeName(), project);
	        		if(newTitle == null)  // canceled
	        			return;
	        		
	        		// change the name in the workspace
	        		WorkspaceManager.getInstance().changeElementName(project, newTitle);  			
        		}
        		// otherwise dont ask again (always overwrite)
        		else if(choice == 2) {
        			overwriteAll = true;
        		}
        		
        	}
			// then export (check if it worked)
			if (!ProjectExporter.write(project)) {
				if (!MessageUtil.showDialog("Unable to export "+project.getTitle()+". Continue?"))
					return;
			}
				
		} // end of each project
		
		
		LastUsedWorkspaceWriter.write();
	} 


	/**
	 * 
	 */
	public void start() {
		startUpProgress = new StartupPage();
		
		startUpProgress.setProgress("Loading AnGSTD", 5);
		initPreferences();
		
		startUpProgress.setProgress("Initiating workspace", 25);
		initWorkspaceDir();
		
		startUpProgress.setProgress("Checking Java version", 50);
//		startUpProgress.setProgress("Paint the main frame", 60);
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
			String msg = "Some of Angstd's functionalities will not work correctly\n";
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


		
		startUpProgress.setProgress("Have fun... ", 100);
		startUpProgress.dispose();
		
		
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
	 * Helper method checking if the Angstd workspace already exists.
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
			MessageUtil.showWarning(startUpProgress, "The default workspace is in use. Please close all AnGSTD instances and try again. ");
			System.exit(0);
		}
	}
	
	/**
	 * Helper method initializing the main window
	 */
	protected void initGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				AngstdUI.getInstance(); 
				
			 }
		 });
	}

	private class StartupPage extends Frame {
		private static final long serialVersionUID = 1L;
		
		//private static final String LOGOPATH = "ui/resources/Logo2.jpg";
		private static final String LOGOPATH = "ui/resources/angstd_logo4.png";
		
		protected JProgressBar progressBar;
		
		public StartupPage() {
			// init components
			JPanel startupPanel = new JPanel(new BorderLayout());
			startupPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray, 2));
			
			// load and display logo
			InputStream is = this.getClass().getResourceAsStream(LOGOPATH);
			ImageIcon logo = null;
			try {
				logo = new ImageIcon(ImageIO.read(is));
				startupPanel.add(new JLabel(logo), BorderLayout.CENTER);
			} catch (IOException e) {
				System.out.println("Failed to load logo image");
			}
			
			// create and display progressbar
			progressBar = new JProgressBar(0, 100);
			progressBar.setIndeterminate(false);
			progressBar.setStringPainted(true);
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}


