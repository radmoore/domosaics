package angstd.model.arrangement.io;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import angstd.model.GO.*;
import angstd.model.arrangement.DomainFamily;
import angstd.model.configuration.Configuration;
import angstd.util.CheckConnectivity;





public class Pfam2GOreader {

	private static Pfam2GOreader instance;
	
	public static Pfam2GOreader getInstance() {
		if (instance == null)
			instance = new Pfam2GOreader();
		
		return instance;
	}
	
	
	public static void readFile() {
		try {
			InputStream is = GatheringThresholdsReader.class.getResourceAsStream("resources/pfam2go");
			BufferedReader localIn = new BufferedReader(new InputStreamReader(is));
			URL remoteFile = new URL("http://www.geneontology.org/external2go/pfam2go");
			BufferedReader remoteIn = new BufferedReader(new InputStreamReader(remoteFile.openStream()));
			
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
	}
	
	
	
	public static void readGOFile() {
		
		
		Map<String, DomainFamily> domFamMap = GatheringThresholdsReader.getInstance();
		
		//!version date: 2011/02/05 07:20:28
		//DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// comment line
		Pattern cLine = Pattern.compile("^!.+");
		// relevant line
		// Pfam:PF00178 Ets > GO:sequence-specific DNA binding transcription factor activity ; GO:0003700
		Pattern pfam2GOline = Pattern.compile("Pfam:(PF\\d+) (\\w+) > GO:(.+) ; (GO:\\d+)");
		Matcher m;
		//Date versionDate;
		boolean firstLine = true;

		GeneOntology go = GeneOntology.getInstance();
		
		try {
		
			InputStream is = GatheringThresholdsReader.class.getResourceAsStream("resources/pfam2go");
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			
			String line;
			
			while((line = in.readLine()) != null) {
				
				if (firstLine) {
					//String [] fields = line.split(": ");
					//versionDate = (Date)formatter.parse(fields[1]);
					//go.setVersionDate(versionDate);
					firstLine = false;
					continue;
				}
				
				m = cLine.matcher(line);
				if (m.matches())
					continue;
				
				m = pfam2GOline.matcher(line);
				if (m.matches()) {
					
					String gid 		= m.group(4);
					//String term 	= m.group(3);
					//String pfamID	= m.group(2);
					String pfamAcc	= m.group(1);
					
					DomainFamily fam = domFamMap.get(pfamAcc);
					
					//go.addTerm(gid, term);
					//System.out.println(line);
					fam.addGoTerm(go.getTerm(gid));
				}
			}
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
	}
	
	//TODO
	public void updateGOmap() {
		
	}
	


	
	
	
}
