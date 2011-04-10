package angstd.ui.views.domainview.components;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import angstd.model.arrangement.Domain;
import angstd.model.configuration.Configuration;
import angstd.ui.views.view.components.AbstractViewComponent;

/**
 * The DomainComponent is the graphical representation for a 
 * backend data model, the {@link Domain} which is
 * rendered and layouted by the DomainView.
 * <p>
 * Besides its backend component it knows whether or not its visible or not.
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainComponent extends AbstractViewComponent {

	/** the backend data object */
	protected Domain dom = null;
	
	/** flag indicating whether or not the arrangement is visible */
	protected boolean visible = true;

	/**
	 * Constructor for a new DomainComponent representing a specified 
	 * backend data domain. 
	 * 
	 * @param dom
	 * 		the backend data domain
	 * 		
	 */
    public DomainComponent(Domain dom) {
        if(dom == null){
        	throw new RuntimeException("Can not create DomainComponent without backend domain !");
        }
        this.dom = dom;      
	}
    
    /**
     * Return whether or not the domain component is visible in 
     * the view.
     * 
     * @return
     * 		 whether or not the domain component is visible
     */
    public boolean isVisible() {
    	return visible;
    }
    
    /**
     * Sets the visibility status for the domain component.
     * 
     * @param visible
     * 		the new visibility status
     */
    public void setVisible(boolean status) {
    	this.visible = status;
    }
    
    
    /**
     * Return the label for this domain.
     * 
     * @return
     * 		label for the domain
     */
    public String getLabel(){
    	String label;
    	if(Configuration.isIdPreferedToAcc())
    	{
    		label = getDomain().getID();
    		if (label == null) {
        		label = getDomain().getAcc();
        	}
    	}
    	else
    		label = getDomain().getAcc();	
    	
    	
		if(label != null && !label.trim().isEmpty()) 
			return label;
		return null;
	}
    
    /**
	 * Return the backend data for this domain component 
	 * 
	 * @return
	 * 		backend Domain for this component
	 */
    public Domain getDomain() {
		return dom;
	}
    
	/**
	 * Returns the top left corner of the domain bounds
	 * 
	 * @return
	 * 		 top left corner of the domain bounds
	 */
    public int getTopLeft() {
    	return (int) (getY() - (getHeight() / 2.0));
    }
    
    /**
	 * @see AbstractViewComponent
	 */
	public Shape getDisplayedShape() {
		return new RoundRectangle2D.Double(
				getX(), 
				getTopLeft(),
				getWidth(), 
				getHeight(), 
				20, 20);
	}
	
	
	
}
