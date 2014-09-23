package domosaics.webservices.RADS;

import info.radm.radscan.model.RADSDomain;
import info.radm.radscan.model.RADSProtein;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JProgressBar;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.DomainType;
import domosaics.model.arrangement.io.GatheringThresholdsReader;
import domosaics.webservices.RADS.util.RADSResultsTableModel;


/**
 * This class describes a RADSResultsProcessor, which performs
 * the parsing of RADSScan Results. It has access to the RADSPanel
 * (via {@link RADSPanelI}) to indicate the progression of the parsing,
 * and to communicate with the ScanPanel (i.e. show Popups etc).
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResultsProcessor {

	private TreeSet<RADSProtein> proteins;
	private RADSPanelI radsPanel;
	private JProgressBar progressBar;
	private RADSService radsService; 	 
	
	/**
	 * Constructs a new RADSResultsProcessor
	 * 
	 * @param radsPanel - the RADSPanel from which the scan was invoked 
	 */
	public RADSResultsProcessor(RADSPanelI radsPanel) {
		this.radsPanel = radsPanel;
		this.progressBar = radsPanel.getProgressBar();
		this.radsService = radsPanel.getRadsService();
		
		this.proteins = radsService.getHits();
	}
	
	//TODO create TableModel from results
	public RADSResultsTableModel createResultTable() {
		
		if (proteins == null)
			return null;

		progressBar.setIndeterminate(false);
		progressBar.setMaximum(proteins.size());
		progressBar.setValue(0);
		int i = 1;
		int tableIndex = 0;
		
		RADSResultsTableModel resultTableModel = new RADSResultsTableModel();
		HashMap<String, DomainArrangement> arrangementData = new HashMap<String, DomainArrangement>(); 
		DomainArrangement da;
		String algo = radsService.getScanResults().getQuery().getAlgorithm();
		if (algo.equals("rads"))
			resultTableModel.setTableMode(RADSResultsTableModel.RADS_MODE);
		else
			resultTableModel.setTableMode(RADSResultsTableModel.RAMPAGE_MODE);
		
		Object[][] tableData = new Object[proteins.size()][resultTableModel.getColumnCount()];
		
		for (RADSProtein p: proteins) {
			
			//queryID+"\t"+p.getID()+"\t"+p.getRADSScore()
//			System.out.println("This is the protein: "+p.getID()+", score: "+p.getRADSScore());
			
			progressBar.setValue(i);
			da = new DomainArrangement();
			da.setName(p.getID());
			da.setSeqLen(p.getLength());
			
			for (RADSDomain resDom: p.getDomains()) {
				DomainFamily domFamily = null;
				Vector<String> df = GatheringThresholdsReader.getIDFromName(resDom.getID());
				if (df != null) {
					if (df.size() == 1)
						domFamily = GatheringThresholdsReader.getInstance().get(df.firstElement());
					else {
						Iterator<String> iS = df.iterator();
						while (iS.hasNext()) {
							domFamily = GatheringThresholdsReader.getInstance().get(iS.next());
							if (DomainType.getType(domFamily.getId()).getName().equals("Pfam"))
								break;
						}
					}	
				}
				if (domFamily == null) {
					domFamily = new DomainFamily(resDom.getID(), resDom.getID(), DomainType.getType(resDom.getID()));
					GatheringThresholdsReader.add(domFamily);
				}
				int from = resDom.getFrom();
				int to = resDom.getTo();
				double evalue = resDom.getEvalue();
				Domain dom = new Domain(from, to, domFamily);
				if (evalue != -1)
					dom.setEvalue(evalue);
				da.addDomain(dom);
			}
			da.sortDomains();
			arrangementData.put(p.getID(), da);
			tableData[tableIndex][0] = i;
			tableData[tableIndex][1] = new Boolean(false);
			tableData[tableIndex][2] = p.getID();
			if (resultTableModel.getTableMode() == RADSResultsTableModel.RADS_MODE) {
				tableData[tableIndex][3] = p.getRADSScore();
				tableData[tableIndex][4] = p.getArrString();
			}
			else {
				tableData[tableIndex][3] = p.getRADSScore();
				tableData[tableIndex][4] = p.getRAMPAGEScore();
				tableData[tableIndex][5] = p.getArrString();
			}
			tableIndex++;
			
			i++;
		}
		resultTableModel.setTableData(tableData);
		resultTableModel.setArrangementData(arrangementData);
		return resultTableModel;
	}
	
	/**
	 * This methods parses the RADS results. Results are obtained
	 * via the RADSPanel
	 * 
	 * @return - an arrangement manager containing the scan results,
	 * or null if no results were found
	 */
	public ArrangementManager process() {
		if (proteins == null) {
			return null;
		}
		progressBar.setIndeterminate(false);
		progressBar.setMaximum(proteins.size());
		progressBar.setValue(0);
		int i = 1;

		//ProjectElement project = WorkspaceManager.getInstance().getViewElement(radsPanel.getView().getViewInfo()).getProject();
		//DomainViewI view =  ViewHandler.getInstance().getView(viewElt.getViewInfo());
		//DomainViewI domView = ViewHandler.getInstance().getView(radsPanel.getView().getViewInfo()) ;
		
		
		ArrangementManager arrSet = new ArrangementManager();
		DomainArrangement da; 
		for (RADSProtein p: proteins) {
			
			progressBar.setValue(i);
			da = new DomainArrangement();
			da.setName(p.getID());
			da.setSeqLen(p.getLength());
			
			for (RADSDomain resDom: p.getDomains()) {
				DomainFamily domFamily = null;
				Vector<String> df = GatheringThresholdsReader.getIDFromName(resDom.getID());
				if(df!=null) {
					if(df.size()==1)
						domFamily = GatheringThresholdsReader.getInstance().get(df.firstElement());
					else {
						Iterator<String> iS=df.iterator();
						while(iS.hasNext()) {
							domFamily = GatheringThresholdsReader.getInstance().get(iS.next());
							if(DomainType.getType(domFamily.getId()).getName().equals("Pfam"))
								break;
						}
					}
				}
				if (domFamily == null) {
					domFamily = new DomainFamily(resDom.getID(), resDom.getID(), DomainType.getType(resDom.getID()));
					GatheringThresholdsReader.getInstance().put(resDom.getID(), domFamily);
				}
				int from = resDom.getFrom();
				int to = resDom.getTo();
				double evalue = resDom.getEvalue();
				Domain dom = new Domain(from, to, domFamily);
				if (evalue != -1)
					dom.setEvalue(evalue);
				da.addDomain(dom);
			}
			da.sortDomains();
			arrSet.add(da);
			i++;
		}
		return arrSet;
	}
	

	
	
}
