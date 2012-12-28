package domosaics.ui.views.domaintreeview.renderer.additional;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.SwingUtilities;

import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.components.DomainEventComponent;
import domosaics.ui.views.view.renderer.Renderer;


public class DomainEventTooltipRenderer implements Renderer {
	    
		protected static final Color DEFAULT_BACKGROUND = new Color(225,225,225,200);
		
	    /** Default row height */
	    protected float vertical_row_height = 10;  
	    
	    private DomainTreeViewI view;
	    private DomainEventComponent tooltipDomEvt = null;
	    private Dimension tooltipDim = null;
	    
	    public DomainEventTooltipRenderer(DomainTreeViewI view) {
	    	this.view = view;
	    }
	    
	    public void setTooltipDomainEvent(DomainEventComponent dec) {
	    	this.tooltipDomEvt = dec;
	    }
	    
	    public void render(Graphics2D g2d) {
//	    	int cursor_width = 8;
	    	
	    	// get mouse over component
	    	DomainEventComponent dec = view.getDomainEventSelectionManager().getMouseOverComp();
	        if (dec == null) 
	        	return;
	        
	        if (tooltipDomEvt != dec)
	        	tooltipDim = this.getPrefferedSize(g2d, dec);
	        
	        if(tooltipDim.height == 0 && tooltipDim.width == 0) 
	        	return;
	    	
	        Rectangle p = view.getViewComponent().getVisibleRect();
	               
	        Font oldFont = g2d.getFont();
	        Stroke oldStroke = g2d.getStroke();
	        Shape oldClip = g2d.getClip();
	        
	        // set font and stroke
	        g2d.setFont(g2d.getFont().deriveFont(12.0f));        

	        // get component of the position 
	        Point displayPoint = new Point((int) (dec.getBounds().getCenterX()), (int) (dec.getBounds().getY()+dec.getBounds().getHeight()/2+2));
	      
	        // check whether or not the tool tip has to be drawn to the right or left of the curser
	        int rightSpace = (p.x + p.width) - displayPoint.x;
	        int downSpace = (p.y + p.height) - displayPoint.y;
	        int x; // = displayPoint.x;
	        int y; // = displayPoint.y;
	        
	        // calculate y coordinate
	        if (downSpace > tooltipDim.height)
	        	y = (int) (displayPoint.y+(tooltipDim.height / 2.0));
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
	       
	        if(dec.getLabel() != null){
	            elements++;
	            g2d.setFont(font.deriveFont(Font.BOLD));
	            g2d.drawString(dec.getLabel(),b.x+2,2+b.y+vertical_row_height*elements-4);
	            g2d.setFont(font);
	        }
//	        elements++;
//	        g2d.drawString("From-To: "+dc.getDomain().getFrom()+" - "+dc.getDomain().getTo(),b.x+2,2+b.y+vertical_row_height*elements-4);
//	        
//	        elements++;
//	        g2d.drawString("E-Value: "+dc.getDomain().getEvalue(),b.x+2,2+b.y+vertical_row_height*elements-4);
	        
	        // reset settings
	        g2d.setFont(oldFont);
	        g2d.setStroke(oldStroke);
	        g2d.setClip(oldClip);
	    }

	    /**
	     * Returns the size of the label
	     * 
	     * @param g2d
	     * @return
	     */
	    public Dimension getPrefferedSize(Graphics2D g2d, DomainEventComponent dec){
	        int width = 0;
	        vertical_row_height = g2d.getFont().getSize2D() + 4;
	        int elements = 0;
	        
	        int labelWidth = 0;
	        if(dec.getLabel() != null){
	        	Font f = new Font("Arial", 1, 12);
	        	labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(f), dec.getLabel());
	        	//labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), dec.getLabel());
	            width = labelWidth > width ? labelWidth : width;
	            elements++;
	        }   
	        
//	        String fromTo = "From-To: "+dc.getDomain().getFrom()+" - "+dc.getDomain().getTo();
//	        labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), fromTo);
//	        width = labelWidth > width ? labelWidth : width;
//	        elements++;
//	        
//	        if (dc.getDomain().getEvalue() != Double.POSITIVE_INFINITY) {
//	        	String eval = "E-Value: "+dc.getDomain().getEvalue();
//	            labelWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), eval);
//	            width = labelWidth > width ? labelWidth : width;
//	            elements++;
//	        }
	        	
	        int height = (int)(elements * vertical_row_height);
	        return new Dimension(width+4,height+4);
	    }    
}
