package angstd.webservices.interproscan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;

/**
 * Parses the result produces by WSInterproScan package into
 * {@link DomainArrangement} objects.
 * 
 * @author Andreas Held
 * 
 * TODO ask if pfam or normal id should be used
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
			
			String[] token = line.split("\\s+");
			
			if (prot.getName() == null)
				prot.setName(token[0]);
	
			String pfamID = token[4];
			String id = token[5];
	
			DomainFamily domFamily = domFamilyMap.get(id);
			if (domFamily == null) { 				
				domFamily = new DomainFamily(id);
//				domFamily.setPfamID(pfamID);
				domFamilyMap.put(domFamily.getID(), domFamily);
			}

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
}
