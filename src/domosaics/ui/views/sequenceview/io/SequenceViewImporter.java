package domosaics.ui.views.sequenceview.io;

import java.io.BufferedReader;
import java.io.StringReader;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.view.io.ViewImporter;

public class SequenceViewImporter extends ViewImporter<SequenceView>{
	private static final int DEFAULTSETTINGS = 0;
	private static final int LAYOUTSETTINGS = 1;

	public SequenceView readData(String data) {
		SequenceView res = null;
		
		SequenceI[] seqs = (SequenceI[]) new FastaReader().getDataFromString(data);
		if (seqs == null)
			return null;
		
		res = ViewHandler.getInstance().createView(ViewType.SEQUENCE, viewName);
		res.setSeqs(seqs);
		return res;
	}
	
	public void readAttributes(String attributes, SequenceView view)  {
		try {
			BufferedReader in = new BufferedReader(new StringReader(attributes)); 
			String line;
			int flag = -1; 		
			
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				
				if (line.startsWith("#"))			// ignore comments
					continue;
				
				// set the importer flag depending on actual entry
				if (line.toUpperCase().contains("<DEFAULTSETTINGS>")) {
					flag = DEFAULTSETTINGS;
					continue;
				}
				
				if (line.toUpperCase().contains("<LAYOUTSETTINGS>")) {
					flag = LAYOUTSETTINGS;
					continue;
				}
				
				// if it is not a parameterline, skip it
				if (!line.contains("parameter"))	
					continue;
				
				// read actual line in context to the setted flag
				switch (flag) {
					case DEFAULTSETTINGS: readDefaultSetting(line, view); break;
					case LAYOUTSETTINGS:  readLayoutSetting(line, view); break;
					default: continue;
				}	
			}
			
			in.close();
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else {
				Configuration.getLogger().debug(e.toString());
				Configuration.getLogger().debug("Error occured during project import - reading attribute file for view: "+view.getViewInfo().getName());
			}
		}
		
	}
	
	private static void readDefaultSetting(String line, SequenceView view) {
		// no default settings implemented yet
	}
	
	private static void readLayoutSetting(String line, SequenceView view) {
		// no layout settings implemented yet
	}

	@Override
	protected void setLayoutSettings(SequenceView view) {
		// TODO Auto-generated method stub
	}
		
}
