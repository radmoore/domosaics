package domosaics.webservices.interproscan;

/**
 * Interface AnnotatorProcessWriter defines methods to print messages 
 * about the annotation progress.
 * 
 * @author Andreas Held
 *
 */
public interface AnnotatorProcessWriter {

	/**
	 * Prints textual messages about the annotation progress.
	 * 
	 * @param msg
	 * 		the message to be printed
	 */
	public void print(String msg);
	
	/**
	 * Can be used to update a progressbar
	 * 
	 * @param val
	 * 		value in percent about the total progress
	 */
	public void updateProgress(int val);
	
	
}
