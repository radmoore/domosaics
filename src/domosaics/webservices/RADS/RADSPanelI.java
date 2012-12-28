package domosaics.webservices.RADS;

import info.radm.radscan.RADSResults;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import domosaics.ui.views.view.View;




/**
 * Interface that describes a RADSPanel. Ensures access
 * to some graphical components of the panel as well
 * as to the scan results
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public interface RADSPanelI {

	/**
	 * Provides access to the scanning panels progress bar
	 * @return - progress bar of the scanning panel
	 */
	public JProgressBar getProgressBar();

	/**
	 * Provides access to the parent frame of the scanning panel
	 * @return - parent frame of the scanning panel
	 */
	public JFrame getParentFrame();

	/**
	 * Provides access to the scan results
	 * @return - scan results
	 */
	public RADSResults getResults();
	
	/**
	 * Provides access to the RADS service
	 * @return - the rads service performing the scan
	 */
	public RADSService getRadsService();
	
	/**
	 * Ensures the implementation of a close method that
	 * checks the current scan state
	 * @param checkScanState
	 */
	public void close(boolean checkScanState);
	
	/**
	 * Provides access to a view. This can either be a user-selected view (combobox within 
	 * a panel), or the currently active view 
	 * 
	 * @return - a view that matches the context of the scan
	 */
	public View getView();
	
}
