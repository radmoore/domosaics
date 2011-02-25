package angstd.model.arrangement.io;

import java.io.BufferedWriter;
import java.io.IOException;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.io.AbstractDataWriter;
import angstd.model.io.DataWriter;
import angstd.util.StringUtils;

/**
 * Class XdomWriter produces xdom format from an arrangement view.
 * 
 * @author Andreas Held
 *
 */
public class XdomWriter extends AbstractDataWriter<DomainArrangement>{

	/**
	 * See interface {@link DataWriter}.
	 */
	public void write(BufferedWriter out, DomainArrangement[] daSet) {
        try {
    		for (int i = 0; i < daSet.length; i++) {
    			
    			// start header
    			out.write("> ");
    			
    			// write name
    			out.write(StringUtils.convertSpaces(daSet[i].getName()));
    			
    			// write sequence length, of course without gaps
    			out.write("\t"+daSet[i].getLen(false));
    			
    			// if there is a description write it as well
    			if (daSet[i].getDesc() != null)
    				out.write("\t"+daSet[i].getDesc());
    			
    			// end header
    			out.write("\r\n");
    					
    			// write domain lines		
    			for (int j = 0; j < daSet[i].countDoms(); j++) {
    				Domain dom = daSet[i].getDomain(j);
    				out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(dom.getID()));
    				if (dom.getEvalue() != Double.POSITIVE_INFINITY)
    					out.write("\t"+dom.getEvalue());
                    if (dom.isPutative()) // Specifying the putative aspect
    					out.write("\t;putative");
    				else
    					out.write("\t;asserted");
    				out.write("\r\n");
    			}
    			
    			// write hidden domain lines		
    			for (Domain dom : daSet[i].getHiddenDoms()) {
    				out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(dom.getID()));
    				if (dom.getEvalue() != Double.POSITIVE_INFINITY)
    					out.write("\t"+dom.getEvalue());
                    if (dom.isPutative()) // Specifying the putative aspect
    					out.write("\t;putative");
    				else
    					out.write("\t;asserted");
    				out.write("\t hidden\r\n");
    			}
    			
    			out.flush(); 
    		}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Implemention required as defined by {@link DataWriter}. The wrapAfter int
	 * has no effect (line wrapping is not required in xdoms)
	 */
	public void wrappedWrite(BufferedWriter out, DomainArrangement[] data, int wrapAfter) {
		write(out, data);
		
	}
	
}
