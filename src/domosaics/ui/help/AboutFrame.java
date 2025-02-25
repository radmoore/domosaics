package domosaics.ui.help;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import domosaics.model.configuration.Configuration;

public class AboutFrame extends JFrame{

	JPanel panel;
	
	private static final String ABOUTPNG = "/domosaics/ui/resources/about_domosaics.png";
	public static AboutFrame instance;
	
	public AboutFrame() {
		
		panel = new JPanel();
		instance=this;
		//topPanel.setBackground(new Color(255,255,255));
		
		InputStream is = this.getClass().getResourceAsStream(ABOUTPNG);
		ImageIcon about = null;
		try {
			about = new ImageIcon(ImageIO.read(is));
			panel.add(new JLabel(about), BorderLayout.CENTER);
		} 
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		
		
		
		this.add(panel);
		
		this.pack();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//this.setSize(600, 400);
		int x = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	    int y = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	    this.setLocation(x/2-450,y/2-450);
		this.setResizable(true);
		this.setVisible(true);
		
        this.addWindowListener(new WindowAdapter(){
        	@Override
			public void windowClosing(WindowEvent e) {
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
	
}
