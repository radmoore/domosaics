package domosaics.ui.views.domaintreeview.renderer.additional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;
import java.util.List;

import domosaics.model.domainevent.DomainEventI;
import domosaics.model.tree.TreeEdgeI;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.components.DomainEventComponent;
import domosaics.ui.views.domainview.components.shapes.DoMosaicsPolygon;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.renderer.Renderer;




public class InDelRenderer implements Renderer{

	private DomainTreeViewI view;
	
	public InDelRenderer(DomainTreeViewI view) {
		this.view = view;
	}

	public void render(Graphics2D g) {
		if(!view.getDomainTreeLayoutManager().isShowInDels())
			return;
		
		if(view.getCSAInSubtreeManager().isActive())
			return;
		
		// traverse the domain tree and render the insertion deletions at the edges
		Iterator<TreeNodeI> iter = view.getDomTree().getNodeIterator();
		while(iter.hasNext()) {
			TreeNodeI dtn = iter.next();
			
			for (int i = 0; i < dtn.childCount(); i++) {
				TreeEdgeI edge = dtn.getEdgeToChild(i);
				if (edge.hasDomainEvent())
					renderDomainEvent(edge, view.getTreeComponentManager().getComponent((TreeNodeI)edge.getSource()), view.getTreeComponentManager().getComponent((TreeNodeI)edge.getTarget()), g);
			}
		}	
	}

	public void renderDomainEvent(TreeEdgeI edge, NodeComponent p, NodeComponent c, Graphics2D g) {	
		// save last color and stroke settings
		Color oldColor = g.getColor();         
		Paint oldPaint = g.getPaint();
		
		// number of insertion deletions 
		// TODO move this into layouting with relative coordinates?
//		int x1 = p.getX();
//		int y1 = p.getY();
//		int x2 = c.getX();
//		int y2 = c.getY();
//		
//		int maxWidth = Math.abs(x1-x2)-4;
//		int maxHeight = Math.abs(y1-y2)-1;
//		
//		int width = AbstractDomainLegendLayout.PROPSIZEWIDTH/2;
//		int height = 12; // g.setFont(Font.decode("Arial-12")); //maxHeight-2;
//		
//		int y = y2 -height;
		

		List<DomainEventI> domEvents = edge.getDomainEvents();
		for (int i = 0; i < domEvents.size(); i++) {
			
//			int x = x1+3+(width+2)*i;
			
			DomainEventI evt = domEvents.get(i);
			DomainEventComponent dec = view.getDomainEventComponentManager().getComponent(evt);
			
			
			Color color = view.getDomainColorManager().getDomainColor(view.getArrangementComponentManager().getDomainComponent(evt.getDomain()));
			
			Shape shape; 
			
			if (view.getDomainLayoutManager().isShowShapes()) 
				shape = view.getDomainShapeManager().getUnsetShape(view.getArrangementComponentManager().getDomainComponent(evt.getDomain()));
			else
				shape = new RoundRectangle2D.Double();
		
			if (shape instanceof RoundRectangle2D.Double) 
				((RoundRectangle2D.Double) shape).setRoundRect(dec.getX(), dec.getY(), dec.getWidth(), dec.getHeight(), 20, 20); 
			else if (shape instanceof Rectangle2D.Double) 
				((Rectangle2D.Double) shape).setRect(dec.getX(), dec.getY(), dec.getWidth(), dec.getHeight()); 
				else if (shape instanceof DoMosaicsPolygon)
					((DoMosaicsPolygon) shape).setPolygon(dec.getX(), dec.getY(), dec.getWidth(), dec.getHeight()); 
		
			g.setColor(color);
			float fac = 0.5f;
			Color fadeColor = new Color ((int) (color.getRed()*fac), (int) (color.getGreen()*fac), (int) (color.getBlue()*fac));
			g.setPaint(new GradientPaint(dec.getX()+dec.getWidth()/2, dec.getY(), color, dec.getX()+dec.getWidth()/2, dec.getY()+dec.getHeight(), fadeColor, false));
			
			g.fill(shape);
			g.setColor(Color.black);
			g.draw(shape);
			
			if (evt.isDeletion()) {
				Stroke oldStroke = g.getStroke();
				g.setColor(Color.white);
				g.setStroke(new BasicStroke(1f));
				g.drawLine(dec.getX(), dec.getY(), dec.getX()+dec.getWidth(), dec.getY()+dec.getHeight());
				g.drawLine(dec.getX()+dec.getWidth(), dec.getY(), dec.getX(), dec.getY()+dec.getHeight());
				g.setStroke(oldStroke);
			}
		}
		g.setColor(oldColor);
		g.setPaint(oldPaint);
	}
	
}
