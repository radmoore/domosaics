package domosaics.ui.views.domainview.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.io.XdomWriter;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.manager.DomainLayoutManager;
import domosaics.ui.views.view.io.ViewExporter;



public class DomainViewExporter extends ViewExporter<DomainViewI> {

	public void write(BufferedWriter out, DomainViewI view) {
        try {
        	writeTag(out, 1, "DOMAINVIEW", true);
			writeParam(out, 2, "VIEWNAME", view.getViewInfo().getName());
			
			
			writeTag(out, 2, "DATA", true);
			
			// store associated sequences if there are any
			if (view.isSequenceLoaded()) {
				writeTag(out, 3, "SEQUENCEDATA", true);
				SequenceI[] seqs = view.getSequences();
				new FastaWriter().write(out, seqs);
				writeTag(out, 3, "SEQUENCEDATA", false);
			}
			
			writeTag(out, 3, "DOMAINDATA", true);
			new XdomWriter().write(out, view.getDaSet());
			writeTag(out, 3, "DOMAINDATA", false);
			
			writeTag(out, 2, "DATA", false);
			writeTag(out, 2, "ATTRIBUTES", true);
			
			// layout options
    		writeTag(out, 3, "LAYOUTSETTINGS", true);
    		
    		writeParam(out, 4, "VIEWLAYOUT", 	layout2String(view.getDomainLayoutManager()));
    		writeParam(out, 4, "FITTOSCREEN", 	""+view.getDomainLayoutManager().isFitDomainsToScreen());
    		writeParam(out, 4, "EVALCOLOR", 	""+view.getDomainLayoutManager().isEvalueColorization());
    		writeParam(out, 4, "SHOWSHAPES", 	""+view.getDomainLayoutManager().isShowShapes());
    		
    		writeTag(out, 3, "LAYOUTSETTINGS", false);
   
    		// domain settings
    		writeTag(out, 3, "DOMAINSETTINGS", true);
    		
    		ArrangementManager manager = new ArrangementManager();
    		manager.add(view.getDaSet());
    		Iterator<DomainFamily> famIter = manager.getFamilyIterator();
    		while (famIter.hasNext()) {
    			DomainFamily fam = famIter.next();
        		writeTag(out, 4, "DOMAINFAMILY", true);
        		writeParam(out, 5, "ACC", fam.getName());
        		writeParam(out, 5, "COLOR", color2str(view.getDomainColorManager().getDomainColor(fam)));
        		writeParam(out, 5, "SHAPE", ""+view.getDomainShapeManager().getShapeID(fam));
        		writeTag(out, 4, "DOMAINFAMILY", false);
        	}
    		
        	writeTag(out, 3, "DOMAINSETTINGS", false);
			writeTag(out, 2, "ATTRIBUTES", false);
			writeTag(out, 1, "DOMAINVIEW", false);

        	out.flush();
        } 
        catch (IOException e) {
        	Configuration.getLogger().debug(e.toString());
        }
    }
	
	private String layout2String(DomainLayoutManager manager) {
		if (manager.isProportionalView())
			return "PROPORTIONAL";
		else if (manager.isUnproportionalView())
			return "UNPROPORTIONAL";
		else if (manager.isMsaView()) 
			return "MSA";
		else
			return "PROPORTIONAL";
		
	}
		
}
