package angstd.ui.tools.domaingraph.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import prefuse.data.Node;
import prefuse.data.Table;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainFamily;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.shapes.AngstdShapeIcon;
import angstd.ui.views.domainview.components.shapes.DomainShapes;

/**
 * DomainGraph represents the backend data structure for a domain
 * co occurrence graph. Those domain graphs should be created using the 
 * {@link DomainGraphFactory}.
 * 
 * @author Andreas Held
 *
 */
public class DomainGraph extends prefuse.data.Graph {

	/** array of shapes to display the domains */
	protected Shape[] shapes;
	
	/** backend domain view */
	protected DomainViewI domView;
	
	/** maximum number of connections to a domain */
	protected int maxConnections = 0;
	
	
	/**
	 * Constructor for a new co occurrence domain graph.
	 * 
	 * @param domView
	 * 		the backend domain view.
	 * @param nodes
	 * 		the node shema for the graph
	 * @param edges
	 * 		the edge shema for the graph
	 */
	public DomainGraph(DomainViewI domView, Table nodes, Table edges) {
		super(nodes, edges, true);
		
		this.domView = domView;
		
		// init the domain shapes
		DomainShapes[] domShapes = DomainShapes.values();
		shapes = new Shape[domShapes.length];
		for (int i = 0; i < domShapes.length; i++) 
			shapes[i] = domShapes[i].getShape();
	}
	
	/**
	 * Adds a node to the graph
	 * 
	 * @param domain
	 * 		the domain which should be added as node to the graph
	 * @return
	 * 		the created graph node.
	 */
	public Node addNode(Domain domain) {
    	Node n = this.addNode(); 
    	n.setString("name", domain.getID());
		n.set("image", getImage4Domain(domain.getFamily()));
		n.setInt("connectivity", 0);
		n.setInt("status", 0);
		return n;
	}
	
	/**
	 * increases the connectivity of a domain
	 * 
	 * @param n
	 * 		the node which connectivity should be increased
	 */
	public void incConnectivity(Node n) {
		int cons = n.getInt("connectivity")+1;
		n.setInt("connectivity", cons);
		if(cons > 0)
			n.setInt("status", 1);
		if (cons > maxConnections)
			maxConnections = cons;
	}
	
	/**
	 * Returns the maximum number of connection to a domain.
	 * This can be used to set the maximum value for the thrshold slider.
	 * 
	 * @return
	 * 		maximum number of connection to a domain.
	 */
	public int getMaxConnections() {
		return maxConnections;
	}
	
	/**
	 * Returns a node with the specified index
	 */
    public Node getNode(int n) {
        return (Node)m_nodeTuples.getTuple(n);
    }
		
    /**
     * Helper method to get an shape and color image for a domain family.
     * This image can then be displayed within the prefuse graph as node.
     * 
     * @param domFam
     * 		the domain family which shape image is requested
     * @return
     * 		shape image of the querried domain family
     */
	private Image getImage4Domain(DomainFamily domFam) {
		int shapeID = domView.getDomainShapeManager().getShapeID(domFam);
		AngstdShapeIcon icon = new AngstdShapeIcon(shapes[shapeID], domView.getDomainColorManager().getDomainColor(domFam), false);
		return icon2image(icon);
	}
	
	/**
	 * Helper method to convert shape icons to images which can be used
	 * within the prefuse graph.
	 * 
	 * @param icon
	 * 		the icon to convert
	 * @return
	 * 		the image of an icon
	 */
	private Image icon2image(AngstdShapeIcon icon) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(w, h);
        Graphics2D g = image.createGraphics();
		
        g.setColor(Color.white);
		g.fill(new Rectangle(0, 0, icon.getIconWidth(), icon.getIconHeight()));
        
		icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return image;
    } 
}
