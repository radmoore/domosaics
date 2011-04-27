package angstd;

import java.util.logging.Level;

import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;

import angstd.model.configuration.Configuration;
import angstd.ui.util.MessageUtil;



/**
 * Welcome to the main class of Angstd. All initialization is delegated 
 * to the {@link ApplicationHandler}. So no fun here.
 * 
 * @author Andrew D. Moore, Andreas Held, Nicolas Terrapon
 *
 */
public class Angstd {
	
	/**
	 * The main method of Angstd and the anchor point to start the program. 
	 * 
	 * @param args
	 * 		arguments you wish to run with Angstd (nothing supported)
	 */
	public static void main(String[] args) {
		
        Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler(){
        	
            public void uncaughtException(Thread t, Throwable e) {
            	
//            	Logger log = Logger.getLogger("angstdlog");
////            	RollingFileAppender rfl = (RollingFileAppender)log.getAppender("angstdlog");
////				rfl.setFile(Configuration.DEF_LOG_LOCATION);
////				log.addAppender(rfl);
//		    	log.info("Starting AnGSTD");
//            	log.info("Exception in main: ");
//            	log.info(e.toString());
            	Configuration.getLogger().debug("Uncaught exception");
            	Configuration.getLogger().debug(e.toString());
//            	MessageUtil.showWarning("There was a problem starting AnGSTD. Please consult log file.");
//              System.exit(1);
            }
        });
        
		try {
			Configuration.getLogger().info("=============================================");
			Configuration.getLogger().info("Starting AnGSTD.");
			ApplicationHandler.getInstance().start();
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("There was a problem starting AnGSTD. Please consult log file.");
		}
        
	}
	
}
