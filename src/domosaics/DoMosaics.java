package domosaics;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import domosaics.model.configuration.Configuration;
import domosaics.model.configuration.ConfigurationWriter;
import domosaics.ui.util.MessageUtil;


/**
 * Welcome to the main class of DoMosaicS. All initialization is delegated 
 * to the {@link ApplicationHandler}. So no fun here.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * @author Andreas Held <a.held@uni-muenster.de>
 * @author Nicolas Terrapon <n.terrapon@uni-muenster.de>
 *
 */
public class DoMosaics {
	
	/** 
	 * The main method of DoMosaicS and the anchor point to start the program. 
	 * 
	 * @param args
	 * 		arguments you wish to run with DoMosaicS
	 */
	public static void main(String[] args) {
		
		Configuration.setDebug(true);
		if (args.length > 0) {
			for(String a : args) {
				if (a.equals("--debug")) {
					Configuration.setDebug(true);
				}
				else {
					System.out.println("DoMosaicS: dunno what >"+args[0]+"< means. Exiting.");
					System.exit(1);
				}
			}
		}

		
		// if we are not in debug mode, catch all 
		// unhandled exceptions
		if (!Configuration.isDebug()) {
			Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler(){

				public void uncaughtException(Thread t, Throwable e) {
					Configuration.getLogger().debug("Uncaught exception");
					StringWriter w = new StringWriter();
					e.printStackTrace(new PrintWriter(w));
					Configuration.getLogger().debug(w.toString());
					MessageUtil.showWarning("There was a unexpected problem running DoMosaicS; consult log file.");
					// remove lock file if possible
					if (Configuration.getInstance().hasLockfile()) {
						// TODO Save the the views: i) all and the user will remove the fucking one ii) only the working ones
						// Save the configuration
						ConfigurationWriter.write(Configuration.getInstance().getConfigFile());						
						Configuration.getInstance().getLockFile().delete();
					}
					System.exit(1);
				}
			});
		}
		try {
			Configuration.getLogger().info("*** INFO: Starting DoMosaicS.");
			ApplicationHandler.getInstance().start();
		}
		catch (Exception e) {
			e.printStackTrace();
			Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("There was a problem starting DoMosaicS. Please consult log file.");
		}

	}
	
}
