package angstd.ui.views.view.layout;

import java.awt.Container;

import angstd.ui.views.view.View;

/**
 * The ViewLayout interface has to be implemented by all 
 * view specific classes which manage the layout process for
 * their [@link ViewComponent}s.
 * <p>
 * A view renderer then just have to renderer the view components
 * at the posiito which are assigned to the component during the
 * layout process.
 * 
 * @author Andreas Held
 *
 */
public interface ViewLayout {

	/** 
	 * Creates a reference to the view which layout should be calculated.
	 * 
	 * @param view
	 * 		the view which layout should be calculated.
	 */
	public void setView(View view);
	
	/**
	 * Layouts the view components on the parent panel by assigning
	 * them positions.
	 * 
	 * @param parent
	 * 		the displaying parent container of the view
	 */
	public void layoutContainer(Container parent);

}
