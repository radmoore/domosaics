package domosaics.model.dotplot;

import java.util.EventObject;

/**
 * DotplotChangeEvent defines a event, which is fired, when the Dotplot changes,
 * e.g.because parameters were changed, and the dotplot was recalculated. 
 * Therefore graphical objects, can be informed and a redraw triggered.
 * 
 * @author Andreas Held
 *
 */
public class DotplotChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for a new DotplotChangeEvent
	 * 
	 * @param source
	 * 		the dotplot which fires the event
	 */
	public DotplotChangeEvent(Dotplot source) {
		super(source);
	}
	
	/**
	 * Returns the changed Dotplot object
	 * 
	 * @return
	 * 		Dotplot which changed
	 */
	public Dotplot getSource() {
		return (Dotplot) getSource();
	}

}
