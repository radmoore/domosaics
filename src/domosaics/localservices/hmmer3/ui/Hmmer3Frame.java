package domosaics.localservices.hmmer3.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Generic frame for Hmmer3Programs. This may be unecessary
 * as it is an artifact of the (old) hmmer 2 GUI, which changed
 * panels depending on the program choice.
 *  
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class Hmmer3Frame extends JFrame{

	private static final long serialVersionUID = 1L;
	private static Hmmer3Frame instance = null;
		
	// panel which holds the components 
	protected HmmScanPanel hmmScanPanel; 
	protected JPanel content;
		
	/**
	 * Constructor for a new Hmmer3Frame
	**/	
	public Hmmer3Frame() {
		super("Local HMMER job");
		instance = this;
		content = new JPanel(new BorderLayout());
		hmmScanPanel = new HmmScanPanel(this); 
		content.add(hmmScanPanel, BorderLayout.CENTER);
		getContentPane().add(hmmScanPanel);
		setSize(500, 500);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setVisible(true);
        setAlwaysOnTop(true);
        this.addWindowListener(new WindowAdapter(){
        	@Override
			public void windowClosing(WindowEvent e) {
        		hmmScanPanel.cancelAction();
        		instance=null;
        	}
        	
        	@Override
			public void windowActivated(WindowEvent e) { }
        	
        	@Override
			public void windowClosed(WindowEvent e) { 
        		instance=null;
        	}

        	@Override
			public void windowDeactivated(WindowEvent e) { }

        	@Override
			public void windowDeiconified(WindowEvent e) { }

        	@Override
			public void windowIconified(WindowEvent e) { }
        	
        	@Override
			public void windowOpened(WindowEvent e) { }
		});
	}
	
	// weak singleton
	public static Hmmer3Frame getFrame() {
		if (instance == null) 
			instance = new Hmmer3Frame();
		instance.setState(Frame.NORMAL);
		instance.setVisible(true);
		return instance;
	}
	
	public static boolean isIntanciated() {
		return instance != null;
	}
	

	public static boolean isOpen() {
		if ( instance == null )
			return false;
		return instance.isShowing();
	}
	
	
}
