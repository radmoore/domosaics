package domosaics.ui.views.view.manager;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import domosaics.ui.views.view.components.ViewComponent;




/**
 * Basic implementation of the FontManager interface. This
 * class provides the functionalities for font mapping to 
 * view components.
 * <p>
 * For further details see {@link SelectionManager}.
 * 
 * @author Andreas Held
 *
 * @param <C>
 * 		@see FontManager
 */
public class DefaultFontManager<C extends ViewComponent> 
	   extends DefaultViewManager 
	   implements FontManager<C> 
{
	
	/** mapping between view components and their fonts */
	protected Map<C, Font> comp2fonts;
	
	/** the default font */
	protected Font labelFont = new Font("Arial", Font.PLAIN, 12);
	
	/** the minimum font size */
	protected float minimumFontSize;
	
	/** the maximum font size */
	protected float maximumFontSize;
	
	
	/**
	 * Basic constructor initializing the font managers minimum
	 * font size as 5 and maximum as 12.
	 */
	public DefaultFontManager() {
		this(5f, 12f, Font.PLAIN);
	}
	
	/**
	 * Constructor which allows a on the fly setting for the minimal 
	 * and maximal allowed font size.
	 * 
	 * @param min
	 * 		minimal allowed font size
	 * @param max
	 * 		maximal allowed font size
	 * @param style
	 * 		the font style, e.g. Font.PLAIN
	 */
	public DefaultFontManager(float min, float max, int style) {
		comp2fonts = new HashMap<C, Font>();
		this.minimumFontSize = min;
		this.maximumFontSize = max;
		if (style != Font.PLAIN)
			labelFont = new Font("Arial", style, 12);
	}
	
  	/* ******************************************************************* *
	 *   							GET methods							   *
	 * ******************************************************************* */

	/**
	 * @see FontManager
	 */
  	@Override
	public Font getFont(C comp){
		if(comp == null) 
			return getFont();
   		if (comp2fonts.get(comp) == null)
   			return getFont();
   		else
   			return comp2fonts.get(comp);
	}
  	
	/**
	 * @see FontManager
	 */
	@Override
	public Font getFont() {
		return labelFont;
	}
	
	/**
	 * @see FontManager
	 */
	@Override
	public float getMaximumFontSize(){
		return maximumFontSize;
	}
	
	/**
	 * @see FontManager
	 */
  	@Override
	public float getMinimumFontSize(){
		return minimumFontSize;
	}
	
	
	/* ******************************************************************* *
	 *   							SET methods							   *
	 * ******************************************************************* */
	
	/**
	 * @see FontManager
	 */
  	@Override
	public void setFont(C nc, Font font) {
  		if (font == null) 
			return;
		
		if (font.getSize() < minimumFontSize)
			setMinimumFontSize(font.getSize());
		if (font.getSize() > maximumFontSize)
			setMaximumFontSize(font.getSize());
	
		comp2fonts.put(nc, font);
		structuralChange();
	}
	
	/**
	 * @see FontManager
	 */
  	@Override
	public void setFont(Font font) {
		if (font == null) 
			return;
		
		if (font.getSize() < minimumFontSize)
			setMinimumFontSize(font.getSize());
		if (font.getSize() > maximumFontSize)
			setMaximumFontSize(font.getSize());
		
		this.labelFont = font;
		structuralChange();
	}
	
	/**
	 * @see FontManager
	 */
  	@Override
	public void setMinimumFontSize(float size){
		if (size > 0 && size < maximumFontSize)
		this.minimumFontSize = size;
	}
	
	/**
	 * @see FontManager
	 */
  	@Override
	public void setMaximumFontSize(float size){
		if (size > minimumFontSize)
			this.maximumFontSize = size;
	}
}
