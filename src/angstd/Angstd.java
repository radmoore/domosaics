package angstd;

import java.io.PrintWriter;
import java.io.StringWriter;

import angstd.model.configuration.Configuration;
import angstd.ui.util.MessageUtil;



/**
 * Welcome to the main class of AnGSTD. All initialization is delegated 
 * to the {@link ApplicationHandler}. So no fun here.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * @author Andreas Held <a.held@uni-muenster.de>
 * @author Nicolas Terrapon <n.terrapon@uni-muenster.de>
 *
 */
public class Angstd {
	
	/** 
	 * The main method of Angstd and the anchor point to start the program. 
	 * 
	 * @param args
	 * 		arguments you wish to run with Angstd
	 */
	public static void main(String[] args) {
		
		Configuration.setDebug(true);
		if (args.length > 0) {
			for(String a : args) {
				if (a.equals("--debug")) {
					Configuration.setDebug(true);
				}
				else {
					System.out.println("AnGSTD: dunno what >"+args[0]+"< means. Exiting.");
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
	            	MessageUtil.showWarning("There was a unexpected problem running AnGSTD; consult log file.");
	            	// remove lock file if possible
	            	if (Configuration.getInstance().hasLockfile())
	            		Configuration.getInstance().getLockFile().delete();
	            	System.exit(1);
	            }
	        });
		}
		try {
			Configuration.getLogger().info("*** INFO: Starting AnGSTD.");
			ApplicationHandler.getInstance().start();
		}
		catch (Exception e) {
			e.printStackTrace();
			Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("There was a problem starting AnGSTD. Please consult log file.");
		}
        
	}
	
}
