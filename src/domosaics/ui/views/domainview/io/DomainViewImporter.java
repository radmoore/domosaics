package domosaics.ui.views.domainview.io;

import java.io.BufferedReader;
import java.io.StringReader;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.io.XdomReader;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.layout.MSALayout;
import domosaics.ui.views.domainview.layout.ProportionalLayout;
import domosaics.ui.views.domainview.layout.UnproportionalLayout;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.MsaArrangementRenderer;
import domosaics.ui.views.view.io.ViewImporter;
import domosaics.ui.views.view.layout.ViewLayout;

public class DomainViewImporter extends ViewImporter<DomainViewI> {
	// settings for the actual domain family
	private static DomainFamily fam = null;
	private static ViewLayout domLayout;
	
	public DomainViewI readData(String data) {
		try {
			BufferedReader in = new BufferedReader(new StringReader(data));
			String line;
		
			DomainViewI view = null;
			SequenceI[] seqs = null;		// associated sequences
			
			StringBuffer seqData = new StringBuffer();
			StringBuffer domainData = new StringBuffer();
			
			while((line = in.readLine()) != null) {
				
				if(line.contains("<SEQUENCEDATA>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</SEQUENCEDATA>")) 
						seqData.append(line+"\r\n");
					seqs = (SequenceI[]) new FastaReader().getDataFromString(seqData.toString());
				}
				
				if(line.contains("<DOMAINDATA>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</DOMAINDATA>")) 
						domainData.append(line+"\r\n");
					
					DomainArrangement[] proteins = (DomainArrangement[]) new XdomReader().getDataFromString(domainData.toString());
					if (proteins == null)
						return null;

					// this is where the view is created
					view = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
					view.setDaSet(proteins);
					
					if (seqs != null) 
						view.loadSequencesIntoDas(seqs, proteins);
				}
			}
			return view;
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return null;
		}
	}
	
	public void setLayoutSettings(DomainViewI view) {
		if (domLayout == null)
			return;
		
		// keep the domain layout manager up to date if the layout was set while importing a view
		if (domLayout instanceof ProportionalLayout && !view.getDomainLayoutManager().isProportionalView()) {
			view.setViewLayout(new ProportionalLayout());
			view.getDomainLayoutManager().setToProportionalView();
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.registerMouseListeners();
		}
		if (domLayout instanceof UnproportionalLayout && !view.getDomainLayoutManager().isUnproportionalView()) {
			view.setViewLayout(new UnproportionalLayout());
			view.getDomainLayoutManager().setToUnproportionalView();
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.registerMouseListeners();
		}
			
		if (domLayout instanceof MSALayout && !view.getDomainLayoutManager().isMsaView()) {
			view.setViewLayout(new MSALayout());
			view.getDomainViewRenderer().setArrangementRenderer(new MsaArrangementRenderer());		
			view.getDomainLayoutManager().setToMsaView();
			view.registerMouseListeners();
		}
	}
	
	public void readAttributes(String attributes, DomainViewI view)  {
		try {
			BufferedReader in = new BufferedReader(new StringReader(attributes));
			String line;
				
			ArrangementManager manager = new ArrangementManager();
			manager.add(view.getDaSet());
			
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				if (line.startsWith("#"))			// ignore comments
					continue;
				
				// set the importer flag depending on actual entry
				if (line.toUpperCase().contains("<LAYOUTSETTINGS>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</LAYOUTSETTINGS>")) 
						readLayoutSetting(line, view); 
				}
				
				if (line.toUpperCase().contains("<DOMAINSETTINGS>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</DOMAINSETTINGS>")) {
						// if it is not a parameterline, skip it
						if (!line.contains("parameter"))	
							continue;
						
						readFamilySetting(line, view, manager);
					}		
				}
			}
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else {			
				Configuration.getLogger().debug(e.toString());
				Configuration.getLogger().debug("Error occured during project import - reading attribute file for view: "+view.getViewInfo().getName());
			}
		}
	}

	private static void readLayoutSetting(String line, DomainViewI view) {
		if(idEquals(line, "VIEWLAYOUT")) {
			if (getValue(line).equals("PROPORTIONAL"))
				domLayout = new ProportionalLayout();
			else if(getValue(line).equals("UNPROPORTIONAL")) 
				domLayout = new UnproportionalLayout();
			else if(getValue(line).equals("MSA"))
				domLayout = new MSALayout();
			else 
				domLayout = new ProportionalLayout();
				
		} else {
			if(idEquals(line, "FITTOSCREEN"))
				view.getDomainLayoutManager().setFitDomainsToScreen(str2boolean(getValue(line)));
			else
				if(idEquals(line, "EVALCOLOR"))
					view.getDomainLayoutManager().setEvalueColorization(str2boolean(getValue(line)));
				else
					if(idEquals(line, "SHOWSHAPES"))
						view.getDomainLayoutManager().setShowShapes(str2boolean(getValue(line)));
		}
	}
		
	private static void readFamilySetting(String line, DomainViewI view, ArrangementManager manager) {
		if(idEquals(line, "ACC"))
			fam = manager.getFamily(getValue(line));
		else if(idEquals(line, "COLOR"))
			view.getDomainColorManager().setDomainColor(fam, str2color(getValue(line)));
		else if(idEquals(line, "SHAPE")) 
			view.getDomainShapeManager().setDomainShape(fam, str2int(getValue(line)));
	}
	
}
