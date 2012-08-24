package angstd.webservices.RADS;

import info.radm.radscan.RADSResults;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public interface RADSPanelI {

	public JProgressBar getProgressBar();

	public JFrame getParentFrame();

	public RADSResults getResults();
	
	public RADSService getRadsService();
	
	public void close(boolean checkScanState);
	
}
