package angstd.model.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationWriter {

	public static final String DEFAULT_LOCATION = "default file location:";
	public static final String GOOGLE_URL = "google search:";
	public static final String NCBI_URL = "ncbi search:";
	public static final String PFAM_URL = "pfam search:";
	public static final String UNIPROT_URL = "uniprot search:";
	public static final String EMAIL_ADDR = "email address:";
	public static final String HMMER_SCAN_BIN = "hmmer3 scan:";
	public static final String HMMER_PRESS_BIN = "hmmer3 press:";
	public static final String HMMER_PROFILE_DB = "hmmer3 profile db:";
	public static final String SHOWADVISES = "show advice:";
	public static final String SAVEONEXIT = "save WS:";
	public static final String OVERWRITEPROJECTS = "overwrite projects:";
	
	
	public static void write(File file) {
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(file)); 

		    Configuration config = Configuration.getInstance();
		    
		    out.write("AnGSTD Configuration File \n\n");
		    out.write(DEFAULT_LOCATION+config.getDefaultLocation()+"\n\n");	
		    out.write(GOOGLE_URL+config.getGoogleUrl()+"\n");
		    out.write(NCBI_URL+config.getNcbiUrl()+"\n");
		    out.write(PFAM_URL+config.getPfamUrl()+"\n");
		    out.write(UNIPROT_URL+config.getUniprotUrl()+"\n");
		    out.write(EMAIL_ADDR+config.getEmailAddr()+"\n");
		    out.write(HMMER_SCAN_BIN+config.getHmmScanBin()+"\n");
		    out.write(HMMER_PRESS_BIN+config.getHmmPressBin()+"\n");
		    out.write(HMMER_PROFILE_DB+config.getHmmerDB()+"\n");
		    //out.write(SHOWADVISES+config.isShowAdvices()+"\n");
		    out.write(SAVEONEXIT+config.saveOnExit()+"\n");
		    out.write(OVERWRITEPROJECTS+config.getOverwriteProjects()+"\n");
		    		
		    out.flush();
		    out.close();
		   
		} catch (IOException e) {
	    	System.out.println("Configuration file "+file.getAbsolutePath()+" not found");
			//e.printStackTrace();
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
			e.printStackTrace();
		}
	}
	
	
	
}
