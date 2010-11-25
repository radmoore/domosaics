package angstd.model.arrangement.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.io.AbstractDataReader;


/**
 * Class for reading various hmmout like formats. Currently
 * supports:
 * - pfam_scan.pl (part of pfam pipeline, see ftp://ftp.sanger.ac.uk/pub/databases/Pfam/Tools/)
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class HmmOutReader extends AbstractDataReader<DomainArrangement> {

	protected Map<String, DomainFamily> domFamilyMap = new HashMap<String, DomainFamily>();

	public static boolean checkFileFormat(File file) {
		return true;
	}

	public DomainArrangement[] getData(Reader reader) throws IOException {
		
		List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
		
		BufferedReader in = new BufferedReader(reader);
		
		int from, to;
		double evalue;
		String line, acc;
		String prevProtID 		= null;
		String currentProtID 	= null;
		DomainArrangement prot 	= null;
		DomainFamily domFamily 	= null;
		Domain dom 				= null;
		
		while((line = in.readLine()) != null) {
			
			
			if (line.isEmpty())					
				continue;
			if (line.startsWith("#"))			
				continue;
			
			String[] entryFields = line.split("\\s+");
			currentProtID = entryFields[0];
						
			from 		= Integer.parseInt(entryFields[1]);
			to	 		= Integer.parseInt(entryFields[2]);
			evalue		= Double.parseDouble(entryFields[12]);
			acc			= entryFields[6];
			domFamily 	= domFamilyMap.get(acc);
			
			if (domFamily == null) {
				domFamily = new DomainFamily(acc);
				domFamilyMap.put(domFamily.getID(), domFamily);
			}
			
			if ( !currentProtID.equals(prevProtID) ) {
			
				if (prot != null)
					arrList.add(prot);					// save last protein
				
				prot = new DomainArrangement(); 		// new protein
				prot.setName(currentProtID);
				System.out.println("Protein ID: "+currentProtID);
			}
			
			dom = new Domain(from, to, domFamily); 		// same protein as last entry
			dom.setEvalue(evalue);
			prot.addDomain(dom);
			
			prevProtID = currentProtID;
			
		}
		
		return arrList.toArray(new DomainArrangement[arrList.size()]);
		
	}

}
