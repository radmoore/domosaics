package domosaics.ui.io.menureader;

/**
 * Exception which can be thrown during action creation. <br>
 * This can be caught and directed to a logger to inform the user
 *
 * @author Andreas Held
 *
 */
public class CreateActionException extends Exception {
	private static final long serialVersionUID = 1L;
	
    public CreateActionException() {
    	super();
    }
    
    public CreateActionException(String str) {
        super(str);
    }
    
    @Override
	public String toString() {
        return "failed to create MenuItem: "+super.getMessage();
    }

}
