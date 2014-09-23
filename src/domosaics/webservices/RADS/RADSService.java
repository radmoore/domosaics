package domosaics.webservices.RADS;

import info.radm.radscan.RADSParser;
import info.radm.radscan.RADSQuery;
import info.radm.radscan.RADSResults;
import info.radm.radscan.RADSRunner;
import info.radm.radscan.model.RADSProtein;

import java.util.TreeSet;

import javax.swing.SwingWorker;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;




/**
 * The RADSService is the class that delegates the actual scan of a query
 * using RADS or RAMPAGE to the RADSRunner defined in the RADSScan lib. It
 * is of type SwingWorker, which allows the scan to run in a dedicated thread 
 * and supports messaging between the Panel and the RADSRunner.
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
	private static boolean running;

	/**
	 * Default constructor. Requires a RADSQuery on which the
	 * scan is to be executed
	 * 
	 * @param radsQuery - the Query on which to run the RADS scan
	 */
	public RADSService(RADSQuery radsQuery) {
		this.radsQuery = radsQuery;
		running = false;
	}
	
	/**
	 * The implementation of doInBackground() starts the scan
	 * in a seperate thread. It constructs a new instance of
	 * RADSRunner using the RADSQuery.
	 *
	 */
	@Override
	protected TreeSet<RADSProtein> doInBackground() throws Exception {
		running = true;
		this.radsRunner = new RADSRunner(radsQuery);
		results = radsRunner.submit();
		resultParser = new RADSParser(results);
		return resultParser.parse();
	}
	
	/**
	 * This method is called when the scanning thread is complete 
	 */
	@Override
	public void done() {
		try {
			running = false;
			proteins = get();
		}
		
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
	}
	
	/**
	 * Allows to set the run state from outside of the class.
	 * This is necessary as the scan is remote, and if interrupted
	 * it will continue to run on the server side until complete.
	 * By setting the state we can indicate that we no longer care
	 * about the outcome of the scan.
	 * 
	 * @param runState - boolean overriding the actual state of the scan
	 */
	public void setRunState(boolean runState) {
		running = runState;
	}
	
	/**
	 * Check the scan state
	 * @return - true if there is a scan running, false otherwise
	 */
	public static boolean isRunning() {
		return running;
	}
	
	/**
	 * Provides access to the hits of the scan
	 * @return - hit proteins found by scan
	 */
	public TreeSet<RADSProtein> getHits() {
		return proteins;
	}
	
	/**
	 * Allows to check whether a scan returned any results
	 * @return - true if a scan found hits or has not yet been started, false otherwise.
	 */
	public boolean hasResults() {
		if ((proteins == null) || (proteins.size() == 0))
				return false;
		return true;
	}
	
	/**
	 * Provides access to the RADSResults object. The RADSResults object
	 * contains, besides the hits, information on chosen database,
	 * query protein, etc.
	 * @return - the result of this scan
	 */
	public RADSResults getScanResults() {
		return this.results;
	}

	/**
	 * Canel the current scan
	 */
	public void cancelScan() {
		this.cancel(true);
		running = false;
	}
	
	
}
