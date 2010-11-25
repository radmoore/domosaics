package angstd.ui.tools.configuration;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import angstd.model.configuration.Configuration;

/**
 * Configuration frame allows modifications on the lookup addresses.
 * 
 * @author Andreas Held, Andrew Moore
 *
 */
public class ConfigurationFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	

	/**
	 * Constructor for a new ConfigurationFrame
	 */
    public ConfigurationFrame() {
		super("Angstd Configuration");	
		
		getContentPane().add(new ConfigurationPanel(this));
		
		Configuration.getInstance().setVisible(true);
		
		// set up the main window
		pack();
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setResizable(true);
		setVisible(true);
	}
    
    /**
     * When the main frame is closed, the panel has to be disposed and therefore 
     * the thread spawner cancelled.
     */
	public void windowClosing(WindowEvent e) {
		Configuration.getInstance().setVisible(false);
	}
	
	public void windowActivated(WindowEvent e) { }
	
	public void windowClosed(WindowEvent e) { }

	public void windowDeactivated(WindowEvent e) { }

	public void windowDeiconified(WindowEvent e) { }

	public void windowIconified(WindowEvent e) { }
	
	public void windowOpened(WindowEvent e) { }


}
