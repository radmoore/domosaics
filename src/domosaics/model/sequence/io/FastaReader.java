package domosaics.model.sequence.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domosaics.model.configuration.Configuration;
import domosaics.model.io.AbstractDataReader;
import domosaics.model.sequence.Sequence;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.util.SeqUtil;
import domosaics.ui.util.MessageUtil;




/**
 * Class FastaReader parses fasta formatted sequences based on an 
 * input stream.
 * 
 * @author Andreas Held
 * @author Andrew Moore
 * 
 */
public class FastaReader extends AbstractDataReader<SequenceI>{
	
	public static boolean isValidFasta(File fastaFile) {
		
        BufferedReader inputStream = null;
        Pattern p = Pattern.compile("^$");
        Matcher emptyLine; 
        	
		// string buffer holding the actual sequence which can be over more than one line
		StringBuffer seqBuf = new StringBuffer();
		// actual line
		String line;		
		// flag indicating whether or not the first sequence was parsed
		boolean firstRead = false;
		
		// the initial sequence type before guessing the format
		int type = SeqUtil.UNKNOWN;

        try {
            inputStream = new BufferedReader(new FileReader(fastaFile));
            
            while ((line = inputStream.readLine()) != null) {
            	 emptyLine = p.matcher(line);

            	// ignore comments (and empty lines)
            	if ( line.startsWith(";") || emptyLine.matches())
            		continue;
            	
            	if (line.startsWith(">")) {		// parse header line
    				if (firstRead) {
    					
    					// guess the format
    					type = SeqUtil.checkFormat(seqBuf.toString().replace("*", ""));
    					if (type == SeqUtil.UNKNOWN) {
    						MessageUtil.showWarning("Can't determine the sequence format.");
    						return false;
    					}
    				}else {
    					firstRead = true;
    				}
    				seqBuf = new StringBuffer();
    				if(getNameFromHeader(line)==null) {
    					MessageUtil.showWarning("Error while parsing protein line.");
    					return false;
    				}
    			} else {
    				line = line.replaceAll("\\s+", "");
    				seqBuf.append(line.toUpperCase());
    			}
    		}
    		// add also the last protein
    		type = SeqUtil.checkFormat(seqBuf.toString().replace("*", ""));
    		if (type == SeqUtil.UNKNOWN) {
    			MessageUtil.showWarning("Can't determine the sequence format.");
    			return false;
    		}
		} catch(Exception e) {
			MessageUtil.showWarning("Error while parsing the file.");
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return false;
		}
		return true;
	}
	
	
	public SequenceI[] getData (Reader reader) throws IOException {
		
		// list of all parsed sequences
		List<SequenceI> seqs = new ArrayList<SequenceI>();
		
		// the actual sequence 
		SequenceI seq = new Sequence();
		
		// string buffer holding the actual sequence which can be over more than one line
		StringBuffer seqBuf = new StringBuffer();
		
		// reader stream
		BufferedReader in = new BufferedReader(reader); 
		
		// actual line
		String line;
		
		// flag indicating whether or not the first sequence was parsed
		boolean firstRead = false;
		
		// the initial sequence type before guessing the format
		int type = SeqUtil.UNKNOWN;
		
		// do the parsing now
		while((line = in.readLine()) != null) {
			if (line.isEmpty())				// skip empty lines
				continue;
			if (line.startsWith(";"))		// skip comment lines
				continue;
			if (line.startsWith(">")) {		// parse header line
				if (firstRead) {
					
					// guess the format
					type = SeqUtil.checkFormat(seqBuf.toString().replace("*", ""));
					if (type == SeqUtil.UNKNOWN) {
						System.out.println(seqBuf.toString());
						MessageUtil.showWarning("Can't determine the sequence format.");
						return null;
					}
					seq.setSeq(convertToAminoAcidSeq(seqBuf.toString(), type));
					seqs.add(seq);
				}else {
					firstRead = true;
				}
				seq = new Sequence();
				seqBuf = new StringBuffer();
				seq.setName(getNameFromHeader(line));
				if(seq.getName()==null) {
					MessageUtil.showWarning("Error while parsing protein line.");
					return null;
				}
					
			} else {							// add seq line
				// TODO make sure not to read line numbers if present
//				String[] token = line.split("\\s+");
//				seqBuf.append(token[token.length-1].toUpperCase());
				line = line.replaceAll("\\s+", "");
				seqBuf.append(line.toUpperCase());
			}
		}
		
		// add also the last protein
		type = SeqUtil.checkFormat(seqBuf.toString().replace("*", ""));
		if (type == SeqUtil.UNKNOWN) {
			MessageUtil.showWarning("Can't determine the sequence format.");
			return null;
		}
		seq.setSeq(convertToAminoAcidSeq(seqBuf.toString(), type));
		seqs.add(seq);
		
		return seqs.toArray(new Sequence[seqs.size()]);
	}
	
	private String convertToAminoAcidSeq(String seq, int type) {
		seq = seq.replace("*", "");
		switch(type) {
			case SeqUtil.PROT: 	return seq;
			case SeqUtil.RNA: 	return SeqUtil.rna2as(seq); 
			case SeqUtil.CDNA: 	return SeqUtil.cdna2amino(seq); 
			case SeqUtil.DNA: 	break;
			default: return null;
		}
		return null;
	}
	
	
	/**
	 * Parses the header line of a fasta sequence, 
	 * so that the sequence name is returned.
	 * 
	 * @param header
	 * 		header line
	 * @return
	 * 		sequence name.
	 */
	private static String getNameFromHeader(String header) {
		header = header.replace(">", "");
		String[] token = header.split("\\s+");
		if(token.length!=0) {
			// we are only interested in the sequence name, which is the first word within the header
			String name = token[0];
			
			// its possible that empty spaces occur here, so we have to split again
			String[] nameToken = name.split(" ");
			for (int i = 0; i < nameToken.length; i++)
				if (!nameToken[i].isEmpty()) 
					return nameToken[i];
			return nameToken[0];
		}else
		{
			return null;
		}
		
	}
}
