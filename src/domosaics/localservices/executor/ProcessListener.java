package domosaics.localservices.executor;

/**
 * Interface defining the methods for a process listener.
 * A class implementing this interface can be assigned as listener
 * to the streams created by a process object.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * @author Andreas Held
 *
 */
public interface ProcessListener {

	public void outputRecieved(String out, String type);
	
	public void setResult(int res);
	
}
