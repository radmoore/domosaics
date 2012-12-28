package domosaics.ui.tools.dotplot.components;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import domosaics.ui.tools.dotplot.DotplotView;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;




/**
 * Layout used to layout the dotplot view. This class uses the component
 * manager of the dotplot view instead of the component manager of the 
 * domain view so that no additional domains are added to the backend domainview
 * and the existing domains aren't relayouted in the dotplot style.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDotplotLayout implements ViewLayout {

	/** height of a domain */
	public static final int DOMHEIGHT = 20;
	
	/** height of a label used to draw the protein name */
	public static final int LABELHEIGHT = 14;
	
	/** distance between the two arrangements */
	public static final int DIST2DA = 10;
	
	/** distance between an arrangement and the dotplot */
	public static final int DIST2PLOT = 10;
	
	/** the dotplot view to be layouted */
	protected DotplotView view;
	
	/** the dimension needed to layout the hole dotplot correctly */
	protected Dimension neededDim;
	
	/** space needed from the dotplot to the top of the view */
	protected int upSpace = 0;
	
	/** space needed from the dotplot to the left side of the view */
	protected int leftSpace = 0;

	
	/**
	 * Sets the backend dotplot view
	 * 
	 * @param view
	 * 		backend dotplot view
	 */
	public void setView(View view) {
		this.view = (DotplotView) view;
	}
	
	/**
	 * Method indicating that the dotplot has to be relayouted.
	 */
	public void dotplotStructureChanged() {
		neededDim = null;
	}

	/**
	 * Layout the dotplot view by determining the space available
	 * 
	 * @param parent
	 * 		the parent frame embedding the view
	 */
	public void layoutContainer(Container parent) {
		if (neededDim == null)
			neededDim = getNeededDim();
		
		// get the size and insets of the view
		Dimension viewSize = parent.getSize();
		Insets viewInsets = parent.getInsets();
		
		// get width and height without insets
		int width = viewSize.width - viewInsets.left - viewInsets.right;
		int height = viewSize.height - viewInsets.top - viewInsets.bottom;

		// center the plot within the window
		Insets labelSpace = getInsets(width, height);
		
		// finally calculate the layout area
		Rectangle layoutArea = new Rectangle(viewInsets.left, viewInsets.top, width, height);
		
		layoutArea.x += labelSpace.left;
		layoutArea.y += labelSpace.top;
		layoutArea.width -= (labelSpace.left + labelSpace.right);
		layoutArea.height -= (labelSpace.bottom);
		
		layoutDotplot(layoutArea.x, layoutArea.y, layoutArea.width, layoutArea.height);
	}
	
	public Insets getInsets(int width, int height) {
		// insets of the view	
		Insets insets = new Insets(0,0,0,0);
		
		// center the plot within the window
		insets.left = (width - neededDim.width)/2;
		insets.right = insets.left;
		insets.top = (height - neededDim.height)/2;
		insets.bottom = insets.top;
		return insets;
	}
	
	/**
	 * Calculates and returns the needed space to layout the hole dotplot
	 * 
	 * @return
	 * 		 needed space to layout the hole dotplot
	 */
	public Dimension getNeededDim() {
		upSpace = getBounds(view.getDa1()).height + LABELHEIGHT + DIST2DA + DIST2PLOT;
		leftSpace = getBounds(view.getDa2()).height + LABELHEIGHT + DIST2DA + DIST2PLOT;
		
		int width = getBounds(view.getDa1()).width + leftSpace;
		int height = getBounds(view.getDa2()).width + upSpace;
		return (new Dimension(width, height));
	}

	/**
	 * Return the bounds for an arrangement
	 * 
	 * @param dac
	 * 		the arrangement which bounds are requested
	 * @return
	 * 		bounds for an arrangement
	 */
	public Dimension getBounds(ArrangementComponent dac) {
		int width = dac.getDomainArrangement().getLen(false);
		int height = DOMHEIGHT;
		return new Dimension(width, height);
	}
	
	/**
	 * Method layouting the dotplot within the available space
	 * 
	 * @param x
	 * 		the x coordinate of the given layout space
	 * @param y
	 * 		the y coordinate of the given layout space
	 * @param width
	 * 		the width of the given layout space
	 * @param height
	 * 		the height of the given layout space
	 */
	public void layoutDotplot(int x, int y, int width, int height) {
		if (width < 0 || height < 0)
			return;
		
		// layout upper arrangement
		ArrangementComponent dac1 = view.getDa1();
		dac1.setBounds(
				x + leftSpace,
				y + LABELHEIGHT + DIST2DA,
				getBounds(dac1).width,
				DOMHEIGHT
		);

		layoutDomains(dac1);
		
		// layout left arrangement
		ArrangementComponent dac2 = view.getDa2();

		int dac2translate = getBounds(dac2).width/2;
		
		dac2.setBounds(
				x + LABELHEIGHT + DIST2DA - dac2translate,
				y + upSpace				  + dac2translate - DOMHEIGHT/2,
				dac2translate*2,
				DOMHEIGHT
		);
			
		layoutDomains(dac2);	

		// layout dotplot
		DotplotComponent dotplot = 	view.getDotplotComponent();
	
		dotplot.setBounds(
				x+leftSpace,
				y+upSpace,
				neededDim.width-leftSpace,
				neededDim.height-upSpace
		);

	}
	
	
	/**
	 * Helper method to layout the domain components of the dotplot view
	 * 
	 * @param dac
	 * 		the arrangement component which domain components must be layouted
	 */
	private void layoutDomains(ArrangementComponent dac) {
		for (DomainComponent dc : view.getDomainComponentManager().getDomains(dac)) 
			dc.setBounds(	dac.getX() + dc.getDomain().getFrom(),
							dac.getY(),					 						 
							dc.getDomain().getLen(),
							DOMHEIGHT);	
	}
	
	
}
