package domosaics.model.sequence.io;

import java.io.BufferedWriter;
import java.io.IOException;

import domosaics.model.configuration.Configuration;
import domosaics.model.io.AbstractDataWriter;
import domosaics.model.sequence.SequenceI;




/**
 * Class to convert Sequences into Fasta Format.
 * 
 * @author Andreas Held
 *
 */
public class FastaWriter extends AbstractDataWriter<SequenceI> {
	
	/**
	 * Writes sequences into a Fasta formatted file.
	 * 
	 * @param out
	 * 		the stream used to export the sequences
	 * @param seqs
	 * 		the sequences to be saved.
	 */
	@Override
	public void write(BufferedWriter out, SequenceI[] seqs) {
        try {
    		for (int i = 0; i < seqs.length; i++) {
    			out.write(">"+seqs[i].getName()+"\r\n");
    			out.write(seqs[i].getSeq(true));
    			out.write("\r\n");
    		}
    		out.flush();  
        } 
        catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
        }
    }
	
	@Override
	public void wrappedWrite(BufferedWriter out, SequenceI[] seqs, int wrapChars) {
		try {
    		for (int i = 0; i < seqs.length; i++) {
    			
    			int pos = 0; // number of written chars
    			
    			out.write(">"+seqs[i].getName()+"\n");
    			byte[] chars = seqs[i].getSeq(false).getBytes();
    			
    			for (int j = 0; j < chars.length; j++) {
    				if (pos >= wrapChars) {
    					pos = 0;
    					out.write("\n");
    				}
    				out.write(chars[j]);
    				pos ++;
    			}

    			out.write("\n");
    		}
    		out.flush();
			
		}
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
	}

	// currently not needed.
	@Override
	public void writeSimple(BufferedWriter out, SequenceI[] data) {	}
	
	
}
