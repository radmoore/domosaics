package angstd.localservices.hmmer2.ui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public abstract class HmmerServicePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
//	protected boolean progressable;
	protected JProgressBar progressBar;
	
	public void updateProgress(int val) {
		progressBar.setValue(val);
	}
	
	public void setProgressType(boolean indeterminate) {
		if (indeterminate)
			progressBar.setIndeterminate(true);
		else 
			progressBar.setIndeterminate(false);
	}
	
	public abstract void writeToConsole(String msg);

//	public HmmerServicePanel() {
//		progressBar = new JProgressBar();
//	}
	
//	public boolean isProgressable() {
//		return progressable;
//	}

//	public void setProgressable(boolean progressable) {
//		this.progressable = progressable;
//	}
	
	
	
	
	
	
}
