package angstd.model.sequence.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import angstd.model.io.AbstractDataReader;
import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.util.SeqUtil;
import angstd.ui.util.MessageUtil;

/**
 * Class FastaReader parses fasta formatted sequences based on an 
 * input stream.
 * 
 * @author Andreas Held
 * @author Andrew Moore
 * 
 */
public class FastaReader extends AbstractDataReader<SequenceI>{
	
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
						MessageUtil.showWarning("Can't determine the sequence format.");
						return null;
					}

					seq.setSeq(convertToAminoAcidSeq(seqBuf.toString(), type));
					seqs.add(seq);
				}
				firstRead = true;
				seq = new Sequence();
				seqBuf = new StringBuffer();
				seq.setName(getNameFromHeader(line));
			} else {							// add seq line
				// TODO make sure not to read line numbers if present
//				String[] token = line.split("\\s+");
//				seqBuf.append(token[token.length-1].toUpperCase());
				line = line.replaceAll("\\s+", "");
				seqBuf.append(line.toUpperCase());
			}
		}
		
		// add also the last protein
		if (type == SeqUtil.UNKNOWN) {
			type = SeqUtil.checkFormat(seqBuf.toString().replace("*", ""));
			if (type == SeqUtil.UNKNOWN) {
				MessageUtil.showWarning("Can't determine the sequence format.");
				return null;
			}
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
	private String getNameFromHeader(String header) {
		header = header.replace(">", "");
		String[] token = header.split("\\s+");
		
		// we are only interested in the sequence name, which is the first word within the header
		String name = token[0];
		
		// its possible that empty spaces occur here, so we have to split again
		String[] nameToken = name.split(" ");
		for (int i = 0; i < nameToken.length; i++)
			if (!nameToken[i].isEmpty()) 
				return nameToken[i];
		
		return nameToken[0];
	}
}
