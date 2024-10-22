package domosaics.model.configuration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;




public class ConfigurationWriter {

	public static final String VERSION = "VERSION:";
	public static final String WORKSPACE_LOCATION = "Workspace location:";
	public static final String GOOGLE_URL = "google search:";
	//public static final String NCBI_URL = "ncbi search:";
	public static final String PFAM_URL = "pfam search:";
	public static final String UNIPROT_URL = "uniprot search:";
	public static final String EMAIL_ADDR = "email address:";
	public static final String HMMER_SCAN_BIN = "hmmer3 scan:";
	public static final String HMMER_PRESS_BIN = "hmmer3 press:";
	public static final String HMMER_PROFILE_DB = "hmmer3 profile db:";
	public static final String SHOWADVISES = "show advice:";
	public static final String SAVEONEXIT = "save WS:";
	public static final String OVERWRITEPROJECTS = "overwrite projects:";
	public static final String HELPIMPROVE = "improve domosaics:";
	public static final String DOMAINBYNAME = "domain by name:";
	public static final String DOCUMENTATION_LOCATION = "Documentation location:";
	
	
	public static void write() {
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(Configuration.getInstance().getConfigFile())); 

		    Configuration config = Configuration.getInstance();
		    
		    out.write("DoMosaics Configuration File \n\n");
		    out.write(VERSION+Configuration.CURRENT_PROGRAM_VERSION+"\n");
		    out.write(WORKSPACE_LOCATION+config.getWorkspaceDir()+"\n");	
		    out.write(GOOGLE_URL+config.getGoogleUrl()+"\n");
		    //out.write(NCBI_URL+config.getNcbiUrl()+"\n");
		    out.write(PFAM_URL+config.getPfamUrl()+"\n");
		    out.write(UNIPROT_URL+config.getUniprotUrl()+"\n");
		    out.write(EMAIL_ADDR+config.getEmailAddr()+"\n");
		    out.write(HMMER_SCAN_BIN+config.getHmmScanBin()+"\n");
		    out.write(HMMER_PRESS_BIN+config.getHmmPressBin()+"\n");
		    out.write(HMMER_PROFILE_DB+config.getHmmerDB()+"\n");
		    //out.write(SHOWADVISES+config.isShowAdvices()+"\n");
		    out.write(SAVEONEXIT+config.saveOnExit()+"\n");
		    out.write(OVERWRITEPROJECTS+config.getOverwriteProjects()+"\n");
		    out.write(DOMAINBYNAME+Configuration.isNamePreferedToAcc()+"\n");
		    out.write(HELPIMPROVE+config.getHelpImprove()+"\n");
		    out.write(DOCUMENTATION_LOCATION+config.getDocuPath(false)+"\n");
		    		
		    out.flush();
		    out.close();
		   
		} 
		catch (IOException e) {
			System.out.println(e);
			
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Configuration file not found");
		}
	}
	
	public static void setLockFile() {
		Configuration config = Configuration.getInstance();
		
		try {
			BufferedWriter lockfile = new BufferedWriter(new FileWriter(config.getLockFile())); 
			lockfile.write("");
			lockfile.flush();
			lockfile.close();
			
		}
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
	}
	
	
	
}
