package domosaics.ui.views.sequenceview.io;

import java.io.BufferedWriter;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Element;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.view.io.ViewExporter;


/**
 * Class exporting a sequence view into a XML formatted file.
 * 
 * @author Andreas Held
 *
 */
public class SequenceViewExporter extends ViewExporter<SequenceView> {

	public void write(BufferedWriter out, SequenceView view) {
        try {
        	writeTag(out, 0, "SEQUENCEVIEW", true);
			writeParam(out, 1, "VIEWNAME", view.getViewInfo().getName());
			
			writeTag(out, 1, "DATA", true);
			new FastaWriter().write(out, view.getSeqs());
			writeTag(out, 1, "DATA", false);
			
			writeTag(out, 1, "ATTRIBUTES", true);
		
        	writeTag(out, 2, "DEFAULTSETTINGS", true);
        	writeTag(out, 2, "DEFAULTSETTINGS", false);
    	
    		writeTag(out, 2, "LAYOUTSETTINGS", true);
    		writeTag(out, 2, "LAYOUTSETTINGS", false);
    		
			writeTag(out, 1, "ATTRIBUTES", false);
			writeTag(out, 0, "SEQUENCEVIEW", false);

    		out.flush();
        } 
        catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
        }
    }
}
