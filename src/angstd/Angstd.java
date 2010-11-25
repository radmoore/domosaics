package angstd;



/**
 * Welcome to the main class of Angstd. All initialization is delegated 
 * to the {@link ApplicationHandler}. So no fun here.
 * 
 * @author Andreas Held
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
		try {
			ApplicationHandler.getInstance().start();
		} catch(Exception e) {
			 System.out.println("An unknown error occured while loading AngstdUI");
 		     e.printStackTrace();
 		}
	}
	
}
