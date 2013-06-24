package domosaics.ui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import domosaics.model.configuration.Configuration;


/**
 * Class for handling the internal 
 * documentation files. Provides a method
 * for extracting the documentation (a zipped file)
 * out of the jar file. 
 * 
 * @author <a href="http://radm.info">Andrew Moore</a>
 *
 */
public class DocumentationHandler {

	/* location of zipped documentation within the jar */
	private static String _DOC_LOCATION =  "/domosaics/ui/resources/help/docs.zip";
	
	
	/**
	 * Extracts the internal (zipped) documentation to
	 * the default location (see {@link Configuration}).
	 *   
	 * @return true if file could be unzipped
	 */
	public static boolean extractDocumentation() {
		
		InputStream is = DocumentationHandler.class.getResourceAsStream(_DOC_LOCATION);
		File temp = null;
		
		try {
			temp = File.createTempFile("domosaics_docs", ".zip");
			temp.deleteOnExit();
			
			OutputStream os = new FileOutputStream(temp); 
			int read = 0;
			byte[] bytes = new byte[1024];
	 
			while ( (read = is.read(bytes)) != -1 ) {
				os.write(bytes, 0, read);
			}
	 		
			os.flush();
			os.close();
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return ZipUtil.unzipFile(temp.getAbsolutePath(), Configuration.DEF_DOCUMENTATION_PATH);
	}

}