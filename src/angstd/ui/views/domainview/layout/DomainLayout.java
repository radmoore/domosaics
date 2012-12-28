package angstd.ui.views.domainview.layout;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.layout.AbstractDomainLayout.ArrangementParameter;
import angstd.ui.views.view.layout.ViewLayout;



/**
 * Class DomainLayout specifies all methods necessary to layout
 * the ArrangementComponents within a DomainView.
 * <p>
 * The layout is actually done when layoutArrangements with specified
 * layout bounds is triggered. The implementing layout class tries
 * to layout all arrangements in the given space by successively
 * invoking layoutArrangement().
 * <p>
 * Methods which are needed from external classes which want to know
 * details abaout the layout, like the maximal length of an visible
 * arrangement or the space used for layouting the arrangements
 * are also provided by this interface.
 * 
 * @author Andreas Held
 *
 */
public interface DomainLayout extends ViewLayout{
	public static final Font ARRANGEMENTFONT = new Font("Arial", Font.PLAIN, 12);
	public static final Font DOMAINFONT = new Font("Arial", Font.BOLD, 12);
	public static final int MIN_DA_HEIGHT = 12 + 4;	// FONT + 4 araound 
	public static final int MAX_DA_HEIGHT = 12 + 8; // FONT + 8 araound 
	public static final int DA_SPACE = 12;
	
	/**
	 * Returns the used parameter to layout the view properly
	 * 
	 * @return
	 * 		used parameter to layout the view
	 */
	public ArrangementParameter getDomainParams();
	
	/**
	 * Method to layout arrangements on given layout bounds.
	 * Delegates to layoutArrangements(int, int, int, int, int).
	 * 
	 * @param x
	 * 		x coordinate of the layout bounds
	 * @param y
	 * 		y coordinate of the layout bounds 
	 * @param size
	 * 		the width and height of the layout bounds 
	 */
	public void layoutArrangements(int x, int y, Dimension size);
	
	/**
	 * Method to layout arrangements on given layout bounds.
	 * 
	 * @param x
	 * 		x coordinate of the layout bounds
	 * @param y
	 * 		y coordinate of the layout bounds 
	 * @param width
	 * 		width of the layout bounds 
	 * @param height
	 * 		height of the layout bounds 
	 */
	public abstract void layoutArrangements(int x, int y, int width, int height);
	
	/**
	 * Layouts an arrangement within the given space
	 * 
	 * @param dac
	 * 		the arrangement component to be layouted
	 * @param x
	 * 		the x coordinate of the given layout space
	 * @param y
	 * 		the y coordinate of the given layout space
	 * @param width
	 * 		the width of the given layout space
	 * @param height
	 * 		the height of the given layout space
	 */
	public void layoutArrangement(ArrangementComponent dac, int x, int y, int width, int height);
	
	/**
	 * Return the arrangement bounds for all layouted arrangements.
	 * 
	 * @return
	 * 		arrangement bounds for all layouted arrangements.	
	 */
	public Rectangle getDomainBounds();
	
	/**
	 * Sets the space in where arrangements are layouted
	 * 
	 * @param bounds
	 * 		space in where arrangements are layouted
	 */
	public void setDomainBounds(Rectangle bounds);
	
	/**
	 * Return the maximal needed space to draw the biggest arrangement.
	 * This information comes in handy outside of the layout class
	 * for instance within the DomainRulerRenderer to determine 
	 * the rulers length.
	 * 
	 * @return
	 * 		needed space to draw the biggest arrangement
	 */
	public int getMaxLen();
	
	/**
	 * Calculate the needed space to layout the arrangement properly.
	 * The actual size is determined by the subclasses.
	 * 
	 * @param da
	 * 		the Domain arrangement which preferred size has to be determined
	 * @return
	 * 		the preferred size to layout the specified domain arrangement
	 */
	public Dimension getPreferredArrangementSize(DomainArrangement da);
}
