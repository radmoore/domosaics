package angstd.ui.views.domainview.components.shapes;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;



/**
 * Shape enumeration for domain shaping using the 
 * predefined {@link AngstdPolygon}s.
 * 
 * @author Andreas Held
 *
 */
public enum DomainShapes {

	ROUNDRECTANGLE (new RoundRectangle2D.Double(),			0),
	ARROW (new AngstdPolygon(AngstdPolygon.ARROW),			1),
	RAGGED (new AngstdPolygon(AngstdPolygon.RAGGED),		2),
	ZIGZAG (new AngstdPolygon(AngstdPolygon.ZIGZAG),		3),
	TWOTRAP (new AngstdPolygon(AngstdPolygon.TWOTRAP),		4),
	HORSESHOE (new AngstdPolygon(AngstdPolygon.HORSESHOE),	5),
	PENTAGON (new AngstdPolygon(AngstdPolygon.PENTAGON),	6),
	HEXAGON (new AngstdPolygon(AngstdPolygon.HEXAGON),		7),
	RHOMBOID (new AngstdPolygon(AngstdPolygon.RHOMBOID),	8),
	RHOMBOID2 (new AngstdPolygon(AngstdPolygon.RHOMBOID2),	9),
	MUSHROOM (new AngstdPolygon(AngstdPolygon.MUSHROOM),	10),
	TETRIS (new AngstdPolygon(AngstdPolygon.TETRIS),		11),
	DIAMOND (new AngstdPolygon(AngstdPolygon.DIAMOND),		12),
	ARROW2 (new AngstdPolygon(AngstdPolygon.ARROW2),		13),
	PLUS (new AngstdPolygon(AngstdPolygon.PLUS),			14)
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
