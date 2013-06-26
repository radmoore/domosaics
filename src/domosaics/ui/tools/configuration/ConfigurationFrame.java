package domosaics.ui.tools.configuration;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;




/**
 * Configuration frame allows modifications on the lookup addresses.
 * 
 * @author Andreas Held
 * @author Andrew Moore <radmoore@uni-muenster.de>
 *
 */
public class ConfigurationFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;

	private ConfigurationPanel configPanel;
	private JOptionPane waitingOptionPane;
	private JDialog waitingDialog;
	/**
	 * Constructor for a new ConfigurationFrame
	 */
    public ConfigurationFrame() {
		super("DoMosaics Settings");	
		
		configPanel = new ConfigurationPanel(this);
		getContentPane().add(configPanel);
		
		//Configuration.getInstance().setVisible(true);
		//Configuration.getInstance().setFrame(this);
		
		// set up the main window
		pack();
		//setSize(500, 650);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setResizable(true);
		/*if(Configuration.getInstance().getFrame()!=null)
			if(Configuration.getInstance().getFrame().getState() == Frame.ICONIFIED)
				setState(Frame.ICONIFIED);*/
		setVisible(true);
	}
    
    public void windowClosing(WindowEvent e) {
    	tryStop();
    	Configuration.getInstance().setFrame(null);
	}
	
	public void windowActivated(WindowEvent e) { }
	
	public void windowClosed(WindowEvent e) {
    	Configuration.getInstance().setFrame(null);
	}

	public ConfigurationPanel getConfigPanel() {
		return this.configPanel;
	}
	
	public void windowDeactivated(WindowEvent e) { }

	public void windowDeiconified(WindowEvent e) { }

	public void windowIconified(WindowEvent e) {  }
	
	public void windowOpened(WindowEvent e) { }

	public void tryStop() {
    	if(configPanel.isRunningPress()) {
    		waitingOptionPane = new JOptionPane("Please wait for hmmpress job to finish!\n ", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
    		waitingDialog = waitingOptionPane.createDialog(this,"");
    		waitingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    		waitingDialog.pack();
    		waitingDialog.setVisible(true);    		
    		while(configPanel.isRunningPress()) {}
    		waitingDialog.dispose();
    	}
	}
	
}
