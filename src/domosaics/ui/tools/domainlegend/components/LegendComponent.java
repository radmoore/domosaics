package domosaics.ui.tools.domainlegend.components;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.view.components.AbstractViewComponent;


/**
 * LegendComponent is the type of component which gets layouted and
 * rendered within the DomainLegendView. It is based on a
 * backend domain component.
 * 
 * @author Andreas Held
 *
 */
public class LegendComponent extends AbstractViewComponent {
	
	/** the backend domain component */
	protected DomainComponent dc = null;
	
	/** the frequency on how often the domain occurred within the dataset */
	protected int frequency = 1;

	
	/**
	 * Constructor for a new LegendComponent setting the backend 
	 * domain component.
	 * 
	 * @param dc
	 * 		backend domain component
	 */
    public LegendComponent(DomainComponent dc) {
        if(dc == null)
        	throw new RuntimeException("Can not create LegendComponent without backend DomainComponent !");
        this.dc = dc;      
	}
    
    /**
     * Returns the backend domain component
     * 
     * @return
     * 		backend domain component
     */
    public DomainComponent getDomainComponent() {
    	return dc;
    }
    
    /**
     * Sets the frequency on how often the domain occurred within 
     * the dataset
     * 
     * @param freq
     * 		number of occurrences within the dataset
     */
    public void setFrequency(int freq) {
    	this.frequency = freq;
    }
    
    /**
     * Returns the frequency on how often the domain occurred within 
     * the dataset
     * 
     * @return
     * 		frequency on how often the domain occurred within the dataset
     */
    public int getFrequency() {
    	return frequency;
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
    
	/**
     * Return the label for this domain.
     * 
     * @return
     * 		label for the domain
     */
	public String getLabel(){
		String label = dc.getDomain().getID();
		if(label != null && !label.trim().isEmpty()) 
			return label;
		return null;
	}
	
}
