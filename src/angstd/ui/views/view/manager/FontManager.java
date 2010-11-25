package angstd.ui.views.view.manager;

import java.awt.Font;

import angstd.ui.views.view.components.ViewComponent;

/**
 * The FontManager is a ViewManager which handles the font attribute for
 * ViewComponents.
 * <p>
 * This interface describes methods for setting the default, maximal allowed,
 * minimal allowed and actually font size as well as the font. The maximum
 * and minimum font size for instance are important for the layout process.
 * <p>
 * The basic implementation of this interface is {@link DefaultFontManager}
 * To extend or overwrite some of the basic functionalities the 
 * DefaultFontManager should be extended.
 * <p>
 * The extending of the ViewManager interface ensures the communication
 * with the observed view. 
 * 
 * @author Andreas Held
 *
 * @param <C>
 * 		the generic type of the ViewComponent
 */
public interface FontManager<C extends ViewComponent> extends ViewManager {

	/**
	 * Sets a new font for the component.
	 * 
	 * @param comp
	 * 		the view component which font is changed
	 * @param font
	 * 		the new font for the view component
	 */
	public void setFont(C comp, Font font);
	
	/**
	 * Returns the font for the specified view component
	 * 
	 * @param comp
	 * 		the view component which font is requested
	 * @return
	 * 		the font for the specified view component
	 */
	public Font getFont(C comp);
  	
	/**
	 * Return the default font
	 * 
	 * @return
	 * 		default font
	 */
	public Font getFont();
	
	/**
	 * Return maximum font size
	 * 
	 * @return
	 * 		maximum font size
	 */
	public float getMaximumFontSize();
	
	/**
	 * Return minimum font size
	 * 
	 * @return
	 * 		minimum font size
	 */
  	public float getMinimumFontSize();

	/**
	 * Sets the default font
	 * 
	 * @param font
	 * 		new default font
	 */
  	public void setFont(Font font);
	
  	/**
  	 * sets the minimal allowed font size
  	 * 
  	 * @param size
  	 * 		minimal allowed font size
  	 */
  	public void setMinimumFontSize(float size);
	
  	/**
  	 * sets the maximal allowed font size
  	 * 
  	 * @param size
  	 * 		maximal allowed font size
  	 */
  	public void setMaximumFontSize(float size);

}
