package domosaics.ui.views.domainview.components.shapes;

import java.awt.Polygon;

/**
 * Class defining the shape forms scaled from 0 to 1. To actually
 * set the shape the setPolygon() method has to be called with 
 * the given bounds as parameter. This class creates the shape
 * then based on the relative coordinates so that it fits into
 * the specified bounds.
 * 
 * @author Andreas Held
 *
 */
public class DoMosaicsPolygon extends Polygon{
	private static final long serialVersionUID = 1L;
	
	public static final int TWOTRAP = 0;
	protected static final double[] TWOTRAPX = {0.0, 0.1, 0.9, 1.0, 0.9, 0.1};
	protected static final double[] TWOTRAPY = {0.5, 0.0, 0.0, 0.5, 1.0, 1.0};
	
	public static final int ARROW = 1;
	protected static final double[] ARROWX = {0.0, 0.9, 1.0, 0.9, 0.0, 0.1};
	protected static final double[] ARROWY = {0.0, 0.0, 0.5, 1.0, 1.0, 0.5};

	public static final int RAGGED = 2;
	protected static final double[] RAGGEDX = {0.0, 0.1, 0.1, 0.3, 0.3, 0.5, 0.5, 0.7, 0.7, 0.9, 0.9, 1.0, 1.0, 0.8, 0.8, 0.6, 0.6, 0.4, 0.4, 0.2, 0.2, 0.0};
	protected static final double[] RAGGEDY = {0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.2, 0.2, 1.0, 1.0, 0.8, 0.8, 1.0, 1.0, 0.8, 0.8, 1.0, 1.0};

	public static final int ZIGZAG = 3;
	protected static final double[] ZIGZAGX = {0.0, 0.17, 0.33, 0.5, 0.67, 0.83, 1.0, 1.0, 0.83, 0.67, 0.5, 0.33, 0.17, 0.0};
	protected static final double[] ZIGZAGY = {0.0, 0.20, 0.00, 0.2, 0.00, 0.20, 0.0, 1.0, 0.80, 1.00, 0.8, 1.00, 0.80, 1.0};
	
	public static final int HORSESHOE = 4;
	protected static final double[] HORSESHOEX = {0.0, 0.33, 0.33, 0.67, 0.67, 1.0, 1.0, 0.0};
	protected static final double[] HORSESHOEY = {0.0, 0.00, 0.30, 0.30, 0.00, 0.0, 1.0, 1.0};
	
	public static final int H = 5;
	protected static final double[] HX = {0.0, 0.33, 0.33, 0.67, 0.67, 1.0, 1.0, 0.67, 0.67, 0.33, 0.33, 0.0};
	protected static final double[] HY = {0.0, 0.00, 0.20, 0.20, 0.00, 0.0, 1.0, 1.00, 0.80, 0.80, 1.00, 1.0};
	
	public static final int MUSHROOM = 6;
	protected static final double[] MUSHROOMX = {0.0, 1.0, 1.0, 0.67, 0.67, 0.33, 0.33, 0.00};
	protected static final double[] MUSHROOMY = {0.0, 0.0, 0.8, 0.80, 1.00, 1.00, 0.80, 0.80};
	
	public static final int TETRIS = 7;
	protected static final double[] TETRISX = {0.0, 0.33, 0.33, 0.67, 0.67, 1.00, 1.0, 0.0};
	protected static final double[] TETRISY = {0.2, 0.20, 0.00, 0.00, 0.20, 0.20, 1.0, 1.0};
	
	public static final int DIAMOND = 8;
	protected static final double[] DIAMONDX = {0.0, 0.5, 1.0, 0.5};
	protected static final double[] DIAMONDY = {0.5, 0.0, 0.50, 1.0};
	
	public static final int RHOMBOID = 9;
	protected static final double[] RHOMBOIDX = {0.2, 1.0, 0.8, 0.0};
	protected static final double[] RHOMBOIDY = {0.0, 0.0, 1.0, 1.0};
	
	public static final int RHOMBOID2 = 10;
	protected static final double[] RHOMBOID2X = {0.0, 1.0, 0.8, 0.2};
	protected static final double[] RHOMBOID2Y = {0.0, 0.0, 1.0, 1.0};
	
	public static final int ARROW2 = 11;
	protected static final double[] ARROW2X = {0.0, 0.33, 0.33, 0.67, 0.67, 1.0, 0.67, 0.67, 0.33, 0.33};
	protected static final double[] ARROW2Y = {0.5, 0.00, 0.20, 0.20, 0.00, 0.5, 1.00, 0.80, 0.80, 1.00};
	
	public static final int PLUS = 12;
	protected static final double[] PLUSX = {0.0, 0.33, 0.33, 0.67, 0.67, 1.0, 1.0, 0.67, 0.67, 0.33, 0.33, 0.0};
	protected static final double[] PLUSY = {0.2, 0.20, 0.00, 0.00, 0.20, 0.2, 0.8, 0.80, 1.00, 1.00, 0.80, 0.8};
	
	public static final int PENTAGON = 13;
	protected static final double[] PENTAGONX = {0.0, 0.2, 0.8, 1.0, 0.5};
	protected static final double[] PENTAGONY = {0.5, 0.0, 0.0, 0.5, 1.0};
	
	public static final int HEXAGON = 14;
	protected static final double[] HEXAGONX = {0.0, 0.2, 0.8, 1.0, 0.8, 0.2};
	protected static final double[] HEXAGONY = {0.5, 0.0, 0.0, 0.5, 1.0, 1.0};
	
	/** number of points for this shape */
	protected int numPoints;
	
	/** x coordinates for this shape */
	protected double[] xPoints;
	
	/** y coordinates for this shape */
	protected double[] yPoints;
	
	/**
	 * Constructor for a new DoMosaicsPolygon of the specified type 
	 * initializing the given coordinates.
	 * 
	 * @param style
	 * 		the style of the shape to be created
	 */
	public DoMosaicsPolygon(int style) {
		super();
		init(style);
	}
	
	/**
	 * Uses the relative coordinates to specify the real shape coordinates
	 * within the specified bounds.
	 * 
	 * @param x
	 * 		x position of the shape
	 * @param y
	 * 		y position of the shape
	 * @param width
	 * 		width of the shape
	 * @param height
	 * 		height of the shape
	 */
	public void setPolygon(double x, double y, double width, double height) {
		if (super.npoints != 0)
			super.reset();

		for (int i = 0; i < numPoints; i++) 
			super.addPoint((int) (x + width*xPoints[i]), (int) (y + height*yPoints[i]));
	}
	
	/**
	 * initializes the coordinates based on the specified shape style
	 * 
	 * @param style
	 * 		the style of the shape to be created
	 */
	protected void init(int style) {
		switch(style) {
			case TWOTRAP: 	xPoints = TWOTRAPX;  	yPoints = TWOTRAPY; 	break;
			case ARROW:   	xPoints = ARROWX;    	yPoints = ARROWY;   	break;
			case RAGGED:  	xPoints = RAGGEDX;   	yPoints = RAGGEDY; 	 	break;
			case ZIGZAG:  	xPoints = ZIGZAGX;   	yPoints = ZIGZAGY;   	break;
			case HORSESHOE:	xPoints = HORSESHOEX;   yPoints = HORSESHOEY; 	break;
			case H:			xPoints = HX;   		yPoints = HY; 			break;
			case MUSHROOM:	xPoints = MUSHROOMX;   	yPoints = MUSHROOMY; 	break;
			case TETRIS:	xPoints = TETRISX;   	yPoints = TETRISY; 		break;
			case DIAMOND:	xPoints = DIAMONDX;   	yPoints = DIAMONDY; 	break;
			case RHOMBOID:	xPoints = RHOMBOIDX;   	yPoints = RHOMBOIDY; 	break;
			case RHOMBOID2:	xPoints = RHOMBOID2X;   yPoints = RHOMBOID2Y; 	break;
			case ARROW2:	xPoints = ARROW2X;   	yPoints = ARROW2Y; 		break;
			case PLUS:		xPoints = PLUSX;   		yPoints = PLUSY; 		break;
			case PENTAGON:	xPoints = PENTAGONX;   	yPoints = PENTAGONY; 	break;
			case HEXAGON:	xPoints = HEXAGONX;   	yPoints = HEXAGONY; 	break;
		}
		numPoints = xPoints.length;
	}
}
