package angstd.webservices.RADS;

import info.radm.radscan.Parser;
import info.radm.radscan.QueryBuilder;
import info.radm.radscan.RADSQuery;
import info.radm.radscan.RADSResults;
import info.radm.radscan.RADSRunner;
import info.radm.radscan.ds.Protein;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeSet;

import javax.swing.SwingWorker;

import angstd.model.arrangement.DomainArrangement;

public class RadsService extends SwingWorker<TreeSet<Protein>, Void> {
	
	private RADSResults results;
	private RADSRunner radsRunner;
	private RADSQuery radsQuery;
	private DomainArrangement queryProtein;
	private Parser resultParser;
	private TreeSet<Protein> proteins = null;
	private boolean running;
	
	public RadsService(RADSQuery radsQuery, DomainArrangement queryProtein) {
		this.radsQuery = radsQuery;
		this.queryProtein = queryProtein;
		running = false;
	}
	
	protected TreeSet<Protein> doInBackground() throws Exception {
		this.radsRunner = new RADSRunner(radsQuery);
		results = radsRunner.submit();
		resultParser = new Parser(results);
		return resultParser.parse();
	}
	
	public void done() {
		try {
			proteins = get();
		}
		catch (Exception e) {};
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public TreeSet<Protein> getHits() {
		return proteins;
	}
	
	public DomainArrangement getQueryProtein() {
		return this.queryProtein;
	}
	
	public RADSResults getScanResults() {
		return this.results;
	}
//
//	@Override
//	public void propertyChange(PropertyChangeEvent evt) {
//		if ("state".equals(evt.getPropertyName())) {
//			if ( "DONE".equals(evt.getNewValue().toString()) ) {
//				System.out.println("Scan Complete.");
//				runScan.setEnabled(false);
//				radsRunning = false;
//				processResults();
//			}
//		}
//		
//	}
	
}
