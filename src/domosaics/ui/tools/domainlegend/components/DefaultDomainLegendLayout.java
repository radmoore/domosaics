package domosaics.ui.tools.domainlegend.components;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import domosaics.ui.tools.domainlegend.DomainLegendView;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;




/**
 * DefaultDomainLegendLayout does the layouting of the legend components.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainLegendLayout implements ViewLayout {

	/** width of a domain */
	public static final int PROPSIZEWIDTH = 40;
	
	/** height of a domain */
	public static final int PROPSIZEHEIGHT = 18;
	
	/** minimum horizontal distance between two domains */
	public static final int MINDISTANCEHORIZONTAL = 10;
	
	/** minimum vertical distance between two domains */
	public static final int MINDISTANCEVERTICAL = 8;
	
	/** the view to be layouted */
	protected DomainLegendView view;
	
	/** the backend domain view on which the legend is based on */
	protected DomainViewI domView;
	
	/** a list of all domain families which have to be layouted */
	protected List<DomainComponent> doms;
	
	/** the largest label of a domain family */
	protected LegendComponent largestLabel;
	
	
	/**
	 * Sets the view to the layout and therefore initializes the layout
	 * 
	 * @param view
	 * 		the view to be layouted
	 */
	public void setView(View view) {
		this.view = (DomainLegendView) view;
		domView = this.view.getDomView();
		
		this.doms = this.view.getDoms();
	}
	
	/**
	 * determining that the legend have to be re layouted
	 */
	public void legendStructureChanged() {
		largestLabel = null;
	}

	/**
	 * calculates the area in which the layout has to take place
	 * 
	 * @param parent
	 * 		the parent container
	 */
	public void layoutContainer(Container parent) {
		if (largestLabel == null)
			getLargestLabel();
		
		// get the size and insets of the view
		Dimension viewSize = parent.getSize();
		Insets viewInsets = parent.getInsets();
		
		// get width and height without insets
		int width = viewSize.width - viewInsets.left - viewInsets.right;
		int height = viewSize.height - viewInsets.top - viewInsets.bottom;

		// finally calculate the layout area
		Rectangle layoutArea = new Rectangle(viewInsets.left, viewInsets.top, width, height);
		layoutLegend(layoutArea.x, layoutArea.y, layoutArea.width, layoutArea.height);
	}
	
	/**
	 * Method which does the layouting.
	 * 
	 * @param x
	 * 		x coordinate of the layout area
	 * @param y
	 * 		y coordinate of the layout area
	 * @param width
	 * 		width of the layout area
	 * @param height
	 * 		height of the layout area
	 */
	public void layoutLegend(int x, int y, int width, int height) {
		if (width < 0 || height < 0)
			return;
		
		// calculate params
		if (largestLabel == null)
			getLargestLabel();
		
		Dimension maxDim = getMaxLegendComponent();
		int columnwidth = maxDim.width;
		int cols = (int) Math.floor(width / (double) columnwidth);
		if (cols == 0)
			cols = 1;
		int colBetweenSpace = (int) Math.floor(width % columnwidth);
		int rowheight = PROPSIZEHEIGHT+MINDISTANCEVERTICAL;

		Iterator<LegendComponent>iter;
		if (view.getLegendLayoutManager().isSortAlphabetically())
			iter = view.getLegendComponentManager().getAlphabeticalOrderedComponentsIterator();
		else
			iter = view.getLegendComponentManager().getByFrequenceOrderedComponentsIterator();
		
		int row = 0;
		int col = 0;
		while (iter.hasNext()) {
			LegendComponent lc = iter.next();
			lc.setBounds(
					x + col * columnwidth, 
					y + row * rowheight, 
					columnwidth-colBetweenSpace/2,
					rowheight-MINDISTANCEVERTICAL
			);
			col++;
			if (col == cols) {
				col = 0;
				row++;
			}
		}
	}
	
	/**
	 * Method returning the largest component. This can be used to
	 * get the column width.
	 * 
	 * @return
	 * 		dimension of the largest component
	 */
	public Dimension getMaxLegendComponent() {
		Dimension maxDim = new Dimension(0, 0);
		for (int i = 0; i < doms.size(); i++) {
			LegendComponent lc = view.getLegendComponentManager().getComponent(doms.get(i));
			Dimension actDim = getBounds(lc);
			if (maxDim.width < actDim.width)
				maxDim = actDim;
		}
		return maxDim;
	}
	
	/**
	 * Helper method to determine the largest label
	 */
	protected void getLargestLabel() {
		largestLabel = view.getLegendComponentManager().getComponent(doms.get(0));
		for (int i = 1; i < doms.size(); i++) {
			LegendComponent lc = view.getLegendComponentManager().getComponent(doms.get(i));
			if (lc.getLabel() != null && largestLabel.getLabel().length() < lc.getLabel().length())
				largestLabel = lc;
		}
	}
	
	/**
	 * Helper method to determine the width of a rendered string
	 * 
	 * @param label
	 * 		the label to be rendered
	 * @return
	 * 		the width needed to render the label
	 */
	protected int getStringWidth(String label) {
		Font f = new Font("Arial", 1, 14);
		return SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(f), label);
	}
	
	/**
	 * Returns the bounds of a legend component 
	 * 
	 * @param lc
	 * 		the legend component which bounds are requested
	 * @return
	 * 		 the bounds of the specified legend component 
	 */
	protected Dimension getBounds(LegendComponent lc) {
		// DomainSize + 4 + DomainLabel + mindistance betwwen cols/2
		int width = PROPSIZEWIDTH + 4 + getStringWidth(lc.getLabel()) + MINDISTANCEHORIZONTAL/2;
		int height = PROPSIZEHEIGHT;
		return new Dimension(width, height);
	}
}
