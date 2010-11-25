package angstd.localservices.hmmer2.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Main frame for the Hmmer2 interface. The frame contains a panel
 * for program selection as well as a program specific parameter panel. 
 * 
 * @author Andrew Moore, Andreas Held
 *

public class Hmmer2Frame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	/** panel which holds the components 
	protected JPanel content;
	
	/** parameter panel which can be changed during the application workflow 
	protected JPanel southPanel;
	
	
	/**
	 * Constructor for a new Hmmer2Frame
	
	public Hmmer2Frame() {
		content = new JPanel(new BorderLayout());
		content.add(new Hmmer2ChooserPanel(this), BorderLayout.CENTER);
		
		getContentPane().add(content);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Changes the parameter panel.
	 * 
	 * @param progPanel
	 * 		the new parameter panel to be added
	 
	public void addProgFrame(JPanel progPanel) {
		if (southPanel != null)
			content.remove(southPanel);
		
		southPanel = progPanel;
		content.add(progPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
	}
}
	 */