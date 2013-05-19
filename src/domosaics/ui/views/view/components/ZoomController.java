package domosaics.ui.views.view.components;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import domosaics.model.configuration.Configuration;
import domosaics.ui.views.view.AbstractView;
import domosaics.ui.views.view.View;

/**
 * The ZoomController controls the zoom settings for a {@link AbstractView}.
 * <p>
 * The zoom is represented by an AffineTransform object which must be 
 * applied to the active graphical context in a rendering method.
 * See {@link AbstractView#render(Graphics)} for details.
 * <p>
 * The AffineTransforms scale factor is calculated when the mouse wheel
 * is scrolled and its translation is calculated if the mouse is dragged around.
 * <p>
 * This class additionally provides methods to enable the zoom mode
 * for a view by using toggleZoomMode().
 * <p>
 * if the view switches into zoom mode all actions are disabled except
 * those actions which implement the empty interface {@link ZoomCompatible}.
 * 
 * @author Andreas Held
 *
 */
public class ZoomController extends MouseAdapter {
	
	/** the view controlled by this ZoomController */
	protected View view;
	
	/** current scale factor */
	protected double scale = 1.0;
	
	/** flag indicating that the pan has to be dragged around */
	protected boolean dragging;
	
	/** drag point of last drag calculation */
	protected Point oldDragPoint;
	
	/** the resulting transformation after zoom calculation which has to be used on the graphics object rendering the view */
	protected AffineTransform zoom;
	
	/** flag indicating whether or not the view is in zoom mode */
	protected boolean isZooming = false;
	
	
	/**
	 * Constructor for the ZoomController
	 * 
	 * @param view
	 * 		the view being controlled by this Controller
	 */
	public ZoomController (View view) {
		this.view = view;
	}
	
	/* ********************************************************* *
	 * 			Methods controlling the ZoomMode				 *
	 * ********************************************************* */
	
	/**
	 * Toggles the zoom mode feature for the controlled view. 
	 * Therefore switches the zoom mode between on and off.
	 */
	public void toggleZoomMode() {
		isZooming = !isZooming;
		
		// work with the view
		zoom = new AffineTransform();
		
		// if the mode was changed to zoom mode
		if (isZooming) {
			/*
			 *  reset view width and height to zero results in
			 *  disabling the scrollPane sliders. This doesn't effect
			 *  the work flow because the doLayout() isn't called anyway 
			 *  during the zoom mode.
			 */
			view.setNewViewWidth(0);
			view.setNewViewHeight(0);
			
			zoom = new AffineTransform();
		} else {
			view.doLayout();
		}
		
		// automatically assign the correct mouse listeners
		view.registerMouseListeners();
		
		// repaint the view
		SwingUtilities.invokeLater(new Runnable() {						
			public void run() {	
				view.getParentPane().repaint();
			}
		});
	}
	
	/**
	 * Return whether or not the view is within zoom mode
	 * 
	 * @return
	 * 		whether or not the view is within zoom mode
	 */
	public boolean isZoomMode() {
		return isZooming;
	}
	
	/**
	 * Return the calculated zoom transformation. This Transformation
	 * has to be applied to the actual graphics context to 
	 * render the zoom.
	 * 
	 * @return
	 * 		zoom transformation needed to render the zoom action
	 */
	public AffineTransform getZoomTransform() {
		return zoom;
	}
	
	/* ********************************************************* *
	 * 			Methods calculating the zoom transformation		 *
	 * ********************************************************* */
	
	/**
	 * Calculates and applies a scale on the AffineTransform object 
	 * representing the zoom when the mouse wheel is used.
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!isZooming)
			return;
		
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			Point2D p1 = e.getPoint();
			Point2D p2 = null;
			try {
				p2 = zoom.inverseTransform(p1, null);
			} 
			catch (NoninvertibleTransformException ex) {
				if (Configuration.getReportExceptionsMode(true))
					Configuration.getInstance().getExceptionComunicator().reportBug(ex);
				else {			
					Configuration.getLogger().debug(ex.toString());
					Configuration.getLogger().debug("Zoom calculation failed");
				}
				return;
			}

			scale -= (0.1 * e.getWheelRotation());
			scale = Math.max(0.01, scale);

			zoom.setToIdentity();
			zoom.translate(p1.getX(), p1.getY());
			zoom.scale(scale, scale);
			zoom.translate(-p2.getX(), -p2.getY());

			view.getParentPane().repaint();
		}
	}

	/**
	 * Calculates the translation of AffineTransform object
	 * representing the zoom when mouse is dragged. 
	 */
	public void mouseDragged(MouseEvent e) {
		int xDist = oldDragPoint.x - e.getPoint().x;
		int yDist = oldDragPoint.y - e.getPoint().y;
		oldDragPoint = e.getPoint();
		
		zoom.translate(-xDist*4, -yDist*4); 
		view.getParentPane().repaint();
	}
	
	/**
	 * Indicates that the dragging started
	 */
	public void mousePressed(MouseEvent e) {
		dragging = true;
		oldDragPoint = e.getPoint();
	}
	
	/** 
	 * Indicates that the dragging was stopped
	 */
	public void mouseReleased(MouseEvent e) {
		dragging = false;
		oldDragPoint = null;
	}
	
}
