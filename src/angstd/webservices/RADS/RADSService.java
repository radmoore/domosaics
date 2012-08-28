package angstd.webservices.RADS;

import info.radm.radscan.RADSParser;
import info.radm.radscan.RADSQuery;
import info.radm.radscan.RADSResults;
import info.radm.radscan.RADSRunner;
import info.radm.radscan.ds.RADSProtein;

import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import angstd.model.arrangement.DomainArrangement;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSService extends SwingWorker<TreeSet<RADSProtein>, Void> {
	
	private RADSResults results;
	private RADSRunner radsRunner;
	private RADSQuery radsQuery;
	private DomainArrangement queryProtein = null;
	private RADSParser resultParser;
	private TreeSet<RADSProtein> proteins = null;
	private boolean running;

	public RADSService(RADSQuery radsQuery) {
		this.radsQuery = radsQuery;
		running = false;
	}
	
	public RADSService(RADSQuery radsQuery, DomainArrangement queryProtein) {
		this.radsQuery = radsQuery;
		this.queryProtein = queryProtein;
		running = false;
	}
	
	protected TreeSet<RADSProtein> doInBackground() throws Exception {
		running = true;
		this.radsRunner = new RADSRunner(radsQuery);
		results = radsRunner.submit();
		resultParser = new RADSParser(results);
		return resultParser.parse();
	}
	
	public void done() {
		try {
			running = false;
			proteins = get();
		}
		catch (Exception e) {};
	}
	
	public void setRunState(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public TreeSet<RADSProtein> getHits() {
		return proteins;
	}
	
	public boolean hasResults() {
		if ((proteins == null) || (proteins.size() == 0))
				return false;
		return true;
	}
	
	public DomainArrangement getQueryProtein() {
		return this.queryProtein;
	}
	
	public RADSResults getScanResults() {
		return this.results;
	}

	public void cancelScan() {
		this.cancel(true);
		running = false;
	}
	
	
}
