package domosaics.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * Utility for dealing with zipped files. Currently ony
 * supports unzipping of zipped files.
 * 
 * @author <a href="http://radm.info">Andrew Moore</a>
 *
 */
public class ZipUtil {

	/**
	 * Static method for unzipping files
	 * 
	 * @param zipFile - Zipped file
	 * @param outputFolder - folder where to unzip
	 * @return true if file unzipped, false otherwise
	 */
	public static boolean unzipFile (String zipFile, String outputFolder){
   	 
		byte[] buffer = new byte[1024];
    
        try{

	       	File folder = new File(outputFolder);

	       	if ( !folder.exists() )
	       		folder.mkdir();
	    
	       	// content of the zip file
	       	ZipInputStream zis = new ZipInputStream( new FileInputStream(zipFile) );
	       	
	       	// next entry in zip file
	       	ZipEntry ze = zis.getNextEntry();
	    
	       	while ( ze != null ) {
	    
	       		String fileName = ze.getName();
	            File newFile = new File(outputFolder + File.separator + fileName);
	    
	            // create all parent folders
	            new File( newFile.getParent() ).mkdirs();
	    
	            FileOutputStream fos = new FileOutputStream(newFile);             
	    
	            int len;
	            while ( (len = zis.read(buffer)) > 0 )
	            	fos.write(buffer, 0, len);
	    
	            fos.close();   
	            ze = zis.getNextEntry();
	       	}
	    
	        zis.closeEntry();
	       	zis.close();
    
       }
       catch ( IOException ex ) {
          ex.printStackTrace(); 
          return false;
       }
        return true;
    }
}
