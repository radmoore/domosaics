package domosaics.ui.tools.domainmatrix.components;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import domosaics.ui.tools.domainmatrix.DomainMatrixView;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;




/**
 * DefaultDomainLegendLayout does the layouting of the domain matrix.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainMatrixLayout implements ViewLayout{

	/** width of a domain */
	public static final int PROPSIZEWIDTH = 40;
	
	/** height of a domain */
	public static final int PROPSIZEHEIGHT = 18;
	
	/** minimal width for table columns */
	public static final int MINCOLWIDTH = 50;
	
	/** distance between table columns */
	public static final int COLDISTANCE = 10;
	
	/** distance between table rows */
	public static final int ROWDISTANCE = 5;
	
	/** fontsize used to render the header labels */
	public static final int FONTSIZE = 18;
	
	/** the view to be layouted */
	protected DomainMatrixView view;
	
	/** the data of this matrix represented by a 2d array of DomainMatrixEntry components */
	protected DomainMatrixEntry[][] data;
	
	/** the largest label of a domain family */
	protected DomainMatrixEntry largestLabel;
	
	/** the width of the largest label */
	protected int largestLabelWidth;
	
	
	/**
	 * Sets the view to the layout and therefore initializes the layout
	 * 
	 * @param view
	 * 		the view to be layouted
	 */
	public void setView(View view) {
		this.view = (DomainMatrixView) view;
		this.data = this.view.getData();
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
		layoutMatrix(layoutArea.x, layoutArea.y, layoutArea.width, layoutArea.height);
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
	public void layoutMatrix(int x, int y, int width, int height) {
		if (width < 0 || height < 0)
			return;
		
		// calculate params
		if (largestLabel == null)
			getLargestLabel();
		
		int rows = data.length;
		int cols = data[0].length;
		int colWidth = getMaxColWidth(width);
		int rowHeight = getMaxRowHeight(height);
		
		double offsetX = 1.0 / cols;
		double offsetY = 1.0 / rows;

		// calc relative coordinates
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++) {
				data[r][c].getRelativeBounds().x = c * offsetX;
				data[r][c].getRelativeBounds().y = r * offsetY;
			}
		
		// calc real coordiantes
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++) {
				data[r][c].setBounds(
					(int) (x + Math.round(data[r][c].getRelativeBounds().x * width)),
					(int) (y + Math.round(data[r][c].getRelativeBounds().y * height)),					 						 
					(int) Math.round(colWidth),
					(int) Math.round(rowHeight)	
				);
			}
	}
	
	/**
	 * Helper method to determine the largest label
	 */
	public void getLargestLabel() {
		largestLabel = data[0][0];
		for (int i = 1; i < data[0].length; i++) 
			if (largestLabel.getLabel().length() < data[0][i].getLabel().length())
				largestLabel = data[0][i];
		
		largestLabelWidth = getStringWidth(largestLabel.getLabel());
	}
	
	/**
	 * Helper method to determine the width of a rendered string
	 * 
	 * @param label
	 * 		the label to be rendered
	 * @return
	 * 		the width needed to render the label
	 */
	public int getStringWidth(String label) {
		Font f = new Font("Arial", 0, FONTSIZE);
		return SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(f), label);
	}
	
	/**
	 * Method to determine the maximal needed width for columns.
	 * 
	 * @param viewWidth
	 * 		the view width
	 * @return
	 * 		maximal needed width for columns.
	 */
	public int getMaxColWidth(int viewWidth) {
		if (largestLabel == null)
			getLargestLabel();

		// check if cols fit into screen
		int cols = data[0].length;
		
		// maxSpace for column
		int maxColWidth = (int) Math.ceil(viewWidth / cols);
		
		if (largestLabelWidth+COLDISTANCE < maxColWidth)
			return maxColWidth;
		else if (largestLabelWidth+COLDISTANCE > MINCOLWIDTH)
			return largestLabelWidth+COLDISTANCE;
		else
			return MINCOLWIDTH;
	}
	
	/**
	 * Method to determine the maximal needed height for rows.
	 * 
	 * @param viewHeight
	 * 		the view height
	 * @return
	 * 		maximal needed height for rows.
	 */
	public int getMaxRowHeight(int viewHeight) {
		// check if cols fit into screen
		int rows = data.length;
		
		// maxSpace for column
		int maxRowHeight = (int) Math.ceil(viewHeight / rows);
		
		if (FONTSIZE+ROWDISTANCE < maxRowHeight)
			return maxRowHeight;
		else
			return FONTSIZE+ROWDISTANCE;
	}
	
	/**
	 * Calculates the needed space to draw the hole table
	 * 
	 * @param viewWidth
	 * 		the view width
	 * @return
	 * 		needed space to draw the hole table
	 */
	public Dimension getNeededDim(int viewWidth) {
		int cols = data[0].length;
		int rows = data.length;
		
		int colWidth = getMaxColWidth(viewWidth);
		int rowHeight = FONTSIZE + ROWDISTANCE;
		
		return new Dimension(colWidth*cols, rowHeight*rows);
	}

}

