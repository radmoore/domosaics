package domosaics.ui.tools.configuration;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import domosaics.model.configuration.Configuration;




/**
 * Configuration frame allows modifications on the lookup addresses.
 * 
 * @author Andreas Held
 * @author Andrew Moore <radmoore@uni-muenster.de>
 *
 */
public class ConfigurationFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private ConfigurationPanel configPanel;
	/*private JOptionPane waitingOptionPane;
	private JDialog waitingDialog;*/
	/**
	 * Constructor for a new ConfigurationFrame
	 */
    public ConfigurationFrame() {
		super("DoMosaics Settings");	
		
		configPanel = new ConfigurationPanel(this);
		getContentPane().add(configPanel);
				
		// set up the main window
		pack();
		//setSize(500, 650);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setResizable(true);
		setVisible(true);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				Configuration.getInstance().closeFrame();
				//tryStop();
			}
			
			@Override
			public void windowActivated(WindowEvent e) { }
			
			@Override
			public void windowClosed(WindowEvent e) { 
				Configuration.getInstance().closeFrame();
		    	//tryStop();
		    }
			
			@Override
			public void windowDeactivated(WindowEvent e) {    }

			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) { 
		    	//tryStop();
		    }
			
			@Override
			public void windowOpened(WindowEvent e) { }
		});
	}
    
    public ConfigurationPanel getConfigPanel() {
    	return configPanel;
    }

	/*public void tryStop() {
		if(configPanel.isRunningPress()) {
    		waitingOptionPane = new JOptionPane("Please wait for hmmpress job to finish!\n ", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
    		waitingDialog = waitingOptionPane.createDialog(this,"");
    		waitingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    		waitingDialog.pack();
    		waitingDialog.setVisible(true);
    		while(configPanel.isRunningPress()) {}
    		waitingDialog.dispose();
    	}
	}*/
	
}
