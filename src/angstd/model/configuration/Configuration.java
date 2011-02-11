package angstd.model.configuration;

import java.io.File;

/**
 * Configuration holds all specified URLs to look up nodes in trees and 
 * domains from an arrangement. The entrys can be changed using the 
 * configuration module.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class Configuration {
	
	public static final String CONFIGFILE = "config";
	public static final String DEF_FILE_LOCATION = System.getProperty("user.home");
	public static final String DEF_GOOGLE_SEARCH = "http://www.google.com/search?q=XXX";
	public static final String DEF_NCBI_SEARCH = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=protein&cmd=search&term=XXX";
	public static final String DEF_PFAM_SEARCH = "http://pfam.sanger.ac.uk/family?acc=XXX";
	public static final String DEF_SWISSPROT_SEARCH = "http://www.expasy.org/cgi-bin/sprot-search-de?XXX";
	public static final String DEF_EMAIL_ADDR = "";
	public static final String DEF_HMMERBINS = "/usr/share/Hmmer3/binaries/";
	public static final String DEF_HMMERDB = "";
	
	public static final boolean DEF_SHOW_ADVICES = true;
	

	protected String defaultFileLocation;
	
	protected String googleUrl;
	protected String ncbiUrl; 
	protected String pfamUrl; 
	protected String swissprotUrl;
	protected String emailAddr;
	protected String hmmBinDir;
	protected String hmmDB;

	protected boolean showAdvices;
	
	protected static Configuration instance;
	protected String workspace_dir; // this is null and seems to cause an exception, see below
	
	protected boolean visible = false;
	
	
	public Configuration() {
		restoreDefaults();
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void restoreDefaults() {
		defaultFileLocation = DEF_FILE_LOCATION;
		googleUrl = DEF_GOOGLE_SEARCH;
		ncbiUrl = DEF_NCBI_SEARCH; 
		pfamUrl = DEF_PFAM_SEARCH; 
		swissprotUrl = DEF_SWISSPROT_SEARCH;
		emailAddr = DEF_EMAIL_ADDR;
		hmmBinDir = DEF_HMMERBINS;
		hmmDB = DEF_HMMERDB;
		showAdvices = DEF_SHOW_ADVICES;
	}
	
	public static Configuration getInstance() {
		if (instance == null)
			instance = new Configuration();
		return instance;
	}
	
	public File getConfigFile() {
		return new File(workspace_dir+"/"+CONFIGFILE);
	}
	
	public void setWorkspaceDir(String workspace_dir) {
		this.workspace_dir = workspace_dir;
	}
	
	public String getWorkspaceDir() {
		return workspace_dir;
	}
	
	public void setShowAdvices(boolean showAdvices) {
		this.showAdvices = showAdvices;
	}
	
	public boolean isShowAdvices() {
		return showAdvices;
	}
	
	/**
	 * TODO:
	 * There are some problems here when in the ebb lab.
	 * They result from the home env variable being /home/username
	 * and the ldap home variable being /ddfs/user/data/...
	 * The workdir folder ends up being >null< and cant be found,
	 * which leads to an IO exception when the ConfigurationWriter
	 * is called. (ADM)
	**/
	public void setDefaultLocation(String location) {
		if (!defaultFileLocation.equals(location))
			ConfigurationWriter.write(getConfigFile());
		this.defaultFileLocation = location;
		
	}
	
	public String getDefaultLocation() {
		return defaultFileLocation;
	}
	
	public void setGoogleUrl(String newUrl) {
		googleUrl = newUrl;
	}
	
	public String getGoogleUrl() {
		return googleUrl;
	}
	
	public String getGoogleUrl(String searchItem) {
		return googleUrl.replace("XXX", searchItem);
	}
	
	public void setNcbiUrl(String newUrl) {
		ncbiUrl = newUrl;
	}
	
	public String getNcbiUrl() {
		return ncbiUrl;
	}
	
	public String getNcbiUrl(String searchItem) {
		return ncbiUrl.replace("XXX", searchItem);
	}
	
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
		swissprotUrl = newUrl;
	}
	
	public String getUniprotUrl() {
		return swissprotUrl;
	}
	
	public String getUniprotUrl(String searchItem) {
		return swissprotUrl.replace("XXX", searchItem);
	}
	
	public void setEmailAddr(String email) {
		emailAddr = email;
	}
	
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setHmmerBins(String path2HmmerBins) {
		hmmBinDir = path2HmmerBins; 
	}
	
	public String getHmmerBins() {
		return hmmBinDir;
	}
	
	public void setHmmerDB(String path2HmmerDB) {
		hmmDB = path2HmmerDB;
	}
	
	public String getHmmerDB() {
		return hmmDB;
	}
}
