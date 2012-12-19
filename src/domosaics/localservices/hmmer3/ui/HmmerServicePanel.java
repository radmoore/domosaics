package domosaics.localservices.hmmer3.ui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import domosaics.localservices.hmmer3.programs.Hmmer3Program;


/**
 * Class to allow access to the panel that triggered
 * the respective {@link Hmmer3Program}, is meant
 * in particular for access to the progress bar and console 
 * of the calling panel.
 * 
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public abstract class HmmerServicePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	protected boolean progressable;
	protected JProgressBar progressBar;
	
	public HmmerServicePanel() {
		progressBar = new JProgressBar();
	}
	
	public void updateProgress(int val) {
		progressBar.setValue(val);
	}
	
	
	public void setIndetermindateProgressBar(boolean value) {
		progressBar.setIndeterminate(value);
	}
	
	public boolean isProgressable() {
		return progressable;
	}

	public void setProgressable(int min, int max) {
		this.progressable = true;
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}
	
	/**
	 * Write to the console of the calling panel
	 * @param msg
	 */
	public abstract void writeToConsole(String msg);
	
	
	/**
	 * Enables closing the calling panel
	 */
	public abstract void close();
	
	/**
	 * Resets all components of the calling panel
	 * (eg. in case of an error while running the program)
	 */
	public abstract void resetPanel();
	
	
}
