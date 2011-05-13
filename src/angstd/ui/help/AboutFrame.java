package angstd.ui.help;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import angstd.model.configuration.Configuration;



public class AboutFrame extends JFrame{

	JPanel panel;
	
	private static final String ABOUTPNG = "/angstd/ui/resources/about_angstd.png";
	
	public AboutFrame() {
		
		panel = new JPanel();
		
		//topPanel.setBackground(new Color(255,255,255));
		
		InputStream is = this.getClass().getResourceAsStream(ABOUTPNG);
		ImageIcon about = null;
		try {
			about = new ImageIcon(ImageIO.read(is));
			panel.add(new JLabel(about), BorderLayout.CENTER);
		} 
		catch (IOException e) {
			Configuration.getLogger().debug(e.toString());
		}
		
		
		
		this.add(panel);
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//this.setSize(600, 400);
		int x = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	    int y = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	    this.setLocation(x/2-450,y/2-450);
		this.setResizable(true);
		this.setVisible(true);
	}
	
}
