package angstd.localservices.hmmer3.ui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import angstd.localservices.hmmer3.programs.Hmmer3Program;

/**
 * Class to allow access to the panel that triggered
 * the respective {@link Hmmer3Program}, is meant
 * in particular for access to the progress bar and console 
 * of the calling panel.
 * 
 * TODO
 * this method is currently in limbo
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public abstract class HmmerServicePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	protected boolean progressable;
	//protected JProgressBar progressBar;
	
	public HmmerServicePanel() {
		//progressBar = new JProgressBar();
	}
	
	public void updateProgress(int val) {
		//progressBar.setValue(val);
	}
	
	public void setIndetermindateProgressBar(boolean value) {
		//progressBar.setIndeterminate(value);
	}
	
	public boolean isProgressable() {
		return progressable;
	}

	public void setProgressable(boolean progressable) {
		this.progressable = progressable;
	}
	
	public abstract void writeToConsole(String msg);
	
	public abstract void close();
	
	/**
	 * TODO:
	 * this doesnt seem right, but I simply could not
	 * get my hands on a the eg. hmmscan panels progress bar
	 * to be able to change it.
	 * 
	 * @return
	 */
	public abstract JProgressBar getProgressBar();
	
	
}
