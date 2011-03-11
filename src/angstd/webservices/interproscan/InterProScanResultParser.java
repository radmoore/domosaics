package angstd.webservices.interproscan;

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

import org.apache.log4j.Logger;

import angstd.model.GO.GeneOntology;
import angstd.model.GO.GeneOntologyTerm;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.model.arrangement.io.GatheringThresholdsReader;

/**
 * Parses the result produces by WSInterproScan package into
 * {@link DomainArrangement} objects.
 * 
 * @author Andreas Held
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
			return getArrangement(new StringReader(arrangementsStr));
		} catch (IOException ioe) {
			Logger.getLogger("logger").warn("IO Exception: could not read domain arrangement from string");
			Logger.getLogger("logger").warn("Reading InterproScan result  aborted");
		}
		catch (Exception e) {
			e.printStackTrace();
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
		Map<String, DomainFamily> domFamilyMap = new HashMap<String, DomainFamily>();
		
		// parse each line of the document
		BufferedReader in = new BufferedReader(reader); 
		String line;
		while((line = in.readLine()) != null) {
			if (line.isEmpty())					// ignore empty lines
				continue;
						
			System.out.println("Result string: "+ line);
			
			String[] token = line.split("\\t+");
			
			if (prot.getName() == null)
				prot.setName(token[0]);
	
			String acc = token[4];
			String id = token[5];
			String scanMethod = token[3];

			ArrayList<String> goStrings = null;
			
			if (token.length > 12) {
				goStrings = new ArrayList<String>() ;
				for(int i = 13; i < token.length; i++)
					goStrings.add(token[i]);
			}
	
			DomainFamily domFamily = GatheringThresholdsReader.getInstance().get(acc);
			if (domFamily == null) {
				domFamily = new DomainFamily(acc, id, DomainType.getTypeByMethod(scanMethod));
//				domFamily.setPfamID(pfamID);
				GatheringThresholdsReader.getInstance().put(acc, domFamily);
			}
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
					// TODO: what if term does not exist?
					GeneOntology go = GeneOntology.getInstance();
					GeneOntologyTerm term = go.getTerm(gid);
					if (!(dFam.hasGoTerm(gid)))
						dFam.addGoTerm(term);
				}
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
			
	}
	
	
	
	
}
