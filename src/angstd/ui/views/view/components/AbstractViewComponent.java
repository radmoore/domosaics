package angstd.ui.views.view.components;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import angstd.ui.views.domainview.components.DomainComponent;

/**
 * The basic implementation of the {@link ViewComponent} interface.
 * <p>
 * All interface methods are implemented except the getDisplayedShape()
 * method which various depending on the view component type.
 * For instance {@link DomainComponent}s can have different shapes, 
 * whereas tree nodes are all the same and unlike the domain shapes.
 * <p>
 * For further details look into the interface {@link ViewComponent}.
 * 
 * @author Andreas Held
 *
 */
public abstract class AbstractViewComponent implements ViewComponent{

	/**
     * Bounds of this component. X and Y represent the anchor point. <br>
     * The anchor point for trees is where outgoing edges start and
     * NOT the upper left corner!. <br>
     * The anchor point for DomainArrangements and Domains are also 
     * centered y values.
     */
	protected Rectangle bounds;
   
	/**
	 * Relative bounds of the component, e.g. used during the layout 
	 * process.Changes are made by calling 
	 * {@link #getRelativeBounds()} .x, .y and so on.
	 */
	protected Rectangle2D.Double relativeBounds;
	
	/**
	 * The bounding shape for this component, this can be used to check
	 * whether or not the component intersects with the clipping area.
	 */
    protected Shape boundingShape;
    
    
    /**
     * Basic constructor for a new ViewComponent initializing 
     * the class variables.
     */
    public AbstractViewComponent() {
    	bounds = new Rectangle();
    	relativeBounds = new Rectangle2D.Double();
    	boundingShape = new Rectangle();
    }
    
    /* ************************************************************** *
     * 						Bound methods							  *
     * ************************************************************** */
    
    /**
     * @see ViewComponent
     */
	public void setBounds(int x, int y, int width, int height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
		setBoundingShape(bounds);
	}
	
    /**
     * @see ViewComponent
     */
	public int getX() {
	    return bounds.x;
	}
	
    /**
     * @see ViewComponent
     */
	public int getY() {
	    return bounds.y;
	}
	
    /**
     * @see ViewComponent
     */
	public int getWidth() {
	    return bounds.width;
	}
	
    /**
     * @see ViewComponent
     */
	public int getHeight() {
	    return bounds.height;
	}

    /**
     * @see ViewComponent
     */
	public Point getLocation() {
	    return new Point(bounds.x, bounds.y);
	}
	
    /**
     * @see ViewComponent
     */
	public Rectangle getBounds() {
		return bounds;
	}    
	
    /**
     * @see ViewComponent
     */
	public Rectangle2D.Double getRelativeBounds() {
		return relativeBounds;
	}
	
    /* ************************************************************** *
     * 						Shape methods							  *
     * ************************************************************** */
	
	/**
	 * Sets the bounding shape of this component. 
	 * This method is automatically triggered by 
	 * setBounds.
	 * 
	 * @param boundingShape 
	 * 		the new bounding shape of this component
	 */
	protected void setBoundingShape(Shape boundingShape) {
		this.boundingShape = boundingShape;
	}
	
	/**
     * @see ViewComponent
     */
	public Shape getBoundingShape() {
		return boundingShape;
	}
	
	/**
     * @see ViewComponent
     */
	public abstract Shape getDisplayedShape();
}
