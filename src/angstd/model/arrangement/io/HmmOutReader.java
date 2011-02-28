package angstd.model.arrangement.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.io.AbstractDataReader;
import angstd.ui.util.MessageUtil;


/**
 * Class for reading various hmmout like formats. Currently
 * supports:
 * TODO
 * handel exceptions properly
 * warn RE: end position?
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class HmmOutReader extends AbstractDataReader<DomainArrangement> {

	/**
	 * static parameter: evalue required by the user for each domain
	 * @return
	 */
	protected static double userThresh = 10;
	
	public static void setThreshold (double userT) {
		userThresh = userT;
	}
	
	/**
	 * Checks whether first non-comment, non empty line in 
	 * file can be split on whitespaces producing at least 12 fields
	 * 
	 * @param file
	 * @return
	 */
	public static boolean checkFileFormat(File file) {
		try {
				
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			
			while ((line = in.readLine()) != null) {
				
				if (line.isEmpty())					
					continue;
				if (line.startsWith("#"))			
					continue;
				
				
				in.close(); // only read first 'meaningful' line
				if (line.split("\\s+").length >= 15)
					return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public DomainArrangement[] getData(Reader reader) throws IOException {
		
		List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
		BufferedReader in = new BufferedReader(reader);
		String line;
		boolean read = true;
		
		while(read) {
			
			line = in.readLine();
			
			if (line.isEmpty())			
				continue;
			
			if (line.startsWith("#")){			
				if (line.contains("pfam_scan.pl")) {
					arrList = parsePfamScan(in);
				}
				else {
					System.out.println("Parsing hmmscan output.");
					arrList = parseHmmerScan(in);
				}
			}
			read = false;
			
		}
		in.close();
		return arrList.toArray(new DomainArrangement[arrList.size()]);
		
	}

	
	private List<DomainArrangement> parsePfamScan(BufferedReader in) {
		
		List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
		int from, to;
		double evalue, score;
		String line, acc;
		String prevProtID		= null;
		String currentProtID 	= null;
		DomainArrangement prot 	= null;
		DomainFamily domFamily 	= null;
		Domain dom 				= null;
		
		try {
			
			while((line = in.readLine()) != null) {
						
						
				if (line.isEmpty())					
					continue;
				if (line.startsWith("#"))			
					continue;
						
				String[] entryFields = line.split("\\s+");
					
				currentProtID = entryFields[0];
				
				from 		= Integer.parseInt(entryFields[1]);
				to	 		= Integer.parseInt(entryFields[2]);
				score		= Double.parseDouble(entryFields[11]);
				evalue		= Double.parseDouble(entryFields[12]);
				acc			= entryFields[6];
				domFamily 	= GatheringThresholdsReader.getInstance().get(acc);
				
				if (domFamily == null) { 				
					domFamily = new DomainFamily(acc);
//					domFamily.setPfamID(pfamID);
					GatheringThresholdsReader.getInstance().put(acc, domFamily);
				}
				
					
				if ( !currentProtID.equals(prevProtID) ) {
				
					if (prot != null)
						arrList.add(prot);					// save last protein
					
					prot = new DomainArrangement(); 		// new protein
					prot.setName(currentProtID);
				}
				
				dom = new Domain(from, to, domFamily); 		// same protein as last entry
				dom.setEvalue(evalue);
				dom.setScore(score);   // log odds score
				if(!domFamily.getAcc().equals("")) {
					if(score < domFamily.getGathThreshByDom())
						dom.setPutative(true);
				}
				prot.addDomain(dom);
				
				prevProtID = currentProtID;
			}
			
		} 
		catch (NumberFormatException e) {
			MessageUtil.showWarning("Error while parsing pfam_scan output file. Please check file format.");
			e.printStackTrace();
		} 
		catch (IOException e) {
			MessageUtil.showWarning("Error while reading/parsing pfam_scan output.");
			e.printStackTrace();
		}
		
		if (prot != null)
		 arrList.add(prot);
		
		return arrList;
	}
	
	
	private List<DomainArrangement> parseHmmerScan(BufferedReader in) {
		
		List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
		
		int protLength, from, to;
		double evalue, score;
		String line, acc;
		String prevProtID		= null;
		String currentProtID 	= null;
		DomainArrangement prot 	= null;
		DomainFamily domFamily 	= null;
		Domain dom 				= null;
		
		
		try {
			
			while((line = in.readLine()) != null) {		
						
				if (line.isEmpty())					
					continue;
				if (line.startsWith("#"))			
					continue;
						
				String[] entryFields = line.split("\\s+");
				
				currentProtID = entryFields[3];
				protLength 	= Integer.parseInt(entryFields[5]); 
				from 		= Integer.parseInt(entryFields[17]);
				to	 		= Integer.parseInt(entryFields[18]);
				evalue		= Double.parseDouble(entryFields[12]);
				score		= Double.parseDouble(entryFields[13]);
				acc			= entryFields[0]; // actually: name:
				domFamily 	= GatheringThresholdsReader.getInstance().get(acc);
				if (domFamily == null) { 				
					domFamily = new DomainFamily(acc);
//					domFamily.setPfamID(pfamID);
					GatheringThresholdsReader.getInstance().put(acc, domFamily);
				}
				
				if (evalue > 10 || evalue > userThresh)
					continue;
				
					
				if ( !currentProtID.equals(prevProtID) ) {
				
					if (prot != null)
						arrList.add(prot);					// save last protein
					
					prot = new DomainArrangement(); 		// new protein
					prot.setName(currentProtID);
					prot.setSeqLen(protLength);
				}

				dom = new Domain(from, to, domFamily); 		// same protein as last entry
				dom.setEvalue(evalue);
				//System.out.println(dom.toString());
				dom.setScore(score);   // log odds score
				if (!domFamily.getAcc().equals("")) {
					if (Double.parseDouble(entryFields[13]) < domFamily.getGathThreshByFam() || score < domFamily.getGathThreshByDom()) {
						dom.setPutative(true);
						//System.out.println("Putative");
					}
					else {
						//System.out.println("Asserted");
					}
				}
				prot.addDomain(dom);
				
				prevProtID = currentProtID;
			}
				
		}
		catch (NumberFormatException e) {
			MessageUtil.showWarning("Error while parsing hmmscan output file. Please check file format.");
			e.printStackTrace();
		} 
		catch (IOException e) {
			MessageUtil.showWarning("Error while reading/parsing hmmscan output.");
			e.printStackTrace();
		}
		
		if (prot != null)
		 arrList.add(prot);
		
		return arrList;
	}
	
	
}
