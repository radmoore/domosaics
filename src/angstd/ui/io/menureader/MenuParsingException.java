package angstd.ui.io.menureader;

/**
 * Exception which can be thrown during menu file parsing. <br>
 * 
 * This can be caught and directed to a logger to inform the user
 *
 * @author Andreas Held
 *
 */
public class MenuParsingException extends Exception {
	private static final long serialVersionUID = 1L;
	
    public MenuParsingException() {
    	super();
    }
    
    public MenuParsingException(String str) {
        super(str);
    }
    
    public String toString() {
        return "Failed to parse menu file: "+super.getMessage();
    }

}
