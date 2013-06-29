package domosaics.webservices.RADS.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import domosaics.ui.tools.ToolFrameI;
import domosaics.ui.views.view.View;




/**
 * This class describes a RADSFrame, which is the parent frame
 * to the RADSScanPanel when called from the main menu (ie. not as tool).
 * See {@link RADSScanPanel}, {@link ToolFrameI} for more information 
 *  
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSFrame extends JFrame implements WindowListener, ToolFrameI {

	private static final long serialVersionUID = 1L;
	private static RADSFrame instance = null;
	private RADSScanPanel scanPanel;

	/**
	 * This method provides access to an instance of a RADSFrame  
	 * 
	 * @return - an instance of RADSFrame
	 */
	public static RADSFrame getFrame() {
		if (instance == null)
			new RADSFrame();
		return instance;
	}
	
	/**
	 * Constructs a new instance of a RADSFrame. This should only be called
	 * if {@link RADSFrame#getFrame()} returns null, or if the frame is not visible
	 */
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
		scanPanel.close(true);
		instance = null;
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		instance = null;
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

	@Override
	public void addView(View view) {
		// TODO Auto-generated method stub
		
	}
}
