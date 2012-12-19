package domosaics.ui.views.domainview.components.shapes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

/**
 * AngstdShapeIcon converts shapes for domains into icons,
 * which can be displayed for instance within the shape chooser.
 * <p>
 * The icon may want to leave space to the edges, therefore the
 * default icon width/height differ from the shape width/height.
 * The setting leaveSpace within the constructor determines then
 * whether or not their should be space to the icons edges.
 * 
 * @author Andreas Held
 *
 */
public class AngstdShapeIcon implements Icon {
	/** icon width */
	public static final int DEFAULT_WIDTH = 80;
	
	/** icon height */
	public static final int DEFAULT_HEIGHT = 30;
	
	/** shape width */
	public static final int SHAPE_WIDTH = 40;
	
	/** shape height */
	public static final int SHAPE_HEIGHT = 20;
	
	/** the shape to render */
	protected Shape shape;
	
	/** the color used to fill the rendered shape */
	protected Color color;

	/** flag indicating whether or not their should be space to the icon edges */
	protected boolean leaveSpace;

	
	/**
	 * Constructor for a new AngstdShapeIcon
	 * 
	 * @param shape
	 * 		the shape to be converted into a Icon
	 * @param color
	 * 		the color used to fill the shape
	 * @param leaveSpace
	 * 		flag indicating whether or not there should be space to the icons edges
	 */
	public AngstdShapeIcon(Shape shape, Color color, boolean leaveSpace) {
		this.shape = shape;
		this.color = color;
		this.leaveSpace = leaveSpace;
	    initShape();
	}

	/**
	 * Method for initializing the shape with the specified parameters.
	 */
	private void initShape() {
		int x = (leaveSpace) ? 20 : 0;
		int y = (leaveSpace) ? 5 : 0;
		
		if (shape instanceof RoundRectangle2D.Double) 
			((RoundRectangle2D.Double) shape).setRoundRect(x, y, SHAPE_WIDTH, SHAPE_HEIGHT, 20, 20);
		else if (shape instanceof AngstdPolygon)
			((AngstdPolygon) shape).setPolygon(x, y, SHAPE_WIDTH, SHAPE_HEIGHT);
	}

	/**
	 * @see Icon
	 */
	public int getIconHeight() {
		if (leaveSpace)
			return DEFAULT_HEIGHT;
		return SHAPE_HEIGHT+2;
	}

	/**
	 * @see Icon
	 */
	public int getIconWidth() {
		if (leaveSpace)
			return DEFAULT_WIDTH;
		return SHAPE_WIDTH+2;
	}

	/**
	 * Paints the stored icon at the specified position.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g;
		Color oldC = g2d.getColor();

		g2d.translate(x, y);
		g2d.setColor(color);

		g2d.fill(shape);
		g2d.setColor(Color.black);
		g2d.draw(shape);
		
		g2d.translate(-x, -y);
		g2d.setColor(oldC);
	}
}
