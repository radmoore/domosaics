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
import angstd.model.arrangement.DomainType;
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
	
	public static boolean checkFormat(File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file)); 
			String line;
		
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				if (line.startsWith("#"))			// ignore comments
					continue;
				
				in.close();
				if (line.startsWith(">")) 
					return true;
				break;
			}
			
			return false;
		} catch (IOException e) {
			e.printStackTrace();
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
	
		// parse the document line by line
		BufferedReader in = new BufferedReader(reader); 
		String line;
		while((line = in.readLine()) != null) {
			if (line.isEmpty())					// ignore empty lines
				continue;
			if (line.startsWith("#"))			// ignore comments
				continue;
			if (line.startsWith(">")) {			// parse header line
				if (prot != null) 
					res.add(prot);
				prot = parseHeader(line);
			} else {							// parse domain line
				try {
					prot = parseDomain(line, prot);
				} catch (NumberFormatException nfe) {
					MessageUtil.showWarning("Error while parsing domain line. Make sure the format fullfills: \"name from to [evalue]\"");
					return null;
				} catch (WrongFormatException wfe) {
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

		return prot;
	}
	
	/**
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
		String domFamilyID = token[actToken+2];
		DomainType dType = DomainType.getType(domFamilyID);
		domFamily = GatheringThresholdsReader.getInstance().get(domFamilyID);
		
		if (domFamily == null) { 				
			domFamily = new DomainFamily(domFamilyID, null, dType);
//			domFamily.setPfamID(pfamID);
			GatheringThresholdsReader.getInstance().put(domFamilyID, domFamily);
		}
		
		// "from", "to" must be the first two tokens
		int from = Integer.parseInt(token[actToken]);
		int to = Integer.parseInt(token[actToken+1]);
		
		// create domain
		dom = new Domain(from, to, domFamily);
				
		// check if the e-value is present within the format
		if (token.length == actToken+3)
		{
		 prot.addDomain(dom);
	     return prot;
		}

		// if so... parse it
		double evalue = Double.parseDouble(token[actToken+3]);
		dom.setEvalue(evalue);

        //Also record if the domain is putative or not (known)
		if (token.length == actToken+5)
        {
		 String[] comment = token[actToken+4].split(";");
		 for(int i=1; i< comment.length; i++)
		 {
		  if(comment[i].equals("putative") || comment[i].equals("asserted"))
	      {
	       dom.setPutative(comment[i].equals("putative"));
		  }else
	      {
		   if(comment[i].equals("hidden"))
		   {
	        prot.addDomain(dom);
	        prot.hideDomain(dom);
		   }   
	      }
		 }
        }
		if(!prot.getHiddenDoms().contains(dom))
		 prot.addDomain(dom);
		return prot;
	}
	
	private class WrongFormatException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public WrongFormatException () {
		    super("Wrong Format! Not enough tokens in domain line");
		}
	}
}

