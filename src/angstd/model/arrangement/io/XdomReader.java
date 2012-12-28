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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import angstd.model.GO.GeneOntology;
import angstd.model.GO.GeneOntologyTerm;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.model.configuration.Configuration;
import angstd.model.io.AbstractDataReader;
import angstd.model.io.DataReader;
import angstd.ui.util.MessageUtil;
import angstd.util.StringUtils;



/**
 * The Xdom-Parser parses Xdom-formatted files which are produced 
 * for example by the MKDOM program.
 * <p>
 * If the superclass method {@link AbstractDataReader#getDataFromString(String)}
 * is called to create the arrangements, it must be ensured that each line 
 * contains a line separator.
 * <p>
 * The description for a protein entry within the file is formatted as followed: <br>
 * > Headerline <br>
 * Domainline1 <br>
 * Domainline2...<br>
 * <br>
 * with headerline looking like:
 * >[\\s+]ProteinID[\\s+ SequenceLength][\\s+ Description] <br>
 * <br>
 * and domain lines like: <br>
 * From \\s+ To \\s+ DomainID [\\s+ e-value] <br>
 * <p>
 * Empty lines as well as comment lines starting with # are allowed.
 * 
 * @author Andreas Held
 * 
 * 
 */
public class XdomReader extends AbstractDataReader<DomainArrangement> {
	
	/**
	 * a map storing all parsed domains. (Used to ensure that the same 
	 * domain family object is used for domains with identically names
	 */

	
	
	public boolean checkFormat(File file) {
		DomainArrangement prot = null;
		boolean okay=false;
		// flag indicating whether or not the first sequence was parsed
		boolean firstRead = false;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file)); 
			String line;
			
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				if (line.startsWith("#"))			// ignore comments
					continue;
				if (line.startsWith(">")) {			// parse header line
					if (firstRead) {	
						if(prot!=null) {
							if(!prot.getName().equals(""))
								okay=true;	
							else {
								return false;							
							}
						}
				}else {
					firstRead = true;
				}
					prot = parseHeader(line);
				} else {							// parse domain line
					try {
						prot = parseDomain(line, prot);
						okay=true;
					} catch (NumberFormatException nfe) {
						return false;
					}
					catch (WrongFormatException wfe) {
						return false;
					}
				}
			}
			return okay;
		} catch (IOException e) {
			Configuration.getLogger().debug(e.toString());
			return false;
		}
	}
	
	/**
	 * Parses a xdom formatted file. See also {@link DataReader}.
	 */
	public DomainArrangement[] getData(Reader reader) throws IOException {
		// store the parsed arrangements in here
		List<DomainArrangement> res = new ArrayList<DomainArrangement>();
		
		/*
		 * Proteins are added to the result list each time a new header starts.
		 * Of course not the first time because the arrangement is empty then.
		 * Therefore the test for null is needed in the 
		 * if (line.startsWith(">")) clause.
		 * For the last arrangement no header exists indicating that 
		 * this protein has to be added. Therefore do it manually.
		 */
		
		DomainArrangement prot = null;
		Domain dom = null;
		// flag indicating whether or not the first sequence was parsed
		boolean firstRead = false;
	
		// parse the document line by line
		BufferedReader in = new BufferedReader(reader); 
		String line;
		while((line = in.readLine()) != null) {
			if (line.isEmpty())					// ignore empty lines
				continue;
			if (line.startsWith("#"))			// ignore comments
				continue;
			if (line.startsWith(">")) {	// parse header line
				if (firstRead) {			
					if (prot != null) 
						res.add(prot);
					else {
						MessageUtil.showWarning("Error while parsing protein line.");
						return null;
					}
				}else {
					firstRead = true;
				}
				prot = parseHeader(line);
			} else {							// parse domain line
				try {
					prot = parseDomain(line, prot);
				} 
				catch (NumberFormatException nfe) {
					Configuration.getLogger().debug(nfe.toString());
					MessageUtil.showWarning("Error while parsing domain line. Make sure the format fullfills: \"name from to [evalue]\"");
					return null;
				} 
				catch (WrongFormatException wfe) {
					Configuration.getLogger().debug(wfe.toString());
					MessageUtil.showWarning("Error while parsing domain line. Make sure the format fullfills: \"name from to [evalue]\"");
					return null;
				}
			}
		}
		
		// add also the last protein
		if (prot != null) 
			res.add(prot);
			
		return res.toArray(new DomainArrangement[res.size()]);
	}
	
	/**
	 * parse header: "ProteinID [SequenceLength] [Protein description]"
	 *
	 * @param header
	 * 		the line containing the protein header
	 * @return
	 * 		a new domain arrangement object containing the header information
	 */
	private DomainArrangement parseHeader(String headerStr) {
		DomainArrangement prot = new DomainArrangement();
		int actToken = 0;
		
		// kill ">" char and split header into token
		headerStr = headerStr.replace(">", "");
		String[] token = headerStr.split("\\s+");
		if(token.length!=0) {

			// make both cases "> protID" and ">protID" valid
			if (token[actToken].isEmpty())
				actToken++;

			// first token is always the protein name
			prot.setName(token[actToken]);

			// check if protein contains more information, e.g. seqLen and description
			if (token.length == actToken+1)
				return prot;
			actToken++;

			// just in case create the description buffer
			StringBuffer desc = new StringBuffer();

			// check if protein contains the sequence length
			if (StringUtils.isNumber(token[actToken]))		// if it is a number its the length
				prot.setSeqLen(Integer.parseInt(token[actToken]));
			else 											// else it is the description	
				desc.append(token[actToken].replace(";",""));

			// everything which comes now must be the description
			for (int t = actToken+1; t < token.length; t++) 
				desc.append(" "+token[t].replace(";",""));

			prot.setDesc(desc.toString());
		}
		return prot;
	}
	
	/**
	 *TODO: bug in here, seems to always create the same domain!
	 * read domains: "From, To, DomainID, [e-value]"
	 * @param domainStr
	 * @return
	 */
	private DomainArrangement parseDomain(String domainStr, DomainArrangement prot) throws NumberFormatException, WrongFormatException {
		
		DomainFamily domFamily = null;
		Domain dom = null;
		int actToken = 0;
		
		// split domain string into token
		String[] token = domainStr.split("\\s+");
		
		// make it possible to start the line with white spaces
		if (token[actToken].isEmpty())
			actToken++;
		
		// check if the line has enough tokens, otherwise the format is wrong
        if (token.length-actToken < 3) 
			throw new WrongFormatException();
		
		// first get the domain family id and check whether or not the domain family already occurred within the document
        // DACC;DID (DID may be null, in which case a call to getID() returns the ACC; ergo ACC and DID may be equal)
        String domInfo = token[actToken+2];
        String dName, dId; 
        
        if (domInfo.indexOf(';') > 0) {
        	dId = domInfo.split(";")[0];
        	dName = domInfo.split(";")[1];
        }
        else {
        	dName = domInfo;
        	dId = domInfo;
        }
        	
		DomainType dType = DomainType.getType(dId);
		domFamily = GatheringThresholdsReader.getInstance().get(dId);
		
		if (domFamily == null) { 				
			if (dName == null)
				dName = dId;
			
			domFamily = new DomainFamily(dId, dName, dType);
//			domFamily.setPfamID(pfamID);
			GatheringThresholdsReader.add(domFamily);
		}
		
		// "from", "to" must be the first two tokens
		int from = Integer.parseInt(token[actToken]);
		int to = Integer.parseInt(token[actToken+1]);
		
		// create domain
		dom = new Domain (from, to, domFamily);
				
		// check if the e-value is present within the format
		if (token.length == actToken+3) {
		 prot.addDomain(dom);
	     return prot;
		}

		// if so... parse it
		double evalue = Double.parseDouble(token[actToken+3]);
		dom.setEvalue(evalue);

		if ( token.length == actToken+5 ) {
			String[] comment = token[actToken+4].split(";");
			for (int i=0; i < comment.length; i++) {
				String c = comment[i];
				if ( c.equals("putative") || c.equals("asserted") ) {
		//			dom.setPutative(c.equals("putative"));
				}
				else if ( Pattern.matches("GO:\\d+", c) ) {
					
					if (! (domFamily.hasGoTerm(c)) ) {
						GeneOntology go = GeneOntology.getInstance();
						GeneOntologyTerm term = go.getTerm(c);
						domFamily.addGoTerm(term);
					}
				}
				
				else if ( c.equals("hidden") ) {
					prot.addDomain(dom);
				    prot.hideDomain(dom);
				}
			}	
		}
		if(!prot.getHiddenDoms().contains(dom)) {
			prot.addDomain(dom);
		}
		
		return prot;
	}
		
	
	private class WrongFormatException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public WrongFormatException () {
		    super("Wrong Format! Not enough tokens in domain line");
		}
	}
}

