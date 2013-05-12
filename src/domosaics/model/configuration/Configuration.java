package domosaics.model.configuration;

import java.io.File;
import org.apache.log4j.Logger;
import domosaics.ui.tools.configuration.ConfigurationFrame;
import domosaics.ui.util.MessageUtil;
import domosaics.util.ExceptionComunicator;


/**
 * Configuration holds all specified URLs to look up nodes in trees and 
 * domains from an arrangement. The entries can be changed using the 
 * configuration module.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class Configuration {
	
	public static final String sep = System.getProperty("file.separator");
	
	public static final String DEF_HOMEFOLDER_LOCATION = System.getProperty("user.home");
	public static final String CONFIGFILE = DEF_HOMEFOLDER_LOCATION + sep +"domosaics_config.txt";
	public static final String DEF_GOOGLE_SEARCH = "http://www.google.com/search?q=XXX";
	public static final String DEF_PFAM_SEARCH = "http://pfam.sanger.ac.uk/family?acc=XXX";
	public static final String DEF_UNIPROT_SEARCH = "http://www.uniprot.org/uniprot/?query=XXX";
	public static final String DEF_EMAIL_ADDR = "";
	public static final String DEF_HMMPRESS_BIN = "";
	public static final String DEF_HMMSCAN_BIN = "";
	public static final String DEF_HMMERDB = "";
	
	public static final String LOCKFILE = ".lock";
	public static final String INSTALLATION_PATH = DEF_HOMEFOLDER_LOCATION + sep +"DoMosaics";
	public static final String DOCUMENTATION_PATH = INSTALLATION_PATH + sep + "docs";
	
	
	public static final boolean DEF_SHOW_ADVICES = false;
	public static final boolean DEF_SAVE_ON_EXIT = false;
	public static final boolean OVERWRITEPROJECTS = false;
	public static final boolean HELPIMPROVE = false;

	
	
	private boolean service_running = false;
	private static boolean debugState = false;
	private static boolean reportExceptions = false;
	private static boolean haveAsked = false;
	

	
	private ExceptionComunicator exceptionComunicator = null;
	

	protected String googleUrl;
	protected String pfamUrl; 
	protected String uniprotUrl;
	protected String emailAddr;
	protected String hmmScanBin, hmmPressBin;
	protected String hmmDB;
	protected String documentationPath;

	protected boolean showAdvices, saveWSOnExit, overwriteProjects, helpImprove, hasThrowException;
	
	protected static Configuration instance;
	protected static ConfigurationFrame frame;
	protected String workspace_dir;
	
	//protected boolean visible = false;
	protected static boolean nameRatherThanAcc = false;
	
	
	public Configuration() {
		restoreDefaults();
		if (exceptionComunicator == null)
			exceptionComunicator = ExceptionComunicator.getInstance();
	}

	public static boolean isNamePreferedToAcc() {
		return nameRatherThanAcc;
	}
	
	public static void setNamePreferedToAcc(boolean b) {
		nameRatherThanAcc = b;
	}
	
	public static Logger getLogger() {
		if ( !haveAsked ) {	
			boolean sendMes = MessageUtil.showDialog("A problem was detected - enable bug reporting?");
			if ( sendMes )
				Configuration.setReportExceptionsMode(true);
			haveAsked = true;
		}
    	return Logger.getLogger("domosaicslog");
	}
	
	public static void setDebug(Boolean debug) {
		debugState = debug;
	}
	
	public static void setReportExceptionsMode(Boolean report) {
		reportExceptions = report;
	}

	public static String getDefaultConfig() {
		return CONFIGFILE;
	}
	
	public static Boolean getReportExceptionsMode() {
		return reportExceptions;
	}
	
	public static boolean isDebug() {
		return debugState;
	}
	/*
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	*/
	public void restoreDefaults() {
		workspace_dir = DEF_HOMEFOLDER_LOCATION;
		googleUrl = DEF_GOOGLE_SEARCH;
		//ncbiUrl = DEF_NCBI_SEARCH; 
		pfamUrl = DEF_PFAM_SEARCH; 
		uniprotUrl = DEF_UNIPROT_SEARCH;
		emailAddr = DEF_EMAIL_ADDR;
		hmmScanBin = DEF_HMMSCAN_BIN;
		hmmPressBin = DEF_HMMPRESS_BIN;
		hmmDB = DEF_HMMERDB;
		showAdvices = DEF_SHOW_ADVICES;
		saveWSOnExit = DEF_SAVE_ON_EXIT;
		overwriteProjects = OVERWRITEPROJECTS;
		helpImprove = HELPIMPROVE;
		documentationPath = DOCUMENTATION_PATH;
		
	}
	
	public static Configuration getInstance() {
		getJarPath();
		if (instance == null)
			instance = new Configuration();
		return instance;
	}
	
	public static boolean hasInstance () {
		return (instance != null);
	}
	
	public void setFrame(ConfigurationFrame frame) {
		Configuration.frame = frame;
	}
	
	public ConfigurationFrame getFrame() {
		return frame;
	}
	
	/* Now attribute of ApplicationHandler
	 * 
	  public File getConfigFile() {
		return new File(workspace_dir+"/"+CONFIGFILE);
	}
	*/
	public void setWorkspaceDir(String workspace_dir) {
		this.workspace_dir = workspace_dir;
	}
	
	public String getWorkspaceDir() {
		return workspace_dir;
	}
	
	public boolean workspaceInUse() {
		File lockfile = getLockFile();
		return lockfile.exists();
	}
	
	public void setShowAdvices(boolean showAdvices) {
		this.showAdvices = showAdvices;
	}
	
	public boolean isShowAdvices() {
		return showAdvices;
	}
	
	public void setSaveOnExit(boolean save) {
		this.saveWSOnExit = save;
	}
	
	public boolean saveOnExit() {
		return saveWSOnExit;
	}
	
	public void setOverwriteProjects(boolean overwrite) {
		this.overwriteProjects = overwrite;
	}
	
	public boolean getOverwriteProjects() {
		return overwriteProjects;
	}
	
	public void setHelpImprove(boolean help) {
		Configuration.setReportExceptionsMode(help);
		this.helpImprove = help;
	}
	
	public boolean getHelpImprove() {
		return helpImprove;
	}
	
	public ExceptionComunicator getExceptionComunicator() {
		return exceptionComunicator;
	}
	
	/*
	/**
	 * TODO:
	 * There are some problems here when in the ebb lab.
	 * They result from the home env variable being /home/username
	 * and the ldap home variable being /ddfs/user/data/...
	 * The workdir folder ends up being >null< and cant be found,
	 * which leads to an IO exception when the ConfigurationWriter
	 * is called. (ADM)
	*
	*public void setDefaultLocation(String location) {
		//if (!defaultFileLocation.equals(location))
		//	ConfigurationWriter.write(getConfigFile());
		this.defaultFileLocation = location;
		
	}*/

	public void setLockFile() {
		ConfigurationWriter.setLockFile();
	}
	
	public boolean hasLockfile() {
		File f = new File(workspace_dir+sep+LOCKFILE);
		return f.exists();
	}
	
	public File getLockFile() {
		return new File(workspace_dir+sep+LOCKFILE);
	}
	
	public void removeLockFile() {
		Configuration.getInstance().getLockFile().delete();
	}
	
	/*
	public String getDefaultLocation() {
		return defaultFileLocation;
	}*/
	
	public void setGoogleUrl(String newUrl) {
		googleUrl = newUrl;
	}
	
	public String getGoogleUrl() {
		return googleUrl;
	}
	
	public String getGoogleUrl(String searchItem) {
		return googleUrl.replace("XXX", searchItem);
	}
	
	/*public void setNcbiUrl(String newUrl) {
		ncbiUrl = newUrl;
	}
	
	public String getNcbiUrl() {
		return ncbiUrl;
	}
	
	public String getNcbiUrl(String searchItem) {
		return ncbiUrl.replace("XXX", searchItem);
	}*/
	
	public void setPfamUrl(String newUrl) {
		pfamUrl = newUrl;
	}
	
	public String getPfamUrl() {
		return pfamUrl;
	}
	
	public String getPfamUrl(String searchItem) {
		return pfamUrl.replace("XXX", searchItem);
	}
	
	public void setUniprotUrl(String newUrl) {
		uniprotUrl = newUrl;
	}
	
	public String getUniprotUrl() {
		return uniprotUrl;
	}
	
	public String getUniprotUrl(String searchItem) {
		return uniprotUrl.replace("XXX", searchItem);
	}
	
	public void setEmailAddr(String email) {
		emailAddr = email;
	}
	
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setHmmScanBin(String path2HmmScan) {
		this.hmmScanBin = path2HmmScan;
	}	
	
	public String getHmmScanBin() {
		return hmmScanBin;
	}
	
	public void setHmmPressBin(String path2HmmPress) {
		this.hmmPressBin = path2HmmPress;
	}
	
	public String getHmmPressBin() {
		return hmmPressBin;
	}
	
	public void setHmmerDB(String path2HmmerDB) {
		hmmDB = path2HmmerDB;
	}
	
	public String getHmmerDB() {
		return hmmDB;
	}
	
	public boolean isServiceRunning() {
		return service_running;
	}

	public void setServiceRunning(boolean running) {
		this.service_running = running;
	}
	
	public String getDocuPath() {
		return this.documentationPath;
	}
	
	public void setDocuPath(String path) {
		this.documentationPath = path;
	}
	
	private static void getJarPath() {
		File file = new File(Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		System.out.println("This is the path: "+file.getAbsolutePath());
	
	}
	

	
}


