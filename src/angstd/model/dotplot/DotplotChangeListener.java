package angstd.model.dotplot;

/**
 * DotplotChangeListener are Classes which may be interested in dotplot changes,
 * e.g.because parameters were changed, and the dotplot was recalculated. 
 * Therefore graphical objects, can be informed and a redraw triggered, say just
 * have to be added to the dotplot as DotplotChangeListener.
 * 
 * @author Andreas Held
 *
 */
public interface DotplotChangeListener  {
	
	/**
	 * Fired when the dotplot was recalculated.
	 * 
	 * @param evt
	 * 		Event which contains the changed dotplot.
	 */
	public void dotplotChanged(DotplotChangeEvent evt);
		
}
