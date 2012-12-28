package domosaics.ui.tools.domaingraph;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.jdom2.Element;

import domosaics.ui.tools.Tool;
import domosaics.ui.tools.ToolFrameI;
import domosaics.ui.tools.domaingraph.components.GraphLayoutManager;
import domosaics.ui.tools.domaingraph.components.PrefuseGraph;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.view.AbstractView;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * This view shows a co-occurrence graph for all visible domains within 
 * the dataset. To display the network the external package prefuse is used
 * which comes with a trade off regarding the image export. Only the 
 * visible area can be exported and therefore the user has to manually 
 * zoom to the area he wants to export as image.
 * <p>
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainGraphView extends AbstractView implements Tool {
	protected static final long serialVersionUID = 1L;
	
	/** the frame embedding the view */
	protected DomainGraphFrame parentFrame;
	
	/** the backend domain view */
	protected DomainViewI domView;
	
	/** the prefuse graph managing the displaying */
	protected PrefuseGraph prefuseGraph;
	
	/** the layout manager for this view */
	protected GraphLayoutManager layoutManager;
	
	/** flag indicating whether or not the tool frame is already build */
	protected boolean frameInitialized = false;
	
	
	/**
	 * Constructor for a new DomainGraphView
	 */
	public DomainGraphView() {
		super();
		setFocusable(true);
		setAutoscrolls(true);

		// set up the border and layout
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	/**
	 * @see View
	 */
	public void export(File file) {}
	
	/**
	 * Sets the backend domain view and therefore
	 * initializes the prefuse graph
	 * 
	 * @param domView
	 * 		backend domain view
	 */
	public void setDomainView (DomainViewI domView) {
		this.domView = domView;
		
		layoutManager = new GraphLayoutManager(viewInfo.getActionManager());
		prefuseGraph = new PrefuseGraph(this);
		parentFrame.showGraph(prefuseGraph);
	}
	
	/**
	 * Sets the prefuse graph which manages the displaying
	 * 
	 * @param prefuseGraph
	 * 		the prefuse graph which manages the displaying
	 */
	public void setPrefuseGraph(PrefuseGraph prefuseGraph) {
		this.prefuseGraph = prefuseGraph;
	}
	
	/**
	 * Returns the prefuse graph which manages the displaying
	 * 
	 * @return
	 * 		prefuse graph 
	 */
	public PrefuseGraph getPrefuseGraph() {
		return prefuseGraph;
	}
	
	/**
	 * Helper method necessary for image export returning the views width
	 */
	@Override
	public int getWidth() {
		return prefuseGraph.getDisplay().getWidth();
	}
	
	/**
	 * Helper method necessary for image export returning the views height
	 */
	@Override
	public int getHeight() {
		return prefuseGraph.getDisplay().getHeight();
	}
	
	/**
	 * Helper method necessary for image export returning the views bounds
	 */
	@Override
	public Rectangle getBounds() {
		return prefuseGraph.getDisplay().getBounds();
	}
	
	/**
	 * Returns the layout manager which manages the menu actions 
	 * regarding layout options
	 * 
	 * @return
	 * 		the layout manager
	 */
	public GraphLayoutManager getGraphLayoutManager() {
		return layoutManager;
	}
	
	/**
	 * Returns the backend domain view
	 * 
	 * @return
	 * 		backend domain view
	 */
	public DomainViewI getDomView() {
		return domView;
	}
	
	/**
	 * only used for image exporting of the graph
	 */
	public void renderView(Graphics2D g) {
		prefuseGraph.getDisplay().update(g);
	}
	
	/**
	 * @see View
	 */
	public JComponent getComponent() {
		return this;
	}
	
	/**
	 * @see Tool
	 */
	public void setToolFrame(ToolFrameI frame) {
		parentFrame = (DomainGraphFrame) frame;
	}
	
	/**
	 * @see Tool
	 */
	public ToolFrameI getToolFrame() {
		return parentFrame;
	}
	
	/**
	 * @see AbstractView
	 */
	public void registerMouseListeners() {}
	
	/**
	 * @see ViewI
	 */
	public void setViewLayout(ViewLayout layout) { }

	
	public void setViewRenderer(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void xmlWrite(Element viewType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void xmlWriteViewType() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void xmlRead(Element viewType) {
		// TODO Auto-generated method stub
		
	}

}
