package domosaics.webservices.interproscan.ui;

import java.awt.Frame;
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
	private boolean onTop=true;
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
		instance.setState(Frame.NORMAL);
		instance.setVisible(true);
    	return instance;
    }
    
    
    public static boolean isOpen() {
    	if (instance == null)
    		return false;
    	return (instance.isShowing()); 
    }
    
    /**
     * When the main frame is closed, the panel has to be disposed and therefore 
     * the thread spawner cancelled.
     */
	@Override
	public void windowClosing(WindowEvent e) {
		annotatorPanel.cancel();
		instance=null;
	}
		
	@Override
	public void windowActivated(WindowEvent e) {}
	
	@Override
	public void windowClosed(WindowEvent e) { 
		instance=null;
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) { 
		setState(Frame.NORMAL);
	}

	@Override
	public void windowIconified(WindowEvent e) { }
	
	@Override
	public void windowOpened(WindowEvent e) { }
    
}
