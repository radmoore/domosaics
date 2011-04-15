package angstd.model.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ConfigurationReader {

	public static void read(File file) {
		 try {
		    BufferedReader in = new BufferedReader(new FileReader(file));
	
		    String line;
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				
				if (line.contains(ConfigurationWriter.DEFAULT_LOCATION)) 
					Configuration.getInstance().setDefaultLocation(
							line.replace(ConfigurationWriter.DEFAULT_LOCATION, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.GOOGLE_URL)) 
					Configuration.getInstance().setGoogleUrl(
							line.replace(ConfigurationWriter.GOOGLE_URL, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.NCBI_URL)) 
					Configuration.getInstance().setNcbiUrl(
							line.replace(ConfigurationWriter.NCBI_URL, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.PFAM_URL)) 
					Configuration.getInstance().setPfamUrl(
							line.replace(ConfigurationWriter.PFAM_URL, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.UNIPROT_URL)) 
					Configuration.getInstance().setUniprotUrl(
							line.replace(ConfigurationWriter.UNIPROT_URL, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.EMAIL_ADDR)) 
					Configuration.getInstance().setEmailAddr(
							line.replace(ConfigurationWriter.EMAIL_ADDR, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.HMMER_BINS)) 
					Configuration.getInstance().setHmmerBins(
							line.replace(ConfigurationWriter.HMMER_BINS, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.HMMER_PROFILE_DB)) 
					Configuration.getInstance().setHmmerDB(
							line.replace(ConfigurationWriter.HMMER_PROFILE_DB, "").trim()
					);
				
				if (line.contains(ConfigurationWriter.SHOWADVISES))
					Configuration.getInstance().setShowAdvices(
							Boolean.parseBoolean(line.replace(ConfigurationWriter.SHOWADVISES, "").trim())
					);
				
				if (line.contains(ConfigurationWriter.SAVEONEXIT)) {
					Configuration.getInstance().setSaveOnExit(
							Boolean.parseBoolean(line.replace(ConfigurationWriter.SAVEONEXIT, "").trim())
					);
				}
				
			}
		    in.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	}
}
