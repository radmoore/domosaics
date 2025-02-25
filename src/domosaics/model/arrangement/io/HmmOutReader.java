package domosaics.model.arrangement.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.DomainType;
import domosaics.model.configuration.Configuration;
import domosaics.model.io.AbstractDataReader;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;





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
	private static double userThresh = 10;
	private int total, completed;
	
	
	public static void setThreshold (double userT) {
		userThresh = userT;
	}
	
	public static int countEntries(File file) {
	
		String line;
		String prevProtID		= null;
		String currentProtID 	= null;
		int totalHits = 0;
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			while((line = in.readLine()) != null) {		
						
				if (line.isEmpty())					
					continue;
				if (line.startsWith("#"))			
					continue;
						
				String[] entryFields = line.split("\\s+");
				currentProtID = entryFields[3];
				
				if (!currentProtID.equals(prevProtID))
					totalHits++;
					
				prevProtID = currentProtID;	
			}
			
		}
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return totalHits;
	}
	
	/**
	 * Checks whether first non-comment, non empty line in 
	 * file can be split on whitespaces producing at least 12 fields
	 * 
	 * @param file
	 * @return
	 */
	public static boolean checkFileFormat(File file) {
		
		BufferedReader in=null;
		String line = null;
		try {
				
			in = new BufferedReader(new FileReader(file));
			
			while ((line = in.readLine()) != null) {
				
				if(line.contains("full sequence") && line.contains("this domain") && line.contains("coord"))
				{
					in.close(); 
					return true;
				}else {
					in.close(); 
					return false;
					
				}
			}
			in.close(); // only read first 'meaningful' line
			return false;
		}
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return false;
		}
	}

	
	
	@Override
	public DomainArrangement[] getData(Reader reader) throws IOException {
	
		List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
		BufferedReader in = new BufferedReader(reader);
		String line;
		boolean read = true;
		completed = 0;
		
		while(read) {
			
			line = in.readLine();
			
			if (line.isEmpty())			
				continue;
			
			if (line.startsWith("#")){			
				if (line.contains("pfam_scan.pl")) {
					arrList = parsePfamScan(in);
				}
				else {
					arrList = parseHmmerScan(in);
				}
			}
			read = false;
			
		}
		in.close();
		return arrList.toArray(new DomainArrangement[arrList.size()]);
		
	}

	// TODO: change this to acc!
	private List<DomainArrangement> parsePfamScan(BufferedReader in) {
		
		List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
		int from, to;
		double evalue, score;
		String line, id;
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
				id			= entryFields[6];
				domFamily 	= GatheringThresholdsReader.getInstance().get(id);
				
				if (domFamily == null) { 				
					domFamily = new DomainFamily(id, null, DomainType.PFAM);
//					domFamily.setPfamID(pfamID);
					GatheringThresholdsReader.add(domFamily);
				}
				
					
				if ( !currentProtID.equals(prevProtID) ) {
				
					if (prot != null) {
						prot.sortDomains();
						arrList.add(prot);					// save last protein
					}
					prot = new DomainArrangement(); 		// new protein
					prot.setName(currentProtID);
					completed ++;
				}
				
				dom = new Domain(from, to, domFamily); 		// same protein as last entry
				dom.setEvalue(evalue);
				dom.setScore(score);   // log odds score
				
				if(domFamily.getGathThreshByFam()!=Double.POSITIVE_INFINITY) {
					if(score < domFamily.getGathThreshByDom())
						dom.setPutative(true);
				}
				prot.addDomain(dom);
				
				prevProtID = currentProtID;
			}
			
		} 
		catch (NumberFormatException e) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Error while parsing pfam_scan output file. Please check file format.");
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		} 
		catch (IOException e) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Error while reading/parsing pfam_scan output.");
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		
		if (prot != null) {
			prot.sortDomains();
			arrList.add(prot);
			completed ++;
		}
		
		return arrList;
	}
	
	
	private List<DomainArrangement> parseHmmerScan(BufferedReader in) {
		
		List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
		
		int protLength, from, to;
		double evalue, score;
		String line, acc, id;
		String prevProtID		= null;
		String currentProtID 	= null;
		DomainArrangement prot 	= null;
		DomainFamily domFamily 	= null;
		Domain dom 				= null;
		DomainType dType		= null;
		
		
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
				id			= entryFields[0];
				
				//PF\d+\.\d+ issue
				String accF = entryFields[1];
				acc			= accF.split("\\.")[0];
				
				
				
				
				domFamily 	= GatheringThresholdsReader.getInstance().get(acc);
				dType 		= DomainType.getType(acc);
				
				if (domFamily == null) { 				
					domFamily = new DomainFamily(acc, id, dType);
//					domFamily.setPfamID(pfamID);
					GatheringThresholdsReader.add(domFamily);
				}
				
				if (evalue > 10 || evalue > userThresh)
					continue;
				
					
				if ( !currentProtID.equals(prevProtID) ) {
				
					if (prot != null) {
						prot.sortDomains();
						arrList.add(prot);					// save last protein
					}
					prot = new DomainArrangement(); 		// new protein
					prot.setName(currentProtID);
					prot.setSeqLen(protLength);
					completed ++;
				}

				dom = new Domain(from, to, domFamily); 		// same protein as last entry
				dom.setEvalue(evalue);
				//System.out.println(dom.toString());
				dom.setScore(score);   // log odds score
				if (domFamily.getGathThreshByFam()!=Double.POSITIVE_INFINITY)
				{
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
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Error while parsing hmmscan output file. Please check file format.");
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		} 
		catch (IOException e) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Error while reading/parsing hmmscan output.");
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		
		if (prot != null) {
			prot.sortDomains();
			arrList.add(prot);
			completed ++;
		}
		
		return arrList;
	}
	
	public int getTotalProteins() {
		return total;
	}
	
	public int getProcessedEntries() {
		return completed;
	}	
	
}
