package angstd.ui.views.view.renderer;

import java.awt.Graphics2D;

import angstd.ui.views.view.AbstractView;



/**
 * All renderer assigned to a view must implement the renderer() method.
 * <p>
 * The AbstractView implementation triggers this render method 
 * for each added renderer during the view rendering.
 * <p>
 * For further details look into {@link AbstractView}.
 * 
 * @author Andreas Held
 *
 */
public interface Renderer {

	/**
	 * Specifies the rendering process for the renderer.
	 * 
	 * @param g
	 * 		the actual graphical context
	 */
	public void render(Graphics2D g);
}
