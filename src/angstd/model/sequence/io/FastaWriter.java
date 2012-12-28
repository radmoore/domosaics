package angstd.model.sequence.io;

import java.io.BufferedWriter;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Element;

import angstd.model.configuration.Configuration;
import angstd.model.io.AbstractDataWriter;
import angstd.model.sequence.SequenceI;



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
        	Configuration.getLogger().debug(e.toString());
        }
    }
	
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
			System.out.println("*** Error writing fasta file:");
			e.printStackTrace();
		}
	}
	
	
}
