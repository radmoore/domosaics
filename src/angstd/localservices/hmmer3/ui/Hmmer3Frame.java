package angstd.localservices.hmmer3.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
		
	// panel which holds the components 
	protected JPanel content;
		
	/**
	 * Constructor for a new Hmmer3Frame
	**/	
	public Hmmer3Frame() {
		super("Local HMMER job");
		content = new JPanel(new BorderLayout());
		content.add(new HmmScanPanel(this), BorderLayout.CENTER);
		getContentPane().add(content);
		setSize(500, 500);
		pack();
		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setVisible(true);
        setAlwaysOnTop(true);
	}
		
}
