package domosaics.webservices.clustalw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domosaics.model.configuration.Configuration;




public class ClustalW2ToolOutputParser {
	private static final int ID2NAME = 0;
	private static final int ID2SCORE = 1;
	
	// the query sequence has to be the first sequence!!!
	public Map<String, Integer> parseResult(String tooloutput) {
		try {
			return parse(new StringReader(tooloutput));
		} 
		catch (IOException ioe) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(ioe);
			else			
				Configuration.getLogger().debug(ioe.toString());
		}
		return null;
	}

	public Map<String, Integer> parse(Reader reader) throws IOException {
		// id, seqName 	
		Map<String, String> id2name = new HashMap<String, String>();
		Map<String, Integer> id2score = new HashMap<String, Integer>();
		
		BufferedReader in = new BufferedReader(reader); 
		String line;
		
		int mode = ID2NAME;
		
		while((line = in.readLine()) != null) {
			if (line.isEmpty())					// ignore empty lines
				continue;
			
			if (line.contains("Aligning...")) {
				mode = ID2SCORE;
				continue;
			}
			
			if (mode == ID2NAME) {
				if (!line.contains("Sequence") || line.contains("format") || line.contains("Multiple"))
					continue;
				
				String[] token = line.split("\\s+"); //1->id: 2->name
				id2name.put(token[1].replace(":", ""), token[2]);
				continue;
			}
			
			if (mode == ID2SCORE) {
				if (!line.contains("Sequences (1"))
					continue;
				String[] token = line.split("\\s+"); // 1 -> (1:x) 4->score
				String id = token[1].substring(3, token[1].length()-1);
				int score = Integer.parseInt(token[4]);
				
				id2score.put(id, score);
				continue;
			}
		}
		
		// now map the names to the scores
		Map<String, Integer> name2score = new HashMap<String, Integer>();
		Iterator<String> iter = id2name.keySet().iterator();
		while(iter.hasNext()) {
			String id = iter.next();
			name2score.put(id2name.get(id), id2score.get(id));
		}
		
		in.close();
		return name2score;
	}
}
