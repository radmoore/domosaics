package domosaics.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import domosaics.model.configuration.Configuration;




public abstract class CheckConnectivity {
	
	private final static String DEFAULT_URL = "http://www.google.com";  
	
	/**
	 * General test of inet connectivity
	 * @return true, if google serves content
	 */
	public static boolean checkInternetConnectivity() {
		return addressAvailable(DEFAULT_URL);
	}
	

	/**
	 * Test a specific address
	 * @return true if address serves contect
	 */
	public static boolean addressAvailable(String address) {
		try {
			URL url = new URL(address);
			HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
			Object objData = urlConnect.getContent();
		}
		catch (MalformedURLException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return false;
		}
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return false;
		}
		return true;
	}
	
	//TODO
	public static boolean soapAvailable() {
		return true;
	}
	

}
