package domosaics.ui.views.view.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.view.ViewInfo;

/**
 * Class for reading a views property file. This parser is called by
 * DoMosaicsViewFactory during the creation process. 
 * 
 * @author Andreas Held
 *
 */
public class ViewPropertyReader {

	/**
	 * Creates a ViewInfo object filled with all necessary view informations
	 * which are stored in the property file.
	 * 
	 * @param viewURL
	 * 		view location within the package structure
	 * @return
	 * 		view information object stored with all information from the property file
	 * 		
	 */
	public ViewInfo getView(URL viewURL) {
		try {
			InputStream stream = viewURL.openStream();
	        BufferedReader in = new BufferedReader(new InputStreamReader(stream));	
	        
			String line;
			
			ViewInfo viewInfo = new ViewInfo();
				
			while((line = in.readLine()) != null) {	
				// skip comments
				if (line.contains("<!--"))
					continue;
				
				
				// fill the viewinfo with properties
				if (line.toUpperCase().contains("<PROPERTY ")) {
					
					// name property
					if (getID(line).toUpperCase().equals("NAME")) {
						viewInfo.setName(getValue(line));
						continue;
					}
					
					if (getID(line).toUpperCase().equals("WORKSPACEFOLDERNAME")) {
						viewInfo.setWorkspaceFolderName(getValue(line));
						continue;
					}
					
					// icon property
					if (getID(line).toUpperCase().equals("ICON")) {
						InputStream is = this.getClass().getClassLoader().getResourceAsStream(getValue(line));
						try {
							//System.out.println("test "+line+".");
							ImageIcon icon = new ImageIcon(ImageIO.read(is));
							viewInfo.setDefaultIcon(icon);
							viewInfo.setUsedIcon(icon);
							is.close();
						} 
						catch(Exception e) {
							System.out.println(getValue(line));
							if (Configuration.getReportExceptionsMode(true))
								Configuration.getInstance().getExceptionComunicator().reportBug(e);
							else			
								Configuration.getLogger().debug(e.toString());
						}
						
						continue;
					}
					
					// associated icon property (e.g. if domainview is associated with a sequenceview)
					if (getID(line).toUpperCase().equals("ASSOCIATEDICON")) {
						InputStream is = this.getClass().getClassLoader().getResourceAsStream(getValue(line));
						try {
							ImageIcon icon = new ImageIcon(ImageIO.read(is));
							viewInfo.setAssociatedIcon(icon);
							is.close();
						} 
						catch(Exception e) {
							if (Configuration.getReportExceptionsMode(true))
								Configuration.getInstance().getExceptionComunicator().reportBug(e);
							else			
								Configuration.getLogger().debug(e.toString());
						}
						continue;
					}
					
					// workspace folder property
					if (getID(line).toUpperCase().equals("WORKSPACEFOLDERICON")) {
						InputStream is = this.getClass().getClassLoader().getResourceAsStream(getValue(line));
						try {
							ImageIcon icon = new ImageIcon(ImageIO.read(is));
							viewInfo.setWorkspaceFolderIcon(icon);
							is.close();
						} 
						catch(Exception e) {
							if (Configuration.getReportExceptionsMode(true))
								Configuration.getInstance().getExceptionComunicator().reportBug(e);
							else			
								Configuration.getLogger().debug(e.toString());
						}
						continue;
					}
					
					// data import icon property
					if (getID(line).toUpperCase().equals("DATAICON")) {
						InputStream is = this.getClass().getClassLoader().getResourceAsStream(getValue(line));
						try {
							ImageIcon icon = new ImageIcon(ImageIO.read(is));
							viewInfo.setDataImportIcon(icon);
							is.close();
						} 
						catch(Exception e) {
							if (Configuration.getReportExceptionsMode(true))
								Configuration.getInstance().getExceptionComunicator().reportBug(e);
							else			
								Configuration.getLogger().debug(e.toString());	
						}
						continue;
					}
					
					
//					if (getID(line).toUpperCase().equals("CHANGEDICON")) {
//						InputStream is = this.getClass().getClassLoader().getResourceAsStream(getValue(line));
//						try {
//							ImageIcon icon = new ImageIcon(ImageIO.read(is));
//							viewInfo.setChangedIcon(icon);
//							is.close();
//						} catch(Exception e) {}
//						continue;
//					}
//					
//					
//					if (getID(line).toUpperCase().equals("ASSOCCHANGEDICON")) {
//						InputStream is = this.getClass().getClassLoader().getResourceAsStream(getValue(line));
//						try {
//							ImageIcon icon = new ImageIcon(ImageIO.read(is));
//							viewInfo.setAssocChangedIcon(icon);
//							is.close();
//						} catch(Exception e) {}
//						continue;
//					}
					
					// isTool property
					if (getID(line).toUpperCase().equals("ISTOOL")) {
						if (getValue(line).toUpperCase().equals("FALSE"))
							viewInfo.setIsTool(false);
						else
							viewInfo.setIsTool(true);
						continue;
					}
					
					// frameclass property
					if (getID(line).toUpperCase().equals("FRAMECLASS")) {
						try {
							Class<?> frameClazz = Class.forName(getValue(line));
							viewInfo.setFrameClazz(frameClazz);
						} 
						catch (ClassNotFoundException e) {
							if (Configuration.getReportExceptionsMode(true))
								Configuration.getInstance().getExceptionComunicator().reportBug(e);
							else {
								Configuration.getLogger().debug(e.toString());
								Configuration.getLogger().debug("ClassURL for tool frame "+getValue(line)+" not found");
							}
						}
						continue;
					}
					
				}
			}	// end of property parsing
				
			in.close();	
			return viewInfo;	
		} 
		catch (FileNotFoundException fnfe) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(fnfe);
			else			
				Configuration.getLogger().debug(fnfe.toString());
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Property file not found");
		}
		catch (IOException ioe) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(ioe);
			else			
				Configuration.getLogger().debug(ioe.toString());
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Property file couldnt be parsed");
		}
		return null;
	}

	/**
	 * Extracts an ID of an item.
	 * 
	 * @param str 
	 * 		the line where the id has to be extracted
	 * @return 
	 * 		the extracted id of a item
	 */
	private static String getID(String str) {
		int startPos = str.indexOf("id=\"")+4;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
	
	/**
	 * Extracts an value of an parameter.
	 * 
	 * @param str 
	 * 		the line where the value has to be extracted
	 * @return 
	 * 		the extracted value of a parameter
	 */
	private static String getValue(String str) {
		int startPos = str.indexOf("value=\"")+7;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
}
