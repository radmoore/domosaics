package domosaics.ui.tools.domaingraph.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import domosaics.ui.tools.domaingraph.DomainGraphView;



import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.ShapeAction;
import prefuse.action.assignment.SizeAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.FruchtermanReingoldLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 * Prefuse Graph is the class which sets up the hole domain co occurrence
 * graph using prefuse.
 * <p>
 * All needed prefuse objects as well as the threshold slider are initialized.
 * <p>
 * Methods which should be triggered when menu actions are invoked
 * are also implemented in here.
 * 
 * @author Andreas Held
 *
 */
public class PrefuseGraph extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/** marker to get access to edge labels within the prefuse graph */
	private static final String EDGE_LABEL = "edgelabel";
	
	/** displaying view where the graph panel is embedded in */
	protected DomainGraphView view;
	
	/** prefuse visualization object for graph rendering */
	protected Visualization vis;
	
	/** prefuse display object for showing the graph */
	protected Display display;
	
	/** the prefuse force directed layout for graph layouting and switching forces on and off */
	protected ForceDirectedLayout fdl;
	
	/** the fruchterman reingold layout if the user doesn't want to use a force driven layout */
	protected FruchtermanReingoldLayout frl;
	
	/** the render factories for nodes to set when layout option changes */
	protected DefaultRendererFactory[] renderer;
	
	/** the actual threshold of the slider */
	protected int threshold;


	/**
	 * Constructor for a new PrefuseGraph
	 * 
	 * @param view
	 * 		displaying view where the prefuse graph is embedded in
	 */
	public PrefuseGraph(DomainGraphView view) {
		super(new BorderLayout());
		this.view = view;
		view.setPrefuseGraph(this); // make a cross link to the displaying panel
		
        // create the graph
        DomainGraph graph = new DomainGraphFactory().create(view.getDomView());
        initRenderers();

		// *set up the visualization*
		vis = new Visualization();
		vis.add("graph", graph);
        vis.setRendererFactory(renderer[GraphLayoutManager.LABEL_RENDERER]);
        
        // create decorator schema for edge labels
        final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema(); 
        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(0));
        vis.addDecorators(EDGE_LABEL, "graph.edges", DECORATOR_SCHEMA);

        // *set up ColorActions, shapes and sizes*  
        ColorActions colors = new ColorActions(view.getGraphLayoutManager());
        ShapeAction shapes = new ShapeAction("graph.nodes", Constants.SHAPE_ELLIPSE);  
        SizeAction sizes = new SizeAction("graph.nodes", 1);
        
        StrokeAction edgeStroke = new StrokeAction("graph.edges") {
        	public BasicStroke getStroke(VisualItem item) {
        		//draw edge strokes as thick as their weight
//        		int weight = item.getInt("weight");
//        		if (weight >= 8)
//        			return new BasicStroke(8);
//        		else if(weight > 0)
//        			return new BasicStroke(weight);
//        		else
        		return new BasicStroke(1);
        	}
        };
       
        ActionList draw = new ActionList();
        draw.add(shapes);
        draw.add(sizes);
        draw.add(edgeStroke);
        draw.add(colors.getNodeTextColor());
        draw.add(colors.getNodeFillColor());
        draw.add(colors.getNodeStrokeColor());
        draw.add(colors.getEdgeLineColor());
        
        ColorAction nEdgesHeads = new ColorAction("graph.edges", VisualItem.FILLCOLOR);
        nEdgesHeads.setDefaultColor(ColorLib.gray(100));
        draw.add(nEdgesHeads);
       
        // *set up layout*
        fdl = new ForceDirectedLayout("graph", createForceSimulator(), false);
        frl = new FruchtermanReingoldLayout("graph", 20);
       	
        ActionList layout = new ActionList(Activity.DEFAULT_STEP_TIME);
        layout.add(frl);
        
        ActionList edgeLabelLayout = new ActionList(Activity.INFINITY);
        edgeLabelLayout.add(new LabelLayout2(EDGE_LABEL, view));
        
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(draw);
        animate.add(edgeLabelLayout);
        animate.add(new RepaintAction());
         
        vis.putAction("layout", layout);
        vis.putAction("animate", animate);
        
        // *set up display*
        display = new Display(vis);
        display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        display.setHighQuality(true);
		display.addControlListener(new DragControl());		// drag nodes
	    display.addControlListener(new ZoomToFitControl(Control.MIDDLE_MOUSE_BUTTON)); 
	    display.addControlListener(new ZoomControl());	// zoom by right-dragging the background
	    display.addControlListener(new WheelZoomControl()); // zoom with the mouse wheel
	    display.addControlListener(new PanControl());	// pan with left-click drag on background
	    display.addControlListener(new DomainGraphControl(this));
	    display.setBackground(Color.WHITE);
        
        // add Tuple Listener for Node Selection
        createTupleListener (vis);  
       
        // create the thresholdslider 
		Box sliderBox = createThresholdSlider(graph.getMaxConnections());

		// set up the panel
        setBackground(Color.WHITE);
        add(display, BorderLayout.CENTER);
        add(sliderBox, BorderLayout.SOUTH);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension (screenSize.width, screenSize.height-100));
        
        // *set things running*
        vis.run("layout");
        vis.run("animate");       
	}
	
	/**
	 * Returns the display object showing the prefuse graph. 
	 * This is needed for the graph export as image.
	 * 
	 * @return
	 * 		display object showing the prefuse graph
	 */
	public Display getDisplay() {
		return display;
	}
    
	/**
	 * Recalculates nodes when the threshold slider value changes.
	 * The status for the node is set to 0 when the connectivity
	 * is below the actual threshold.
	 * 
	 * @param threshold
	 * 		the new threshold
	 */
    @SuppressWarnings("unchecked")
	public void recalculateNodes (int threshold) {
    	this.threshold = threshold;
    	
    	// recalculate colorstatus
    	Iterator<NodeItem> iter = vis.visibleItems("graph.nodes");
    	while (iter.hasNext()) {
    		NodeItem node = iter.next();
    		if (node.getInt("connectivity") < threshold)
    			node.setInt ("status", 0);
    		else
    			node.setInt ("status", 1);
    	}
    }
    
    /**
     * Clears the node selection
     */
	public void clearSelectedNodes() {
		vis.getFocusGroup(Visualization.SELECTED_ITEMS).clear();
	}
	
	/** 
	 * adds a node item to the node selection
	 * 
	 * @param item
	 * 		the node item to add to the selection
	 */
	public void addToSelectedItems (NodeItem item) {
		vis.getFocusGroup(Visualization.SELECTED_ITEMS).addTuple(item);
	}
	
	/**
	 * Removes a node item from the node selection
	 * 
	 * @param item
	 * 		the node item to be removed from the selection
	 */
	public void removeFromSelectedItems (NodeItem item) {
		vis.getFocusGroup(Visualization.SELECTED_ITEMS).removeTuple(item);
	}
	
	
	/* ************************************************************************ *
	 * 								Toggle Layout options						*
	 * ************************************************************************ */
	
	/**
	 * Method which must be triggered when the force layout option
	 * is changed
	 */
	public void toggleUseForceLayout() {
		ActionList layout = (ActionList) vis.getAction("layout");
		layout.cancel();
		
		if (view.getGraphLayoutManager().isUseForceLayout()) {
			layout = new ActionList(Activity.INFINITY);
			layout.add(fdl);
		} else {
			layout = new ActionList(Activity.DEFAULT_STEP_TIME);
			layout.add(frl);
		}
		vis.putAction("layout", layout);
		vis.run("layout");
	}
	
	/**
	 * Returns whether or not forces are enabled
	 * 
	 * @return
	 * 		whether or not forces are enabled
	 */
	public boolean getForceStatus() {
		return fdl.isEnabled();
	}
	    
	/**
	 * Enables or disables the using of forces within the force directed layout.
	 * 
	 * @param on
	 * 		flag indicating the new force status
	 */
	public void setForces(boolean on) {
		fdl.setEnabled(on);
	}
	
	/**
	 * Method which should be triggered when the user toggles the 
	 * use curved edge action within the menu
	 */
	public void toggleUseCurvedEdges() {
		EdgeRenderer er = null;
		if (view.getGraphLayoutManager().isUseCurvedEdges())
			er = new EdgeRenderer(Constants.EDGE_TYPE_CURVE, Constants.EDGE_ARROW_FORWARD);
		else
			er = new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD);
		
		renderer[GraphLayoutManager.CIRCULAR_RENDERER].setDefaultEdgeRenderer(er);
		renderer[GraphLayoutManager.LABEL_RENDERER].setDefaultEdgeRenderer(er);
		renderer[GraphLayoutManager.DOMAINSHAPE_RENDERER].setDefaultEdgeRenderer(er);
		
		vis.setRendererFactory(renderer[view.getGraphLayoutManager().getRendererIndex()]);
	}
	
	/**
	 * Changes the renderer for domain rendering.
	 * 
	 * @param newRenderer
	 * 		the new renderer being used to render domains
	 */
	public void setRenderer(int newRenderer) {
		switch (newRenderer) {
			case GraphLayoutManager.CIRCULAR_RENDERER: 
				vis.setRendererFactory(renderer[GraphLayoutManager.CIRCULAR_RENDERER]);
				break;
			case GraphLayoutManager.LABEL_RENDERER: 
				vis.setRendererFactory(renderer[GraphLayoutManager.LABEL_RENDERER]); 
				break;
			case GraphLayoutManager.DOMAINSHAPE_RENDERER: 
				vis.setRendererFactory(renderer[GraphLayoutManager.DOMAINSHAPE_RENDERER]);
				break;
		}
	}
	
	/* ************************************************************************ *
	 * 								GRAPH INIT									*
	 * ************************************************************************ */
	
	/**
	 * Initializes the possible renderers to render domains 
	 * within the graph.
	 */
	private void initRenderers() {
		renderer = new DefaultRendererFactory[3];
		
	       // initialize the default edge renderer
        EdgeRenderer er = new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD);
        er.setDefaultLineWidth(1);
		
		 // initialize the node renderer for rendering circular shapes
        renderer[GraphLayoutManager.CIRCULAR_RENDERER] = new DefaultRendererFactory(new ShapeRenderer(20), er);
		
        // initialize the node renderer for rendering domain labels
		LabelRenderer lr = new LabelRenderer("name");
		lr.setRoundedCorner(8, 8); // round the corners
		renderer[GraphLayoutManager.LABEL_RENDERER] = new DefaultRendererFactory(lr, er);
        
        // initialize the node renderer for rendering domain shapes
		LabelRenderer dsr = new LabelRenderer(null, "image") {
        	protected Image getImage(VisualItem item) {
        		return (Image) item.get("image");
        	}
        };
        dsr.setTextField(null);
        dsr.setVerticalAlignment(Constants.BOTTOM);
        dsr.setHorizontalPadding(0);
        dsr.setVerticalPadding(0);
        dsr.setMaxImageDimensions(100,100);
        renderer[GraphLayoutManager.DOMAINSHAPE_RENDERER] = new DefaultRendererFactory(dsr, er);
	
	
        // create edge label renderer
        renderer[GraphLayoutManager.CIRCULAR_RENDERER].add(new InGroupPredicate(EDGE_LABEL), new LabelRenderer("weight"));
        renderer[GraphLayoutManager.LABEL_RENDERER].add(new InGroupPredicate(EDGE_LABEL), new LabelRenderer("weight"));
        renderer[GraphLayoutManager.DOMAINSHAPE_RENDERER].add(new InGroupPredicate(EDGE_LABEL), new LabelRenderer("weight"));
	}
	
	/**
	 * Initializes the force simulator used within the 
	 * force directed layout.
	 * 
	 * @return
	 * 		the force simulator used within a force directed layout
	 */
	private ForceSimulator createForceSimulator() {
		// gravConstant, distance, theta
        Force nbf = new  NBodyForce(-14.7f, -1.0f, 0.899f);
        
        //DRAG_COEFF 
		Force df = new DragForce(0.007f);
		
		// DEFAULT_SPRING_COEFF, DEFAULT_SPRING_LENGTH 	50.0f
		Force sf = new SpringForce(9E-005F, 50.0f); 
		
		ForceSimulator fsim = new ForceSimulator();
		fsim.addForce(nbf);
		fsim.addForce(df);
		fsim.addForce(sf);
		
		return fsim;
	}
	
	/**
	 * Creates the listener used to select and deselect nodes.
	 * 
	 * @param vis
	 * 		the prefuse visualization object
	 */
	private void createTupleListener (final Visualization vis) {
	    TupleSet selectedGroup = vis.getGroup(Visualization.SELECTED_ITEMS); 
	    selectedGroup.addTupleSetListener(new TupleSetListener() {
	        public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
	        {
	            for ( int i=0; i<rem.length; ++i ) {
	                ((VisualItem)rem[i]).setFixed(false);
	                ((VisualItem)rem[i]).setStroke(new BasicStroke(1));
	            }
	            for ( int i=0; i<add.length; ++i ) {
	                ((VisualItem)add[i]).setFixed(false);
	                ((VisualItem)add[i]).setStroke(new BasicStroke(1));
	                ((VisualItem)add[i]).setFixed(true);
	                ((VisualItem)add[i]).setStroke(new BasicStroke(5));
	            }
	            vis.run("animate");
	        }
	    });
	}
	
	/**
	 * Creates the threshold slider box used to colorize domains
	 * 
	 * @param maxConnections
	 * 		the maximal number of connections for domains
	 * @return
	 * 		the created slider box which can be added to the view
	 */
	private Box createThresholdSlider(int maxConnections) {
		MyThresholdSlider slider = new MyThresholdSlider(view, this, maxConnections);
		Box sliderBox = new Box(BoxLayout.X_AXIS);
		sliderBox.add(slider);
		sliderBox.setBackground(Color.WHITE);
		sliderBox.setBorder(BorderFactory.createTitledBorder("Threshold"));
		return sliderBox;
	}
}

/* ************************************************************************ *
 * 								Extra classes								*
 * ************************************************************************ */

/**
 * Set label positions. Labels are assumed to be DecoratorItem instances,
 * decorating their respective nodes. The layout simply gets the bounds
 * of the decorated node and assigns the label coordinates to the center
 * of those bounds.
 */
class LabelLayout2 extends Layout {
   
	protected DomainGraphView view;
	
	public LabelLayout2(String group, DomainGraphView view) {
        super(group);
        this.view = view;
    }
	
    @SuppressWarnings("unchecked")
	public void run(double frac) {
        Iterator iter = m_vis.items(m_group);
        while ( iter.hasNext() ) {
            DecoratorItem decorator = (DecoratorItem)iter.next();
            VisualItem decoratedItem = decorator.getDecoratedItem();
            Rectangle2D bounds = decoratedItem.getBounds();
  
            double x = bounds.getCenterX();
            double y = bounds.getCenterY();

            setX(decorator, null, x);
            setY(decorator, null, y);
        }
    }
}

