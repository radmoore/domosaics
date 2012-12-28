package domosaics.ui.tools.domainmatrix.components;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.view.components.AbstractViewComponent;




/**
 * DomainMatrixEntry is the type of component which gets layouted and
 * rendered within the DomainMatrixView. It is based on a
 * backend pair consisting of a label and a domain component. Thats because
 * MatrixEntrys can be labels (would be the case for the
 * header fields) or domain components (all other). 
 * Therefore each matrix entry can be represented by a {@link Pair} of 
 * the label and domain component where one of them is null, e.g. if
 * the domain component is null, it must be a header label. Sounds like
 * a dirty job to me. =)
 * 
 * @author Andreas Held
 *
 */
public class DomainMatrixEntry extends AbstractViewComponent {

	/** the backend pair */
	protected Pair pair;

	
	/**
	 * Constructor for a new DomainMatrixEntry setting the backend 
	 * pair.
	 * 
	 * @param dc
	 * 		backend domain component
	 */
    public DomainMatrixEntry(Pair pair) {
        if(pair == null)
        	throw new RuntimeException("Can not create matrix entry without value!");
      
        this.pair = pair;     
	}
    
    /**
     * Return the header label
     * 
     * @return
     * 		header label
     */
	public String getLabel(){
		return pair.label;
	}
    
    /**
     * Returns the domain component rendered within a matrix entry
     * 
     * @return
     * 		domain component
     */
    public DomainComponent getDomainComponent() {
    	return pair.dom;
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
