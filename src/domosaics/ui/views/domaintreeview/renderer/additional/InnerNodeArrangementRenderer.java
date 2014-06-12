package domosaics.ui.views.domaintreeview.renderer.additional;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.components.shapes.DoMosaicsShapeIcon;
import domosaics.ui.views.domainview.layout.UnproportionalLayout;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.renderer.Renderer;




public class InnerNodeArrangementRenderer implements Renderer{

	protected static final Color DEFAULT_BACKGROUND = new Color(225,225,225,200);
	private DomainTreeViewI view;
	
	public InnerNodeArrangementRenderer(DomainTreeViewI view) {
		this.view = view;
	}

	@Override
	public void render(Graphics2D g) {
		// check if a node is in mouse over mode
		NodeComponent nc = view.getTreeSelectionManager().getMouseOverComp();
		if (nc == null)
			return;
		
		// check if an arrangement is stored for the inner node
		TreeNodeI node = nc.getNode();
		DomainArrangement da = view.getInnerNodeArrangementManager().getDA(node);
		if (node.isLeaf() || da == null)
			return;
		
		// calculate needed size for the tooltip
		Dimension tooltipDim = UnproportionalLayout.getPreferredSize(da);
//      if (tooltipDomEvt != dec)
//    	tooltipDim = this.getPrefferedSize(g2d, dec);
		if(tooltipDim.height == 0 && tooltipDim.width == 0) 
        	return;
    	

		Rectangle p = view.getViewComponent().getVisibleRect();
         
	    Font oldFont = g.getFont();
	    Stroke oldStroke = g.getStroke();
	    Shape oldClip = g.getClip();
	        
	    // set font and stroke
	    g.setFont(g.getFont().deriveFont(12.0f));        

	   // get component of the position 
	   Point displayPoint = nc.getLocation();
	      
	   // check whether or not the tool tip has to be drawn to the right or left of the cursor
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
	    g.setClip(x-5, y-5, tooltipDim.width+10, tooltipDim.height+10); 
	        
	    //paint tool tip component
	    g.setColor(DEFAULT_BACKGROUND);
	        
	    Rectangle b = g.getClipBounds();
	    g.fill(b); 
	    g.setColor(Color.black);
	    g.drawRect(b.x,b.y,b.width -1, b.height -1);
	    
	    for (int i = 0; i < da.getDomains().size(); i++) {
	    	Domain dom = da.getDomains().get(i);
	    	Shape shape = view.getDomainShapeManager().getDomainShape(dom.getFamily());
	    	
	    	Color color = view.getDomainColorManager().getDomainColor(dom.getFamily());
	    	DoMosaicsShapeIcon icon = new DoMosaicsShapeIcon(shape, color, false);
	    	
//	    	x = 10;
//	    	y = 10;
	    	icon.paintIcon(null, g, x+i*(UnproportionalLayout.UNPROPSIZE+UnproportionalLayout.UNPROPDISTANCE), y);
	    }
	   
	        
//	       int elements = 0;        
//	        Font font = g2d.getFont();
//	       
//	        if(dec.getLabel() != null){
//	            elements++;
//	            g2d.setFont(font.deriveFont(Font.BOLD));
//	            g2d.drawString(dec.getLabel(),b.x+2,2+b.y+vertical_row_height*elements-4);
//	            g2d.setFont(font);
//	        }
////	        elements++;
////	        g2d.drawString("From-To: "+dc.getDomain().getFrom()+" - "+dc.getDomain().getTo(),b.x+2,2+b.y+vertical_row_height*elements-4);
////	        
////	        elements++;
////	        g2d.drawString("E-Value: "+dc.getDomain().getEvalue(),b.x+2,2+b.y+vertical_row_height*elements-4);
	        
        // reset settings
        g.setFont(oldFont);
        g.setStroke(oldStroke);
        g.setClip(oldClip);
    }

}
