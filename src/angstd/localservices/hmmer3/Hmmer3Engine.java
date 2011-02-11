package angstd.localservices.hmmer3;

import java.io.File;
import java.util.HashMap;

import angstd.localservices.hmmer3.programs.Hmmer3Program;

/**
 * Singleton class to ensure that only one instance of a {@link Hmmer3Program}
 * can run at once. Can launch and stop programs that implement the interface
 * {@link Hmmer3Program}. This class also maintains services that are currently
 * supported. These can be called statically.
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
	
	// maps a valid service to its repective executable 
	protected HashMap<String, File> validServices;
	
	/**
	 * Contructor for a new Hmmer3Engine. The constructor is protected to 
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
	 * Checks whether this service is supported
	 * 
	 * @param serviceName
	 * @return
	 */
	public static boolean isValidService(String serviceName) {
		return supportedServices.containsKey(serviceName);
	}
	
	/**
	 * Returns a one-line description of a valid service
	 * @param serviceName
	 * @return
	 */
	public static String getValidServiceDescription(String serviceName) {
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
	public File getValidServicePath(String serviceName) {
		return (validServices.containsKey(serviceName)) ? 
				validServices.get(serviceName) : 
					null;
	}
	
	/**
	 * Sets a valid service
	 * @param validServices
	 */
	public void setValidServices(HashMap<String, File> validServices) {
		this.validServices = validServices;
	}
	
	
	/**
	 * Launches a program of type {@link Hmmer3Program} via
	 * the programs start() method
	 * @param program
	 */
	public void launch (Hmmer3Program program) {
		service = new Hmmer3Service(program);
		service.start();
	}
	
	/**
	 * Stop the running program
	 */
	public void stop() {
		if (service != null)
			service.stop();
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
