package domosaics.webservices.pidparse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import domosaics.model.configuration.Configuration;


/**
 * 
 * @author Andrew Moore
 *
 */
public class LineageUtil {
	
	public static String getLineage(String acc) {
		if (acc == null)
			return null;
		
		//NP_001157608;
		//gi|255918123|ref|NP_001157608.1
		Boolean complete = false;
	    String efetch 	= "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=";
	    String retmode 	= "&retmode=xml";
	    String line		= new String();
	    String lineage = null;
	    
	    //System.out.println("*** Attempting retrival using: "+returl);
	    
		BufferedReader in;
		
		try {
			URL fetchURL = new URL(efetch+acc.trim()+retmode);
			in = new BufferedReader(new InputStreamReader(fetchURL.openStream()));
			String organism = "";
			while((line = in.readLine()) != null) {
				
				if (complete) 
					System.out.println(line);
				
				if (line.contains("<GBSeq_organism>")) // REMOVE to skip organisms
					organism = line.replaceAll("\\<.*?\\>", "");// REMOVE to skip organisms
				
				if (line.contains("<GBSeq_taxonomy>")) {
						lineage = line.replaceAll("\\<.*?\\>", "");
						lineage += "; "+organism;	// REMOVE to skip organisms
				}
				
			}
			in.close();
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		} 
		return lineage;
	}

}
