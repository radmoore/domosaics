package angstd.model.sequence.io;

import java.io.BufferedWriter;
import java.io.IOException;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
