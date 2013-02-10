package domosaics.ui.tools.configuration;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import domosaics.model.configuration.Configuration;




/**
 * Configuration frame allows modifications on the lookup addresses.
 * 
 * @author Andreas Held
 * @author Andrew Moore <radmoore@uni-muenster.de>
 *
 */
public class ConfigurationFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	

	/**
	 * Constructor for a new ConfigurationFrame
	 */
    public ConfigurationFrame() {
		super("DoMosaicS Settings");	
		
		getContentPane().add(new ConfigurationPanel(this));
		
		//Configuration.getInstance().setVisible(true);
		//Configuration.getInstance().setFrame(this);
		
		// set up the main window
		pack();
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setResizable(false);
		if(Configuration.getInstance().getFrame()!=null)
			if(Configuration.getInstance().getFrame().getState() == Frame.ICONIFIED)
				setState(Frame.ICONIFIED);
		setVisible(true);
	}
    
    public void windowClosing(WindowEvent e) {
    	Configuration.getInstance().setFrame(null);
	}
	
	public void windowActivated(WindowEvent e) { }
	
	public void windowClosed(WindowEvent e) {
    	Configuration.getInstance().setFrame(null);
	}

	public void windowDeactivated(WindowEvent e) { }

	public void windowDeiconified(WindowEvent e) { }

	public void windowIconified(WindowEvent e) { }
	
	public void windowOpened(WindowEvent e) { }


}
