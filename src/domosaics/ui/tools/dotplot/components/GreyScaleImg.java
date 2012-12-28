package domosaics.ui.tools.dotplot.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import domosaics.model.dotplot.Dotplot;


/**
 * Image class to represent the graphical content of a dotplot as
 * gray scale image.
 * 
 * @author Andreas Held
 *
 */
public class GreyScaleImg {
	  
	/** the gray scale image used to represent a dotplot */
	protected BufferedImage grayImg;

	/**
	 * Returns the gray scale image of the dotplot
	 * 
	 * @return
	 * 		gray scale image of the dotplot
	 */
	public BufferedImage get() {
		return grayImg;
	}
	
	/**
	 * Creates a gray scale image out of a dotplot
	 * 
	 * @param dotplot
	 * 		the dotplot data structure being used to create the gray scale image
	 */
	public void create(Dotplot dotplot) {
		int[][] dotMatrix = dotplot.getDotMatrix();
		Dimension dim = dotplot.getDim();
		
		// create grayscale image of the plot
		grayImg = new BufferedImage(dim.width, dim.height,BufferedImage.TYPE_BYTE_GRAY); 
	
		Graphics2D imgG2D = grayImg.createGraphics(); 

		imgG2D.setColor(Color.white);
		imgG2D.fillRect(0, 0, dim.width, dim.height);

		// fill the image
		imgG2D.setColor(Color.black);
		for (int r = 0; r < dim.height; r++)
			for (int c = 0; c < dim.width; c++) 
				if (dotMatrix[r][c] >= dotplot.getCutoffThres())
					imgG2D.drawLine(c, r, c, r);

		imgG2D.dispose();
	}
}
