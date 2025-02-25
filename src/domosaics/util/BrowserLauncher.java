package domosaics.util;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;




/**
 * Opens a browser window to a specified url.
 *
 * @author unknown
 *
 */
public class BrowserLauncher {

	private static final String errMsg = "Error launching web browser";

	public static void openURL(String url) {
		if (!CheckConnectivity.checkInternetConnectivity())
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please check your internet connection (connection failed).");
		else  {
			
			try {
				String osName = System.getProperty("os.name");
				
				if (osName.startsWith("Mac OS")) {
					Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
					Method openURL = fileMgr.getDeclaredMethod("openURL",  new Class[] {String.class});
					openURL.invoke(null, new Object[] {url});
				}
				else if (osName.startsWith("Windows"))
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			
				else { //assume Unix or Linux
					String[] browsers = {"chrome", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
					String browser = null;
					for (int count = 0; count < browsers.length && browser == null; count++)
						if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]}).waitFor() == 0)
							browser = browsers[count];
					if (browser == null)
						throw new Exception("Could not find web browser");
					else
						Runtime.getRuntime().exec(new String[] {browser, url});
				}
			}
			// we did not find the file
			catch (IOException ioe) {
				JOptionPane.showMessageDialog(null, errMsg + ":\n" + ioe.getLocalizedMessage());
			}
			// any other exception
			catch (Exception e) {
				e.printStackTrace();
				if (Configuration.getReportExceptionsMode(true))
					Configuration.getInstance().getExceptionComunicator().reportBug(e);
				else {
					Configuration.getLogger().debug(e.toString());
				}
			}
		}
	}	
}