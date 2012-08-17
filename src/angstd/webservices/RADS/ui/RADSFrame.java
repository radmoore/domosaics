package angstd.webservices.RADS.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSFrame extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static RADSFrame instance = null;
	private RADSScanPanel scanPanel;
	
	public static RADSFrame getFrame() {
		if (instance == null)
			new RADSFrame();
		return instance;
	}
	
	
	public RADSFrame() {
		super("RADS/RAMPAGE");	
		
		addWindowListener(this);
		scanPanel = new RADSScanPanel(this);
		instance = this;
		
		getContentPane().add(scanPanel);
		
		this.pack();
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
