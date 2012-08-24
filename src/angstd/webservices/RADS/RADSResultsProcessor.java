package angstd.webservices.RADS;

import info.radm.radscan.RADSResults;
import info.radm.radscan.ds.RADSDomain;
import info.radm.radscan.ds.RADSProtein;

import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.model.arrangement.io.GatheringThresholdsReader;
import angstd.ui.util.MessageUtil;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResultsProcessor {

	private RADSResults results;
	private TreeSet<RADSProtein> proteins;
	private JPanel panel;
	private ArrangementManager arrSet;
	private RADSPanelI radsPanel;
	private JProgressBar progressBar;
	private RADSService radsService; 	 
	
	public RADSResultsProcessor(RADSPanelI radsPanel) {
		this.radsPanel = radsPanel;
		this.progressBar = radsPanel.getProgressBar();
		this.radsService = radsPanel.getRadsService();
		this.proteins = radsService.getHits();
		this.results = radsService.getScanResults();
	}
	
	public ArrangementManager process() {
		if (proteins == null) {
			progressBar.setIndeterminate(false);
			MessageUtil.showInformation(radsPanel.getParentFrame(), "No hits found");
			//radsPanel.close(false);
			return null;
		}
		progressBar.setIndeterminate(false);
		progressBar.setMaximum(proteins.size());
		progressBar.setValue(0);
		int i = 1;

		ArrangementManager arrSet = new ArrangementManager();
		DomainArrangement da; 
		for (RADSProtein p: proteins) {
			progressBar.setValue(i);
			progressBar.setString("Processing hit "+i+ " of "+progressBar.getMaximum());
			da = new DomainArrangement();
			da.setName(p.getID());
			da.setSeqLen(p.getLength());
			
			for (RADSDomain resDom: p.getDomains()) {
			
				String acc = resDom.getID();
				DomainFamily domFamily = GatheringThresholdsReader.getInstance().get(acc);
				if (domFamily == null) {
					domFamily = new DomainFamily(acc, acc, DomainType.getType(acc));
					GatheringThresholdsReader.getInstance().put(acc, domFamily);
				}
				int from = resDom.getFrom();
				int to = resDom.getTo();
				double evalue = resDom.getEvalue();
				Domain dom = new Domain(from, to, domFamily);
				if (evalue != -1)
					dom.setEvalue(evalue);
				da.addDomain(dom);
			}
			// TODO: consider implementing comparable to avoid this
			// (ie. define natural order)
			da.sortDomains();
			arrSet.add(da);
			i++;
		}
		return arrSet;
	}
	
	
}
