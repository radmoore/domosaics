package domosaics.ui.views.domainview.manager;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import domosaics.model.arrangement.DomainFamily;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.components.shapes.DoMosaicsPolygon;
import domosaics.ui.views.domainview.components.shapes.DomainShapes;
import domosaics.ui.views.view.manager.DefaultViewManager;




/**
 * DomainShapeManager defines the mapping between DomainFamilys 
 * and their shapes. This class extends DefaultViewManager to ensure the
 * communication with the managed view.
 * <p>
 * Because the shapes are mapped by DomainFamily instead of DomainComponent
 * its not possible to shape each domain individually.
 * <p>
 * To request a shape for a domain the DomainComponent as well as the family
 * can be used. Shapes are stored by their index within {@link DomainShapes}.
 * Shapes can be requested by id as well as shape. The ID request would
 * for instance come in handy when exporting the view.
 * <p>
 * If no shape is associated with the requested DomainFamily a new Shape
 * from {@link DomainShapes} is associated ensuring that each 
 * DomainShape is used before starting with the first shape again. But
 * to make things more complicated and to ensure each shape/color 
 * combination being unique for domain families its not started with the first shape again
 * but after all shapes were used with the second. In the next run with the
 * third and so on.
 * <p>
 * When retrieving domain shapes, they are set by default to the
 * domains bounds. If this is not wanted e.g. within the domain 
 * legend view, the method getUnsetShape() is for you.
 * 
 * @author Andreas Held
 *
 */
public class DomainShapeManager extends DefaultViewManager {
	
	/** mapping between DomainFamilies and the associated shapes */ 
	protected Map<String, Integer> doms2shapes;
	
	/**
	 * Basic constructor for a new shape manager.
	 */
	public DomainShapeManager() {
		doms2shapes = new HashMap<String, Integer>();
	}
	
	/**
	 * Helper method to retrieve the next shape index. In a first step
	 * the number of runs is calculated, which means the number of
	 * complete usage of all shapes, e.g.: <br>
	 * all shapes used once => runs = 1 <br>
	 * In the second run the second shape would be returned first,
	 * in the third the third and so on. This ensures every shape/color 
	 * combination being unique for domain families
	 * 
	 * @return
	 * 		the next shape index to use
	 */
	protected int getNextShapeIndex() {
		int runs = (int) Math.floor(getNumDomainShapes() / DomainShapes.values().length);
		return (getNumDomainShapes() + runs) % DomainShapes.values().length;
	}
	
	/**
	 * Helper method converting the shapes bounds to the bounds of the
	 * associated DomainComponent.
	 * 
	 * @param dc
	 * 		the DomainComponent owning the shape
	 * @return
	 * 		the resized shape which fits noe to the DomainComponents bounds
	 */
	protected Shape convert2DomainDim(Shape shape, DomainComponent dc) {
		double x = dc.getX();
  		double y = dc.getTopLeft(); 
  		double width = dc.getWidth();
  		double height = dc.getHeight();
  		
  		// create shape with the specified dimension
  		if (shape instanceof RoundRectangle2D.Double) 
			((RoundRectangle2D.Double) shape).setRoundRect(x, y, width, height, 20, 20);
		else if (shape instanceof Rectangle2D.Double) 
			((Rectangle2D.Double) shape).setRect(x, y, width, height);
			else if (shape instanceof DoMosaicsPolygon)
				((DoMosaicsPolygon) shape).setPolygon(x, y, width, height); 
  		
  		return shape;
	}
	
	/**
	 * Helper method to retrieve a shape using a specified id.
	 * 
	 * @param id
	 * 		the shapes id
	 * @return
	 * 		the shape associated with the id
	 */
	protected Shape getShape(int id) {
		return DomainShapes.values()[id].getShape();
	}
	
  	
	/**
	 * Return the "naked" shape not expanded to the DomainComponents
	 * bounds.
	 * 
	 * @param dc
	 * 		the DomainComponent which shape is requested
	 * @return
	 * 		the shape associated to the domain components family
	 */
	public Shape getUnsetShape (DomainComponent dc) {
		if (doms2shapes.get(dc.getDomain().getFamily().getId()) == null) 
   			setDomainShape(dc, getNextShapeIndex());
  		return getShape(doms2shapes.get(dc.getDomain().getFamily().getId()));
	}
	
	/**
	 * Return the shape associated with the specified DomainComponent
	 * expanded  to the DomainComponents bounds.
	 * @param dc
	 * 		the DomainComponent which shape is requested
	 * @return
	 * 		the shape associated to the domain components family expanded to the DomainComponents bounds
	 */
  	public Shape getDomainShape(DomainComponent dc) {	
  		Shape shape;
  		if (doms2shapes.get(dc.getDomain().getFamily().getId()) == null) {
  			int shapeIndex = getNextShapeIndex();
  			shape = DomainShapes.values()[shapeIndex].getShape();
   			setDomainShape(dc, shapeIndex);
   		}
   		else
   			shape =  getShape(doms2shapes.get(dc.getDomain().getFamily().getId()));
  		
  		convert2DomainDim(shape, dc);
  		
  		return shape;
   	}
  	
  	// used for inner arrangement tooltip
  	public Shape getDomainShape(DomainFamily fam) {	
  		if (doms2shapes.get(fam.getId()) == null) 
   			setDomainShape(fam, getNextShapeIndex());
   		return getShape(doms2shapes.get(fam.getId()));
   	}
	
	/**
	 * Return the number of associated shapes.
	 * 
	 * @return
	 * 		number of associated shapes.
	 */
	public int getNumDomainShapes() {
		return doms2shapes.size();
	}
	
	/**
	 * Maps a new shape to the specified DomainComponents family. 
	 * A repaint event is triggered afterwards.
	 * 
	 * @param dc
	 * 		the DomainComponent which DomainFamily has to be associated with the specified shape.
	 * @param shapeIndex
	 * 		the new shape for the specified DomainFamily
	 */
	public void setDomainShape(DomainComponent dc, int shapeIndex) {
		doms2shapes.put(dc.getDomain().getFamily().getId(), shapeIndex);
		visualChange();
	}
	
	/**
	 * Sets a new shape for a DomainFamily without triggering
	 * a repaint. This method can for instance be used during the
	 * project import process.
	 * 
	 * @param fam
	 * 		DomainFamily to be associated with the specified shape.
	 * @param shapeIndex
	 * 		the new shape for the specified DomainFamily
	 */
	public void setDomainShape(DomainFamily fam, int shapeIndex) {
		doms2shapes.put(fam.getId(), shapeIndex);
	}
	
	/**
	 * Returns the shape id for a specified domain family.
	 * 
	 * @param fam
	 * 		the domain family which shape index is requested
	 * @return
	 * 		the shape index for the specified domain family
	 */
	public int getShapeID(DomainFamily fam) {
		if (doms2shapes.get(fam.getId()) == null) {
  			int shapeIndex = getNextShapeIndex();
  			doms2shapes.put(fam.getId(), shapeIndex);
  			return shapeIndex;
		} 
		else
			return doms2shapes.get(fam.getId());
	}

}
