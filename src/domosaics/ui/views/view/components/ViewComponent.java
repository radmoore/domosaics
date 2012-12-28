package domosaics.ui.views.view.components;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.manager.ComponentManager;


/**
 * View components represent the graphical component for 
 * backend data models rendered within a view. 
 * <p>
 * Examples for ViewComponents are {@link NodeComponent}
 * in trees and {@link ArrangementComponent} for representing
 * domain arrangements.
 * <p>
 * Each view component is in general managed by a {@link ComponentManager}
 * mapping the backend data to the graphical component.
 * <p>
 * During the layout process each ViewComponent is assigned to
 * its correct position on the view panel. Therefore various
 * methods are needed to let layouter classes set the position
 * for the view component. All those methods are defined in this 
 * interface.
 * <p>
 * The basic implementation for this interface is {@link AbstractViewComponent}
 * and view components like {@link ArrangementComponent} just extend the
 * basic implementation.
 * 
 * @author Andreas Held
 *
 */
public interface ViewComponent {

	/**
	 * Sets the new bounds for this component. This method should also 
	 * set the new bounding shape for the bounds. Look into 
	 * {@link AbstractViewComponent#setBounds(int, int, int, int)}
	 * for details.
	 * 
	 * @param x 
	 * 		the x coordinate of the anchor point.
	 * @param y 
	 * 		the y coordinate of the anchor point.
	 * @param width 
	 * 		components width
	 * @param height 
	 * 		components height
	 */
	public void setBounds(int x, int y, int width, int height);
	
	/**
	 * Returns the (anchors) x coordinate for this component. 
	 * 
	 * @return 
	 * 		the (anchors) x coordinate
	 */
	public int getX();
	
	/**
	 * Returns the (anchors) y coordinate for this component. 
	 * 
	 * @return 
	 * 		the (anchors) y coordinate
	 */
	public int getY();
	
	/**
	 * Returns the width of this component. 
	 * 
	 * @return
	 * 		components width
	 */
	public int getWidth();
	
	/**
	 * Returns the height of this component. 
	 * 
	 * @return 
	 * 		the components height
	 */
	public int getHeight();

	/**
	 * Returns the Position of the components anchor point. 
	 * 
	 * @return 
	 * 		Position of the components anchor point
	 */
	public Point getLocation();
	
	/**
	 * Returns the bounds of this component, where X,Y is the anchor 
	 * point and therefore not necessarily the upper left corner.
	 * 
	 * @return 
	 * 		Bounds of this component
	 */
	public Rectangle2D getBounds();    
	
	/**
	 * Returns the relative bounds of this component, which can be used 
	 * during the layout process.
	 * <p>
	 * Changes on the relative bounds are done by using this method. 
	 * For example node.getRelativeBounds().y = newY;
	 * 
	 * @return 
	 * 		components relative bounds
	 */
	public Rectangle2D.Double getRelativeBounds();
	
	/**
	 * Returns the bounding shape of this component. 
	 * <p>
	 * This can be used to determine if the component intersects 
	 * with the clipping area and therefore has to be drawn during 
	 * the rendering process.
	 * 
	 * @return 
	 * 		the bounding shape of this component
	 */
	public Shape getBoundingShape();
	
	/**
	 * Returns the actual displayed shape for this component, 
	 * which is used for mouse events to determine whether or not the 
	 * component was clicked on.
	 * 
	 * @return
	 * 		the currently displayed shape of this component.
	 */
	public Shape getDisplayedShape();
}
