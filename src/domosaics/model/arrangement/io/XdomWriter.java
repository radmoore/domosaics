package domosaics.model.arrangement.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import domosaics.model.GO.GeneOntologyTerm;
import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.configuration.Configuration;
import domosaics.model.io.AbstractDataWriter;
import domosaics.model.io.DataWriter;
import domosaics.util.StringUtils;




/**
 * Class XdomWriter produces xdom format from an arrangement view.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
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
    			// 41	90	PPV_E2_C	0.054	;putative;hidden
    			for (int j = 0; j < daSet[i].countDoms(); j++) {
    				Domain dom = daSet[i].getDomain(j);
    				DomainFamily dFam = dom.getFamily();
    				String domInfo = dom.getID()+";"+dom.getName();
    				//out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(dom.getAcc()));
    				out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(domInfo));
    				if (dom.getEvalue() != Double.POSITIVE_INFINITY)
    					out.write("\t"+dom.getEvalue());
                    if (dom.isPutative()) // Specifying the putative aspect
    					out.write("\t;putative");
    				else
    					out.write("\t;asserted");
                    if (dFam.hasGoAnnotation()) {
                    	Iterator<GeneOntologyTerm> iter = dFam.getGoTerms();
                    	String gTermStr = "";  
                    	while(iter.hasNext()) {
                    		GeneOntologyTerm term = iter.next();
                    		gTermStr += ";"+term.getID();
                    	}
                    	out.write(gTermStr);
                    }
                    	
                    
    				out.write("\r\n");
    			}
    			
    			// write hidden domain lines		
    			for (Domain dom : daSet[i].getHiddenDoms()) {
    				DomainFamily dFam = dom.getFamily();
    				String domInfo = dom.getID()+";"+dom.getID();
    				out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(domInfo));
    				if (dom.getEvalue() != Double.POSITIVE_INFINITY)
    					out.write("\t"+dom.getEvalue());
                    if (dom.isPutative()) // Specifying the putative aspect
    					out.write("\t;putative");
    				else
    					out.write("\t;asserted");
                    
                    if (dFam.hasGoAnnotation()) {
                    	Iterator<?> iter = dFam.getGoTerms();
                    	String gTermStr = "";  
                    	while(iter.hasNext()) {
                    		GeneOntologyTerm term = (GeneOntologyTerm)iter.next();
                    		gTermStr += ";"+term.getID();
                    	}
                    	out.write(gTermStr);
                    }
    				out.write(";hidden\r\n");
    			}
    			
    			out.flush(); 
    		}
        } 
        catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
        }
    }
	
	/**
	 * The same as write(), expect that the simplest possible format is written
	 * (no putative domains, no hidden domains, no confirmed etc, no domain
	 * description). Note also that this export will always export the 
	 * domain name (not the ID).
	 * 
	 * @param out
	 * @param daSet
	 */
	public void writeSimple(BufferedWriter out, DomainArrangement[] daSet) {
        try {
        	
    		for (int i = 0; i < daSet.length; i++) {
    			
    			// start header
    			out.write(">");
    			
    			// write name
    			out.write(StringUtils.convertSpaces(daSet[i].getName()));
    			
    			// write sequence length, of course without gaps
    			out.write("\t"+daSet[i].getLen(false));
    			
    			// end header
    			out.write("\r\n");
    					
    			// write domain lines
    			for (int j = 0; j < daSet[i].countDoms(); j++) {
    				Domain dom = daSet[i].getDomain(j);
    				DomainFamily dFam = dom.getFamily();
    				out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(dom.getName()));
    				//out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(domInfo));
    				if (dom.getEvalue() != Double.POSITIVE_INFINITY)
    					out.write("\t"+dom.getEvalue());
    				out.write("\r\n");
    			}
    			
    			/**
    			// write hidden domain lines		
    			for (Domain dom : daSet[i].getHiddenDoms()) {
    				DomainFamily dFam = dom.getFamily();
    				String domInfo = dom.getID()+";"+dom.getID();
    				out.write(dom.getFrom()+"\t"+dom.getTo()+"\t"+StringUtils.convertSpaces(domInfo));
    				if (dom.getEvalue() != Double.POSITIVE_INFINITY)
    					out.write("\t"+dom.getEvalue());
                    if (dom.isPutative()) // Specifying the putative aspect
    					out.write("\t;putative");
    				else
    					out.write("\t;asserted");
                    
                    if (dFam.hasGoAnnotation()) {
                    	Iterator<?> iter = dFam.getGoTerms();
                    	String gTermStr = "";  
                    	while(iter.hasNext()) {
                    		GeneOntologyTerm term = (GeneOntologyTerm)iter.next();
                    		gTermStr += ";"+term.getID();
                    	}
                    	out.write(gTermStr);
                    }
    				out.write(";hidden\r\n");
    			}
    			**/
    			
    			out.flush(); 
    		}
        } 
        catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
        }
        catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else
				Configuration.getLogger().debug(e.toString());
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
