package domosaics.webservices.interproscan.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * The Annotator gui frame in which the annotator panel is embedded.
 * 
 * @author Andreas Held
 *
 */
public class AnnotatorFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The annotator panel which spawns threads is hold here, 
	 * because if the frame disposes, the panel has to be notified as well, so
	 * that running threads can be stopped
	 */
	private AnnotatorPanel annotatorPanel;
	private static AnnotatorFrame instance = null;
	
    public AnnotatorFrame() {
		super("DoMosaics InterProScan");	
		
		addWindowListener(this);
		annotatorPanel = new AnnotatorPanel(this);
		instance = this;
		
		getContentPane().add(annotatorPanel);
		
		// set up the main window
		this.pack();
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		this.setResizable(false);
		this.setVisible(true);
	}

    public static AnnotatorFrame getFrame() {
    	if (instance == null)
    		instance = new AnnotatorFrame();
    	return instance;
    }
    
    /**
     * When the main frame is closed, the panel has to be disposed and therefore 
     * the thread spawner cancelled.
     */
	public void windowClosing(WindowEvent e) {
		annotatorPanel.cancel();
	}
	
	public void windowActivated(WindowEvent e) { }
	
	public void windowClosed(WindowEvent e) { }

	public void windowDeactivated(WindowEvent e) { }

	public void windowDeiconified(WindowEvent e) { }

	public void windowIconified(WindowEvent e) { }
	
	public void windowOpened(WindowEvent e) { }
    

}
