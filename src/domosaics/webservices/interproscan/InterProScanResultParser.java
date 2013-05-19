package domosaics.webservices.interproscan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domosaics.model.GO.GeneOntology;
import domosaics.model.GO.GeneOntologyTerm;
import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.DomainType;
import domosaics.model.arrangement.io.GatheringThresholdsReader;
import domosaics.model.configuration.Configuration;

//import org.apache.log4j.Logger;


/**
 * Parses the result produces by WSInterproScan package into
 * {@link DomainArrangement} objects.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * 
 * 
 *
 */
public class InterProScanResultParser {

	/** parses the results from WSInterProScan raw format into a 
	 * domain arrangement.
	 * 
	 * @param arrangementsStr
	 * 		WSInterProScan raw format
	 * @return
	 * 		parsed domain arrangement
	 */
	public DomainArrangement parseResult(String arrangementsStr) {
		
		try {
			if (Configuration.isDebug())
//				System.out.println("*** Returned from iprscan: "+arrangementsStr);
			
			return getArrangement(new StringReader(arrangementsStr));
		} 
		catch (IOException ioe) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(ioe);
			else			
				Configuration.getLogger().debug(ioe.toString());
		}
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return null;
		}
		return null;
	}
	
	
	/*
	 * 0.  protName 
	 * 1.  ? 
	 * 2.  length 
	 * 3.  method 
	 * 4.  pfamID 
	 * 5.  domName  
	 * 6.  from 
	 * 7.  to 
	 * 8.  eval 
	 * 9.  T 
	 * 10. date 
	 * 11. InterproEntry 
	 * 12. OtherNames 
	 * 13. GODescription 
	 * 14. GOID
	 */
	/**
	 * The actual parsing is done here.
	 * 
	 * @param reader
	 * 		The Reader generated from the raw format of WSInterProScan
	 * @return
	 * 		parsed domain arrangement.
	 */
	public DomainArrangement getArrangement(Reader reader) throws IOException {
		DomainArrangement prot = new DomainArrangement();
		
		// parse each line of the document
		BufferedReader in = new BufferedReader(reader); 
		String line;
		while((line = in.readLine()) != null) {
			if (line.isEmpty())					// ignore empty lines
				continue;
						
			String[] token = line.split("\\t+");
			
			if (prot.getName() == null)
				prot.setName(token[0]);
	
			String acc = token[4];
			String name = token[5];
			//String scanMethod = token[3];
			String interproEntry = token[11];

			ArrayList<String> goStrings = null;
			
			if (token.length > 12) {
				goStrings = new ArrayList<String>() ;
				for(int i = 13; i < token.length; i++)
					goStrings.add(token[i]);
			}
	
		
			DomainFamily domFamily = GatheringThresholdsReader.getInstance().get(acc);
			if (domFamily == null) {
				
				//domFamily = new DomainFamily(acc, name, domType);
				domFamily = new DomainFamily(acc, name, DomainType.getType(acc));

				GatheringThresholdsReader.add(domFamily);
			}
			if(domFamily.getInterproEntry()==null)
				domFamily.setInterproEntry(interproEntry);
				//GatheringThresholdsReader.getInstance().get(acc).setInterproEntry(interproEntry);
			
			if (! (goStrings == null) )
				parseGOString(goStrings, domFamily);

			int from = Integer.parseInt(token[6]);
			int to = Integer.parseInt(token[7]);
			double eval = Double.parseDouble(token[8]);
			Domain dom = new Domain(from, to, domFamily);
			dom.setEvalue(eval);
			
			prot.addDomain(dom);
		}
//		prot.killOverlaps();
		prot.sortDomains();
		return prot;
	}

	
	public void parseGOString(ArrayList<String> goString, DomainFamily dFam) {
		
		Iterator<String> iter = goString.iterator();
		
		while(iter.hasNext()) {
			
			String presentTerms = iter.next();
			
			try {
				Pattern p = Pattern.compile("(GO:\\d+)");
				Matcher m = p.matcher(presentTerms);
				
				while (m.find()) {
					String gid = m.group();
					GeneOntology go = GeneOntology.getInstance();
					GeneOntologyTerm term = go.getTerm(gid);
					if (!(dFam.hasGoTerm(gid)))
						dFam.addGoTerm(term);
				}
			
			}
			catch (Exception e) {
				if (Configuration.getReportExceptionsMode(true))
					Configuration.getInstance().getExceptionComunicator().reportBug(e);
				else			
					Configuration.getLogger().debug(e.toString());
			}
			
		}
			
	}
	
	
	
	
}
