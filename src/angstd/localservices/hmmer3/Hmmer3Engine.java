package angstd.localservices.hmmer3;

import java.io.File;
import java.util.HashMap;

import sun.security.krb5.Config;

import angstd.localservices.hmmer3.programs.Hmmer3Program;
import angstd.model.configuration.Configuration;

/**
 * Singleton class to ensure that only one instance of a {@link Hmmer3Program}
 * can run at once. Can launch and stop programs that implement the interface
 * {@link Hmmer3Program}. This class also maintains services that are currently
 * supported. These can be called statically.
 *  
 *  TODO
 *	- launchInBackground() and launch() currently do not differ
 *  
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class Hmmer3Engine {

	// instance of this class necessary to support the singleton pattern 
	protected static Hmmer3Engine instance;
	
	// supported programs
	protected static final HashMap<String, String> supportedServices = new HashMap<String, String>();
	
	// supported programs and a short description
	static {
		supportedServices.put("hmmscan", "search sequence(s) against a profile database");
		supportedServices.put("hmmpress", "prepare an HMM database for faster hmmscan searches");
	}
	
	// the running hmmer3 service 
	protected Hmmer3Service service;
	
	// maps a valid service to its respective executable 
	protected HashMap<String, File> availableServices;
	
	/**
	 * Constructor for a new Hmmer3Engine. The constructor is protected to 
	 * support the singleton pattern. 
	 * Use getInstance().
	 */
	protected Hmmer3Engine() {
		reset();
	}
	
	/**
	 * resets the instance
	 */
	public void reset() {
		instance = null;
		Configuration.getInstance().setServiceRunning(false);
	}
	
	/**
	 * Method which delivers an instance of this class. 
	 * 
	 * @return
	 * 		instance to the Hmmer3Engine
	 */
	 
	public static Hmmer3Engine getInstance() {
		if (instance == null)
			instance = new Hmmer3Engine();
		return instance;
	}
	
	/**
	 * Checks whether this service is generally supported
	 * 
	 * @param serviceName
	 * @return
	 */
	public static boolean isSupportedService(String serviceName) {
		return supportedServices.containsKey(serviceName);
	}

	/**
	 * Checks whether this service is actually available
	 * (that is, a bin file has been set)
	 * @param serviceName
	 * @return
	 */
	public boolean isAvailableService(String serviceName) {
		if (availableServices == null)
			return false;
		return availableServices.containsKey(serviceName);
	}
	
	/**
	 * Returns a one-line description of a valid service
	 * @param serviceName
	 * @return
	 */
	public static String getSupportedServiceDescription(String serviceName) {
		return (supportedServices.containsKey(serviceName)) ? 
				supportedServices.get(serviceName) : 
					"Unknown service.";
	}
	
	/**
	 * Returns a file corresponding to the executable for the current
	 * service
	 * @param serviceName
	 * @return
	 */
	public File getAvailableServicePath(String serviceName) {
		return (availableServices.containsKey(serviceName)) ? 
				availableServices.get(serviceName) : 
					null;
	}
	
	/**
	 * Sets a valid service
	 * @param availableServices
	 */
	public void setAvailableServices(HashMap<String, File> validServices) {
		this.availableServices = validServices;
	}
	
	
	/**
	 * Launches a program of type {@link Hmmer3Program} via
	 * {@link Hmmer3Service}.startInBackground()
	 * @param program
	 */
	public void launchInBackground(Hmmer3Program program) {
		service = new Hmmer3Service(program);
		service.startInBackground();
		Configuration.getInstance().setServiceRunning(true);
	}
	
	/**
	 * Launches a program of type {@link Hmmer3Program} via
	 * {@link Hmmer3Service}.start(). This method
	 * will wait for the return value
	 * @param program
	 */
	public int launch(Hmmer3Program program) {
		service = new Hmmer3Service(program);
		Configuration.getInstance().setServiceRunning(true);
		return service.start();
	}
	
	/**
	 * Stop the running program
	 */
	public void stop() {
		if (service != null)
			service.stop();
		Configuration.getInstance().setServiceRunning(false);
	}
	
	/**
	 * Checks whether there is currently a program
	 * running
	 * @return
	 */
	public boolean isRunning() {
		if (service != null) {
			return service.isRunning();
		}
		return false;
	}
	
}
