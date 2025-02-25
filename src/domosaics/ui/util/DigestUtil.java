package domosaics.ui.util;

import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;

import domosaics.model.configuration.Configuration;

/**
 * 
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 *
 */
public class DigestUtil {

	private static String ALGO = "MD5";
	
	// creates a MD5 digest of a string
	public static String createDigest(String name) {
		MessageDigest digest = null;
		byte[] hash = null;
		try {
			digest = MessageDigest.getInstance(ALGO);
			digest.update(name.getBytes());
			hash = digest.digest();
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return hash.toString();
	}

	// adds a file with the name digest to a directory (eg project)
	public static boolean createAndAddDigest(String projectName, File dir, String fileKey) {
		MessageDigest digest = null;
		byte[] hash = null;
		try {
			digest = MessageDigest.getInstance(ALGO);
			digest.update(projectName.getBytes());
			hash = digest.digest();
			FileWriter fw = new FileWriter(dir+File.separator+fileKey);
			fw.write(hash.toString());
			fw.flush();
			fw.close();
			return true;
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return false;
	}

}


