package domosaics.model;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import domosaics.model.configuration.Configuration;




/**
 * Enumeration of loadable data types storing the types name and icon. 
 * Can be used for instance within the import data wizard.
 * 
 * @author Andreas Held
 *
 */
public enum DataType {
	
	TREE ("Tree", 			"domosaics/ui/views/treeview/resources/img/treebig.png"),
	DOMAINS("Arrangement",  "domosaics/ui/views/domainview/resources/img/domsbig.png"),
	SEQUENCE("Sequence", 	"domosaics/ui/views/sequenceview/resources/img/seqbig.png")
	;
	
	private String title;
	private String icon;
	
	private DataType(String title, String icon) {
		this.title = title;
		this.icon = icon;
	}
	
	/**
	 * Returns the data types name 
	 * 
	 * @return
	 * 		data type name
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Returns the associated icon for the data type.
	 * 
	 * @return
	 * 		icon for the data type
	 */
	public ImageIcon getIcon() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(icon);
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(ImageIO.read(is));
		} 
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return icon;
	}
	
	public String toString() {
		return title;
	}
}
