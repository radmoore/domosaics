package angstd.ui.views.domainview.components;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.manager.DomainArrangementComponentManager;
import angstd.ui.views.view.components.AbstractViewComponent;

/**
 * The ArrangementComponent is the graphical representation for a 
 * backend data model, the {@link DomainArrangement} which is
 * rendered and layouted by the DomainView.
 * <p>
 * Besides its backend component it knows whether or not its visible or not.
 * 
 * 
 * @author Andreas Held
 *
 */
public class ArrangementComponent extends AbstractViewComponent{
	
	/**
	 * the component manager which manages this view component.
	 */
	protected DomainArrangementComponentManager manager;
	
	/** the backend data object */
	protected DomainArrangement da = null;

	/** flag indicating whether or not the arrangement is visible */
	protected boolean visible = true;
	
	private boolean renderID = true;
	
	/**
	 * Constructor for a new ArrangementComponent representing a 
	 * specified backend data arrangement. 
	 * 
	 * @param da
	 * 		the backend data arrangement
	 * 		
	 */
    public ArrangementComponent(DomainArrangement da, DomainArrangementComponentManager manager) {
        if(da == null)
        	throw new RuntimeException("Cannot create DomainComponent without backend domain !");
        this.manager = manager;
        this.da = da;    
	}
    
    /**
     * Returns the domain components of the arrangement
     * 
     * @return
     * 		domain components of the arrangement
     */
    public Iterator<DomainComponent> getDomainComponents() {
    	return manager.getDomains(this).iterator();
    }
    
    /**
     * Gets a DomainComponent which is part of this
     * ArrangementComponent given the backend domain
     * @param dom
     * @return
     */
    public DomainComponent getDomain(Domain dom) {
    	return manager.getDomainComponent(dom);
    }
    
    /**
     * Return whether or not the arrangement component is visible in 
     * the view.
     * 
     * @return
     * 		 whether or not the arrangement component is visible
     */
    public boolean isVisible() {
    	return visible;
    }
  
    
    /**
     * Sets the visibility status for the arrangement component.
     * 
     * @param visible
     * 		the new visibility status
     */
    public void setVisible(boolean visible) {
    	this.visible = visible;
    }
    
    /**
     * Return the label for this arrangement.
     * 
     * @return
     * 		label for the arrangement
     */
	public String getLabel(){
		String label = getDomainArrangement().getName();
		if(label != null && !label.trim().isEmpty()) 
			return label;
		return null;
	}
	
	/**
	 * Return the backend data for this arrangement component
	 * 
	 * @return
	 * 		backend DomainArrangement for this component
	 */
	public DomainArrangement getDomainArrangement() {
		return da;
	}
	
	/**
	 * Returns the top left corner of the arrangement bounds
	 * 
	 * @return
	 * 		 top left corner of the arrangement bounds
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
