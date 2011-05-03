package angstd.ui.views.domainview.renderer.additional;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import angstd.model.GO.GeneOntology;
import angstd.model.GO.GeneOntologyTerm;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.view.renderer.Renderer;

/**
 * Renderer a tooltip for domains on mouse over. 
 * The tooltip renderer tries to show the domain label, the sequence 
 * range within the protein and the assigned e-value during annotation.
 * <p>
 * This renderer is not compatible with the zoom mode.
 * 
 * @author Andreas Held
 *
 * TODO:
 *  * complete the CODD intergration 
 *
 */
public class DomainTooltipRenderer implements Renderer {
    
	/** tooltip background */
	protected static final Color DEFAULT_BACKGROUND = new Color(225,225,225,200);
	
    /** Default row height */
    protected float vertical_row_height = 10;  
    
    /** view showing tooltips */
    protected DomainViewI view;
    
    /** the current domain component for showing the tooltip */
    protected DomainComponent tooltipDom = null;
    
    /** current tool tip dimensions */
    protected Dimension tooltipDim = null;
    
    
    /**
     * Constructor for a new DomainTooltipRenderer.
     * 
     * @param view
     * 		the view used with the renderer
     */
    public DomainTooltipRenderer(DomainViewI view) {
    	this.view = view;
    }
    
    /**
     * @see Renderer
     */
    public void render(Graphics2D g2d) {
		// don't show tooltips in zoom mode
    	if (view.isZoomMode())
			return;
    	
    	// if no mouse over component => return
    	DomainComponent dc = view.getDomainSelectionManager().getMouseOverComp();
        if (dc == null) 
        	return;
        
		
        if (tooltipDom != dc)
        	tooltipDim = this.getPrefferedSize(g2d, dc);
        
        if(tooltipDim.height == 0 && tooltipDim.width == 0) 
        	return;
    	
        Rectangle p = view.getViewComponent().getVisibleRect();
               
        Font oldFont = g2d.getFont();
        Stroke oldStroke = g2d.getStroke();
        Shape oldClip = g2d.getClip();
        
        // set font and stroke
        g2d.setFont(g2d.getFont().deriveFont(12.0f));        

        // get component of the position 
        Point displayPoint = new Point((int) (dc.getBounds().getCenterX()), (int) (dc.getBounds().getY()+dc.getBounds().getHeight()/2+2));
      
        // check whether or not the tool tip has to be drawn to the right or left of the curser
        int rightSpace = (p.x + p.width) - displayPoint.x;
        int downSpace = (p.y + p.height) - displayPoint.y;
        int x = displayPoint.x;
        int y = displayPoint.y;
        
        // calculate y coordinate
        if (downSpace > tooltipDim.height)
        	y = displayPoint.y;
        else
        	y = (int) (displayPoint.y - (tooltipDim.height / 2.0));
   
        // calculate x coordinate
        if (rightSpace > tooltipDim.width)
        	x = displayPoint.x;
        else
        	x = displayPoint.x - tooltipDim.width;
        	
        // set clip region for the tool tip
        g2d.setClip(x, y, tooltipDim.width, tooltipDim.height); 
        
        //paint tool tip component
        g2d.setColor(DEFAULT_BACKGROUND);
        
        Rectangle b = g2d.getClipBounds();
        g2d.fill(b);
        
        g2d.setColor(Color.black);
        g2d.drawRect(b.x,b.y,b.width -1, b.height -1);
        
        int elements = 0;        
        Font font = g2d.getFont();
        if(dc.getLabel() != null){
            elements++;
            g2d.setFont(font.deriveFont(Font.BOLD));
            g2d.drawString(dc.getLabel(),b.x+2,2+b.y+vertical_row_height*elements-4);
            g2d.setFont(font);
        }
        elements++;
        g2d.drawString("From - To: "+dc.getDomain().getFrom()+" - "+dc.getDomain().getTo(),b.x+2,2+b.y+vertical_row_height*elements-4);
        
        elements++;
        String eval = "E-Value: ";
        if (dc.getDomain().getEvalue() != Double.POSITIVE_INFINITY)
        	eval = eval+dc.getDomain().getEvalue();
        else
        	eval += "not assigned";
        
        g2d.drawString(eval,b.x+2,2+b.y+vertical_row_height*elements-4);
        
        elements++;
        String domType = "Source DB: "+dc.getDomain().getFamily().getDomainType().getName();
        g2d.drawString(domType,b.x+2,2+b.y+vertical_row_height*elements-4);
        
        if (dc.getDomain().isPutative()) {
        	elements++;
            String codd = "CODD STATUS: putative";
            g2d.drawString(codd,b.x+2,2+b.y+vertical_row_height*elements-4);
        }
        
        if (dc.getDomain().getFamily().hasGoAnnotation()) {
        	
            elements++;
        	g2d.setFont(font.deriveFont(Font.BOLD));
            g2d.drawString("GO-Terms: ",b.x+2,2+b.y+vertical_row_height*elements-4);
            g2d.setFont(font);
        
            //Iterator<Gen> goTerms = dc.getDomain().getFamily().getGoTerms();
			//@SuppressWarnings("unchecked")
			//Iterator<GeneOntologyTerm> iter = dc.getDomain().getFamily().getGoTerms();
            Iterator<?> iter = dc.getDomain().getFamily().getGoTerms();
            GeneOntologyTerm term;
            while(iter.hasNext()) {
            	elements++;
            	term = (GeneOntologyTerm)iter.next();
            	String goTerm = "- "+term.getName();
            	g2d.drawString(goTerm,b.x+2,2+b.y+vertical_row_height*elements-4);

            }
            
            
        }
        
        if (view.isCompareDomainsMode()  && view.getDomainSearchOrthologsManager().getDomainScore(dc) != -1) {
        	 elements++;
             g2d.drawString("Percent identity: "+view.getDomainSearchOrthologsManager().getDomainScore(dc)+"%",b.x+2,2+b.y+vertical_row_height*elements-4);
        }
        
        // reset settings
        g2d.setFont(oldFont);
        g2d.setStroke(oldStroke);
        g2d.setClip(oldClip);
    }

    /**
     * Helper method calculating the prefered size needed to 
     * show the tooltip
     * 
     * @param g2d
     * 		graphics context
     * @param dc
     * 		domain component used to build the tooltip
     * @return
     * 		preffered size for the tooltip
     */
    public Dimension getPrefferedSize(Graphics2D g2d, DomainComponent dc){
        int width = 0;
        vertical_row_height = g2d.getFont().getSize2D() + 4;
        int elements = 0;
        
        int labelWidth = 0;
        if(dc.getLabel() != null){
        	labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), dc.getLabel());
            width = labelWidth > width ? labelWidth : width;
            elements++;
        }   
        
        String fromTo = "From - To: "+dc.getDomain().getFrom()+" - "+dc.getDomain().getTo();
        labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), fromTo);
        width = labelWidth > width ? labelWidth : width;
        elements++;
        
        String eval = "E-Value: ";
        if (dc.getDomain().getEvalue() != Double.POSITIVE_INFINITY)
        	eval = eval+dc.getDomain().getEvalue();
        else
        	eval += "not assigned";
        labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), eval);
        width = labelWidth > width ? labelWidth : width;
        elements++;
        String domType = "Source DB: "+dc.getDomain().getFamily().getDomainType().getName();
        labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), domType);
        width = labelWidth > width ? labelWidth : width;
        elements++;
        
        if (dc.getDomain().isPutative()) {
            String codd = "CODD STATUS: putative";
            labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), codd);
            width = labelWidth > width ? labelWidth : width;
            elements++;
        }
        
        if (dc.getDomain().getFamily().hasGoAnnotation()) {
            
        	elements++ ; // add the "GO terms:" text
        	
            Iterator<?> iter = dc.getDomain().getFamily().getGoTerms();
            GeneOntologyTerm term;
            while(iter.hasNext()) {
            	term = (GeneOntologyTerm)iter.next();
            	String goTerm = "- "+term.getName();
            	labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), goTerm);
            	width = labelWidth > width ? labelWidth : width;
            	elements++;
            }
            
            
        }
        
        if (view.isCompareDomainsMode() && view.getDomainSearchOrthologsManager().getDomainScore(dc) != -1) {
        	String identity = "Percent identity: "+view.getDomainSearchOrthologsManager().getDomainScore(dc)+"%";
            labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), identity);
            width = labelWidth > width ? labelWidth : width;
            elements++;
        }
        	
        int height = (int)(elements * vertical_row_height);
        return new Dimension(width+4,height+4);
    }    
}
