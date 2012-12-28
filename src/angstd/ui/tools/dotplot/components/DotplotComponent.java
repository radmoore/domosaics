package angstd.ui.tools.dotplot.components;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import angstd.model.dotplot.Dotplot;
import angstd.ui.views.view.components.AbstractViewComponent;



/**
 * DotplotComponent is the graphical representation of a dotplot 
 * data structure. A grayscale image produced out of the dotplot is used
 * to render the dotplot which is stored in this view component
 * as well and can be refreshed using the method refresh(). (e.g.
 * if slider values and therefore the dotplot changes 
 * 
 * @author Andreas Held
 *
 */
public class DotplotComponent extends AbstractViewComponent {

	/** the backend dotplot data structure */
	protected Dotplot dotplot;
	
	/** holds the graphic result of the dotplot */
	protected GreyScaleImg grayImg = null;	
	
	/**
	 * Constructor for a new DotplotComponent setting the backend 
	 * dotplot data structure.
	 * 
	 * @param plot
	 * 		backend dotplot data structure
	 */
    public DotplotComponent(Dotplot plot) {
    	this.dotplot = plot;
    	this.grayImg = new GreyScaleImg();
	}
    
	/**
	 * Returns the image used to render the dotplot 
	 * 
	 * @return
	 * 		image used to render the dotplot 
	 */
	public BufferedImage getImg() {
		return grayImg.get();
	}
	
	/**
	 * Returns the backend dotplot data structure 
	 * 
	 * @return
	 * 		backend dotplot data structure 
	 */
    public Dotplot getDotplot() {
    	return dotplot;
    }
    
	/**
	 * @see AbstractViewComponent
	 */
	public Shape getDisplayedShape() {
		return null;
	}

	/**
	 * Method invoked when the dotplot changes so that a new grayscale
	 * image is created depending on the dotplot.
	 */
	public void refresh() {
		grayImg.create(dotplot);	
	}
}
