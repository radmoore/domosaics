package angstd;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;
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
		// ask the user if he likes to export the current desktop bring restored in the next session
		boolean export = false;
		for (ProjectElement project : WorkspaceManager.getInstance().getProjects())
			if (project.getChildCount() > 0) {
				export = true;
				break;
			}
		
		
		if (export) {
			Object[] options = {"Yes", "No", "Cancel", "Restore original"};
			int choice = MessageUtil.show3ChoiceDialog("Restore workspace in next session?", options);
			
			if (choice == 0) { 		// save workspace
				for (ProjectElement project : WorkspaceManager.getInstance().getProjects())
					ProjectExporter.write(project);
				LastUsedWorkspaceWriter.write();
			} else if (choice == 1) {// delete previous stored workspace
				File workspaceFile = new File (Configuration.getInstance().getWorkspaceDir()+"/lastusedworkspace.file");
	        	if (workspaceFile.exists())
	        		workspaceFile.delete();
			} else if (choice == 2) {// cancel quit
				return;
			}
		}
		
//		if (export && MessageUtil.showDialog("Restore workspace in next session?")) {
//			for (ProjectElement project : WorkspaceManager.getInstance().getProjects())
//				ProjectExporter.write(project);
//			LastUsedWorkspaceWriter.write();
//		} else {
//			File workspaceFile = new File (Configuration.getInstance().getWorkspaceDir()+"/lastusedworkspace.file");
//        	if (workspaceFile.exists())
//        		workspaceFile.delete();
//		}

		if (AngstdUI.getInstance().isShowing())
			AngstdUI.getInstance().dispose();
		
		System.exit(0);

		// export the workspace then
		
//		for (View view : ViewHandler.getInstance().getViews()) {
//			if (view.isChanged()) 
//				if (MessageUtil.showDialog("Workspace contains unsaved changes, save them now?")) {
//					for (ProjectElement project : WorkspaceManager.getInstance().getProjects())
//						ProjectExporter.write(project);
//					break;
//				}		
//		}
		
		// export only saved views
		
	}
	
	/**
	 * 
	 */
	public void start() {
		startUpProgress = new StartupPage();
		
		startUpProgress.setProgress("Loading ANGSTD", 5);
		initPreferences();
		
		startUpProgress.setProgress("Initiating workspace", 25);
		initWorkspaceDir();
		
		startUpProgress.setProgress("Checking Java version", 60);
//		startUpProgress.setProgress("Paint the main frame", 60);
		initGUI();
		
		startUpProgress.setProgress("Checking Java version", 75);
		
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
		
		// END of workaround
//		startUpProgress.setProgress("Restore the publication data", 75);
		initLastWorkspace();

		//Reading the gathering thresholds
		startUpProgress.setProgress("Reading thresholds and annotation data ", 85);
		GatheringThresholdsReader.read();
		Pfam2GOreader.readFile();
		
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
		// check for configuration file 
		startUpProgress.setProgress("", 35);
//		startUpProgress.setProgress("Configure proclivities", 35);
		
		File configFile = new File(workspace.getAbsolutePath()+"/"+Configuration.CONFIGFILE);
		if (!configFile.exists())
			ConfigurationWriter.write(configFile);
		else
			ConfigurationReader.read(configFile);
		
		Configuration.getInstance().setWorkspaceDir(workspace_dir);
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
		
		private static final String LOGOPATH = "ui/resources/Logo2.jpg";
		
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


