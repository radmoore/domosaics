package domosaics.ui.tools.domainlegend;

import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdom2.Element;

import domosaics.model.arrangement.DomainFamily;
import domosaics.ui.tools.Tool;
import domosaics.ui.tools.ToolFrameI;
import domosaics.ui.tools.domainlegend.components.DefaultDomainLegendLayout;
import domosaics.ui.tools.domainlegend.components.DefaultDomainLegendRenderer;
import domosaics.ui.tools.domainlegend.components.LegendComponent;
import domosaics.ui.tools.domainlegend.components.LegendComponentManager;
import domosaics.ui.tools.domainlegend.components.LegendLayoutManager;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.manager.DomainColorManager;
import domosaics.ui.views.domainview.manager.DomainLayoutManager;
import domosaics.ui.views.domainview.manager.DomainShapeManager;
import domosaics.ui.views.view.AbstractView;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;
import domosaics.ui.views.view.manager.ViewManager;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * The legend view follows the structure of the main views like
 * {@link DomainView}. It has a layout, a renderer and manager controlling
 * the view.
 * <p>
 * In short: this class manages the display of a domain legend
 * based on a backend domain view.
 * 
 * @author Andreas Held
 *
 */
public class DomainLegendView extends AbstractView implements PropertyChangeListener, Tool {
	protected static final long serialVersionUID = 1L;
	
	/** the component embedding this view if it exceeds the frame size */
	protected JScrollPane scrollPane;
	
	/** the frame embedding the view */
	protected ToolFrameI parentFrame;
	
	/** the backend domain view */
	protected DomainViewI domView;
	
	/** a list of domain families in form of representative domain components */ 
	protected List<DomainComponent> doms;
	
	/** the legend component manager */
	protected LegendComponentManager componentManager;
	
	/** the layout used to layout the components */
	protected DefaultDomainLegendLayout layout;
	
	/** the renderer used to render the view */
	protected DefaultDomainLegendRenderer viewRenderer;
	
	/** the layout manager for this view */
	protected LegendLayoutManager layoutManager;
	
	/** flag indicating whether or not the tool frame is already build */
	protected boolean frameInitialized = false;
	
	
	/**
	 * Constructor for a new DomainLegendView
	 */
	public DomainLegendView() {
		super();
		setFocusable(true);
		setAutoscrolls(true);
		
		// set up the scrollPane
		scrollPane = new JScrollPane(super.getComponent());
		scrollPane.setFocusable(false);

		// set up the border and layout
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	/**
	 * @see View
	 */
	@Override
	public void export(File file) {}
	
	/**
	 * @see Tool
	 */
	@Override
	public void setToolFrame(ToolFrameI frame) {
		this.parentFrame = frame;
	}
	
	/**
	 * @see Tool
	 */
	@Override
	public ToolFrameI getToolFrame() {
		return parentFrame;
	}
	
	/**
	 * Sets the backend domain view for the legend and therefore
	 * initializes the legend
	 * 
	 * @param domView
	 * 		backend domain view
	 */
	public void setDomainView (DomainViewI domView) {
		this.domView = domView;
	
		componentManager = new LegendComponentManager();
		initDoms();
			
		setViewLayout(new DefaultDomainLegendLayout());
		viewRenderer = new DefaultDomainLegendRenderer(this);

		// init manager
		layoutManager = new LegendLayoutManager(viewInfo.getActionManager());
		layoutManager.addPropertyChangeListener(this);
		parentFrame.setSize(parentFrame.getWidth(), parentFrame.getHeight()+1);
	}

	/**
	 * Helper method which computes the frequency for each domain family
	 * and initiliazes the legend components.
	 */
	private void initDoms() {
		// get domains to display (only visible doms if some nodes are collapsed)
		Map<DomainFamily, Integer> famFrequency = new HashMap<DomainFamily, Integer>();
		doms = new ArrayList<DomainComponent>();
		Iterator<DomainComponent> iter = domView.getArrangementComponentManager().getDomainComponentsIterator();
		while ( iter.hasNext() ) {
			
			DomainComponent dc = iter.next();
//			DomainFamily domFamily = dc.getDomain().getFamily();
			if ( domView.getDomainLayoutManager().isNameDisplayed() )
				
			
			if ( !dc.isVisible() )
				continue;
			
			if ( famFrequency.get( dc.getDomain().getFamily() ) == null ) {
				
				famFrequency.put( dc.getDomain().getFamily(), 1 );
				doms.add(dc);	
			} 
			else {
				int newVal = famFrequency.get(dc.getDomain().getFamily())+1;
				famFrequency.put(dc.getDomain().getFamily(), newVal);
			}
		}
		
		// add the frequencys to the LegendComponents
		for (int i = 0; i < doms.size(); i++) {
			LegendComponent lc = getLegendComponentManager().getComponent(doms.get(i));
			lc.setFrequency(famFrequency.get(lc.getDomainComponent().getDomain().getFamily()));
		}
	}
	
	/**
	 * Returns the layout manager
	 * 
	 * @return
	 * 		layout manager
	 */
	public LegendLayoutManager getLegendLayoutManager() {
		return layoutManager;
	}
	
	/**
	 * Method which adjusts the tool size to the legend data
	 */
	public void autoAdjustViewSize() {
		int width = getWidth()-getInsets().left-getInsets().right;
		int colWidth = layout.getMaxLegendComponent().width;
		int cols = (int) Math.floor(width / colWidth);
		if (cols == 0)
			return;
		int rows = (int) Math.ceil(doms.size() / (double) cols);
		
		if (!frameInitialized) {
			frameInitialized = true;
			int rowHeight = DefaultDomainLegendLayout.PROPSIZEHEIGHT+DefaultDomainLegendLayout.MINDISTANCEVERTICAL;
			if (100+rows*rowHeight < parentFrame.getHeight()) {
				parentFrame.setSize(parentFrame.getWidth(), 100+rows*rowHeight);
				return;
			}
		}
		
    	int minHeight = DefaultDomainLegendLayout.PROPSIZEHEIGHT+DefaultDomainLegendLayout.MINDISTANCEVERTICAL;        
    	int height = (rows * minHeight)+getInsets().top;
		setNewViewHeight(height);
	}
	
	/**
	 * Wrapper around the backend domain view to get the 
	 * domain layout manager
	 * 
	 * @return
	 * 		layout manager of the backend domain view
	 */
	public DomainLayoutManager getDomainLayoutManager() {
		return domView.getDomainLayoutManager();
	}
	
	/**
	 * Wrapper around the backend domain view to get the 
	 * domain color manager
	 * 
	 * @return
	 * 		color manager of the backend domain view
	 */
	public DomainColorManager getDomainColorManager() {
		return domView.getDomainColorManager();
	}
	
	/**
	 * Wrapper around the backend domain view to get the 
	 * domain shape manager
	 * 
	 * @return
	 * 		shape manager of the backend domain view
	 */
	public DomainShapeManager getDomainShapeManager() {
		return domView.getDomainShapeManager();
	}
	
	/**
	 * Returns a list in which each domain is a representant of a
	 * domain family
	 * 
	 * @return
	 * 		list of domain familys in form of domain components
	 */
	public List<DomainComponent> getDoms() {
		return doms;
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
	 * @see PropertyChangeListener
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewManager.PROPERTY_STRUCTURAL_CHANGE)) {
			layout.legendStructureChanged();
			doLayout();
		}
		//implicit PROPERTY_VISUAL_CHANGE
		repaint();
	}
	
	/**
	 * Renders the legend
	 */
	@Override
	public void renderView(Graphics2D g) {
		viewRenderer.render(g);
	}
	
	/**
	 * @see JComponent
	 */
	@Override
	public void doLayout() {
		if (parentFrame == null || isZoomMode())
			return;

		if (doms.size() == 0)
			return;
		
		autoAdjustViewSize();
		layout.layoutContainer(this);
	}
	
	/**
	 * Sets the layout used to layout the legend components
	 * 
	 * @param layout
	 * 		the layout used to layout the legend components
	 */
	@Override
	public void setViewLayout(ViewLayout layout) {
		super.setLayout(null);
		layout.setView(this);
		this.layout = (DefaultDomainLegendLayout) layout;
	}
	
	/**
	 * Returns the component manager which manages the legend components
	 * 
	 * @return
	 * 		the component manager
	 */
	public LegendComponentManager getLegendComponentManager() {
		return componentManager;
	}

	/**
	 * Returns the renderer used to render the legend view
	 * 
	 * @return
	 * 		renderer used to render the legend view
	 */
	public Renderer getViewRenderer() {
		return viewRenderer;
	}
	
	/**
	 * @see View
	 */
	@Override
	public JComponent getComponent() {
		return scrollPane;
	}
	
	/**
	 * @see AbstractView
	 */
	@Override
	public void registerMouseListeners() {
		// remove all listener before registering the new ones.
		removeMouseListeners();
		
		// use zoom mode listeners only (defined in ABstractView)
		if(isZoomMode()) {
			addZoomControlMouseListener();
			return;
		}
	}

	
	@Override
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
