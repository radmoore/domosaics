package domosaics.ui.views.domainview.components.shapes;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;



/**
 * Shape enumeration for domain shaping using the 
 * predefined {@link DoMosaicsPolygon}s.
 * 
 * @author Andreas Held
 *
 */
public enum DomainShapes {

	ROUNDRECTANGLE (new RoundRectangle2D.Double(),			0),
	ARROW (new DoMosaicsPolygon(DoMosaicsPolygon.ARROW),			1),
	RAGGED (new DoMosaicsPolygon(DoMosaicsPolygon.RAGGED),		2),
	ZIGZAG (new DoMosaicsPolygon(DoMosaicsPolygon.ZIGZAG),		3),
	TWOTRAP (new DoMosaicsPolygon(DoMosaicsPolygon.TWOTRAP),		4),
	HORSESHOE (new DoMosaicsPolygon(DoMosaicsPolygon.HORSESHOE),	5),
	PENTAGON (new DoMosaicsPolygon(DoMosaicsPolygon.PENTAGON),	6),
	HEXAGON (new DoMosaicsPolygon(DoMosaicsPolygon.HEXAGON),		7),
	RHOMBOID (new DoMosaicsPolygon(DoMosaicsPolygon.RHOMBOID),	8),
	RHOMBOID2 (new DoMosaicsPolygon(DoMosaicsPolygon.RHOMBOID2),	9),
	MUSHROOM (new DoMosaicsPolygon(DoMosaicsPolygon.MUSHROOM),	10),
	TETRIS (new DoMosaicsPolygon(DoMosaicsPolygon.TETRIS),		11),
	DIAMOND (new DoMosaicsPolygon(DoMosaicsPolygon.DIAMOND),		12),
	ARROW2 (new DoMosaicsPolygon(DoMosaicsPolygon.ARROW2),		13),
	PLUS (new DoMosaicsPolygon(DoMosaicsPolygon.PLUS),			14),
	RECTANGLE (new Rectangle2D.Double(),			15)
	;
		
	private Shape shape;
	private int index;
	
	private DomainShapes(Shape shape, int index){
		this.shape = shape;
		this.index = index;
	}
	
	/**
	 * Return the shape
	 * 
	 * @return
	 * 		shape
	 */
	public Shape getShape() {
		return shape;
	}
	
	/**
	 * Return the index of the specified shape
	 * 
	 * @return
	 * 		index of the specified shape
	 */
	public int getIndex() {
		return index;
	}
	


	
}
