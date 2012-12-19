package domosaics.webservices.clustalw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.Sequence;
import domosaics.model.sequence.SequenceI;


public class ClustalW2ResultParser {
	
	public SequenceI[] parseResult(String alignmentStr) {
		try {
			return getSequences(new StringReader(alignmentStr));
		}
		catch (IOException ioe) {
			Configuration.getLogger().debug(ioe.toString());
		}
		return null;
	}

	public SequenceI[] getSequences(Reader reader) throws IOException {
			
		Map<String, String> seqMap = new HashMap<String, String>();
		
		// parse each line of the document
		BufferedReader in = new BufferedReader(reader); 
		String line;
		while((line = in.readLine()) != null) {
			if (line.isEmpty())					// ignore empty lines
				continue;
			
			String[] token = line.split("\\s+");
			
//			if(token.length != 3 || token[0].isEmpty())
//				continue;
			if(token.length != 2 || token[0].isEmpty())
				continue;
			
			if (seqMap.get(token[0]) == null)
				seqMap.put(token[0], token[1]);
			else {
				String newVal = seqMap.get(token[0]).concat(token[1]);
				seqMap.put(token[0], newVal);
			}
		}

		SequenceI[] seqs = new SequenceI[seqMap.size()];
		Iterator<String> seqNames = seqMap.keySet().iterator();
		int count = 0;
		while(seqNames.hasNext()) {
			String name = seqNames.next();
			seqs[count] = new Sequence(name, seqMap.get(name)); 
			count++;
		}
		
		return seqs;
	}
}
