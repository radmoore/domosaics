package domosaics.ui.tools.dotplot;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdom2.Element;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;
import domosaics.model.dotplot.Dotplot;
import domosaics.model.dotplot.DotplotChangeEvent;
import domosaics.model.dotplot.DotplotChangeListener;
import domosaics.ui.tools.Tool;
import domosaics.ui.tools.ToolFrameI;
import domosaics.ui.tools.dotplot.components.DefaultDotplotLayout;
import domosaics.ui.tools.dotplot.components.DomainMatcher;
import domosaics.ui.tools.dotplot.components.DotplotComponent;
import domosaics.ui.tools.dotplot.components.DotplotLayoutManager;
import domosaics.ui.tools.dotplot.renderer.DefaultDotplotViewRenderer;
import domosaics.ui.tools.dotplot.renderer.DomainMatchRenderer;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.manager.DomainArrangementComponentManager;
import domosaics.ui.views.domainview.manager.DomainColorManager;
import domosaics.ui.views.domainview.manager.DomainShapeManager;
import domosaics.ui.views.view.AbstractView;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;
import domosaics.ui.views.view.manager.ViewManager;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * The dotplot view follows the structure of the main views like
 * {@link DomainView}. It has a layout, a renderer and manager controlling
 * the view.
 * <p>
 * In short: this class manages the display of a domain dotplot
 * based on a backend domain view.
 * <p>
 * The view is based on the backend data structure {@link Dotplot} and is 
 * registered as listener to changes. Therefore it can refresh and repaint
 * itself if the dotplot changes. E.g. because the user changed a threshold.
 * 
 * @author Andreas Held
 *
 */
public class DotplotView extends AbstractView implements Tool, PropertyChangeListener, DotplotChangeListener {
	protected static final long serialVersionUID = 1L;
	
	/** the component embedding this view if it exceeds the frame size */
	protected JScrollPane scrollPane;
	
	/** the frame embedding the view */
	protected DotplotFrame parentFrame;
	
	/** backend domain view */
	protected DomainViewI domView;
	
	/** the layout used to layout the components */
	protected DefaultDotplotLayout layout;
	
	/** the renderer used to render the view */
	protected DefaultDotplotViewRenderer viewRenderer;
	
	/** backend data structure which is going to be visualized within this view*/
	protected Dotplot plot;
	
	/** the view component based on the backend data structure */
	protected DotplotComponent dotplot;
	
	/** the first arrangement used to create the dotplot */
	protected ArrangementComponent da1;
	
	/** the second arrangement used to create the dotplot */
	protected ArrangementComponent da2;
	
	/** the layout manager for this view */
	protected DotplotLayoutManager layoutManager;
	
	/** flag indicating whether or not the tool frame is already build */
	protected boolean frameInitialized = false;
	
	/** Helper class handling the calculation of the domain matches, indicating how similar the domains are */
	protected DomainMatcher matchPercent;
	
	protected DomainArrangementComponentManager componentManager;
	
	
	/**
	 * Constructor for a new DotplotView
	 */
	public DotplotView() {
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
	public void export(File file) {	}
	
	/**
	 * Sets the view and therefore initializes all needed parameters.
	 * This method also adds the view to its frame.
	 * 
	 * @param domView
	 * 		the domain view used to create the dotplot view
	 */
	public void setDomainView (DomainViewI domView) {
		this.domView = domView;
	
		// first of all get a grip on the two selected arrangements and clone them
		try {
			Collection<ArrangementComponent> das = domView.getArrangementSelectionManager().getSelection();
			Iterator<ArrangementComponent> iter = das.iterator();
			
			/*
			 * init the domain arrangements. if only one arrangement was
			 * selected take clone this arrangement for both dotplot
			 * arrangements.
			 */
			ArrangementComponent dac = iter.next();
			da1 = new ArrangementComponent((DomainArrangement) (dac.getDomainArrangement().clone()), null);
			da2 = (iter.hasNext()) ? 
					  new ArrangementComponent((DomainArrangement) (iter.next().getDomainArrangement().clone()), null)
					: new ArrangementComponent((DomainArrangement) (dac.getDomainArrangement().clone()), null);
		
		} catch (Exception e) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return;
		}
		
		// if the user wants to look at the domain similarity init the domain matches as well
		matchPercent = new DomainMatcher(da1.getDomainArrangement(), da2.getDomainArrangement());
		
		// generate the dotplot now and register the needed listeners
		plot = new Dotplot(da1.getDomainArrangement(), da2.getDomainArrangement());
		
		parentFrame.addSliderListener(plot); 	// for gaining the new threshold parameters
		plot.addDotplotListener(this);
		
		// generate the graphical object representing the dotplot
		dotplot = new DotplotComponent(plot);
		plot.compute(); // initial computation
		
		// set up the views manager and renderer
		setViewLayout(new DefaultDotplotLayout());
		viewRenderer = new DefaultDotplotViewRenderer(this);

		layoutManager = new DotplotLayoutManager(viewInfo.getActionManager());
		layoutManager.addPropertyChangeListener(this);
		
		addRenderer(new DomainMatchRenderer(this));
	}
	
	/**
	 * @see AbstractView
	 */
	public void registerMouseListeners() {
		// remove all listener before registering the new ones.
		removeMouseListeners();
		
		// use zoom mode listeners only (defined in ABstractView)
		if(isZoomMode()) {
			addZoomControlMouseListener();
			return;
		}
	}
	
	/**
	 * @see Tool
	 */
	public void setToolFrame(ToolFrameI frame) {
		parentFrame = (DotplotFrame) frame;
	}
	
	/**
	 * @see Tool
	 */
	public ToolFrameI getToolFrame() {
		return parentFrame;
	}
	
	/**
	 * Listener method invoked when the dotplot changed and therefore a repaint is necessary
	 */
	public void dotplotChanged(DotplotChangeEvent evt) {
		dotplot.refresh();
    	repaint(); 
	}

	/**
	 * Returns the graphical dotplot component
	 * 
	 * @return
	 * 		graphical dotplot component
	 */
	public DotplotComponent getDotplotComponent() {
		return dotplot;
	}
	
	/**
	 * Returns the first arrangement used within the dotplot
	 * 
	 * @return
	 * 		 first arrangement used within the dotplot
	 */
	public ArrangementComponent getDa1() {
		return da1;
	}
	
	/**
	 * Returns the second arrangement used within the dotplot
	 * 
	 * @return
	 * 		 second arrangement used within the dotplot
	 */
	public ArrangementComponent getDa2() {
		return da2;
	}
	
	/**
	 * Returns the layout manager
	 * 
	 * @return
	 * 		layout manager
	 */
	public DotplotLayoutManager getDotplotLayoutManager() {
		return layoutManager;
	}
	
	/**
	 * Returns the domain matcher helper class managing the 
	 * similarity matches for same representants of a domain family
	 * 
	 * @return
	 * 		domain matcher helper class
	 */
	public DomainMatcher getMatchScores() {
		return matchPercent;
	}
	
	/**
	 * Returns the domain component manager for the dotplot view so its not
	 * needed to mess around with the backend views component manager.
	 * 
	 * @return
	 * 		domain component manager for the dotplot view
	 */
	public DomainArrangementComponentManager getDomainComponentManager() {
		if (componentManager == null)
			componentManager = new DomainArrangementComponentManager();
		return componentManager;
	}
	
	/**
	 * Wrapper method around the backend domain view to get access to the
	 * domain color manager needed for rendering.
	 * 
	 * @return
	 * 		color manager of the backend domain view
	 */
	public DomainColorManager getDomainColorManager() {
		return domView.getDomainColorManager();
	}
	
	/**
	 * Wrapper method around the backend domain view to get access to the
	 * domain shape manager needed for rendering.
	 * 
	 * @return
	 * 		shape manager of the backend domain view
	 */
	public DomainShapeManager getDomainShapeManager() {
		return domView.getDomainShapeManager();
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
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewManager.PROPERTY_STRUCTURAL_CHANGE)) {
			layout.dotplotStructureChanged();
			doLayout();
		}
		//implicit PROPERTY_VISUAL_CHANGE
		repaint();
	}
	
	/**
	 * Renders the dotplot view
	 */
	public void renderView(Graphics2D g) {
		viewRenderer.render(g);
	}
	
	/**
	 * @see JComponent
	 */
	public void doLayout() {
		if (parentFrame == null || isZoomMode())
			return;
		
		// check if there is enough space to draw the plot
		Dimension neededDim = layout.getNeededDim();
		
		Dimension viewSize = getSize();
		Insets viewInsets = getInsets();
		int width = viewSize.width - viewInsets.left - viewInsets.right;
		int height = viewSize.height - viewInsets.top - viewInsets.bottom;
		
		if (neededDim.height > height) 
			setNewViewHeight(neededDim.height+viewInsets.top + viewInsets.bottom);

		if (neededDim.width > width) 
			setNewViewWidth(neededDim.width+viewInsets.left + viewInsets.right);

		layout.layoutContainer(this);
	}
	
	/**
	 * Sets the layout used to layout the dotplot components
	 * 
	 * @param layout
	 * 		the layout used to layout the dotplot components
	 */
	public void setViewLayout(ViewLayout layout) {
		super.setLayout(null);
		layout.setView(this);
		this.layout = (DefaultDotplotLayout) layout;
	}

	
	/* ******************************************************************* *
	 *   							GET methods							   *
	 * ******************************************************************* */
	
	/**
	 * @see View
	 */
	public JComponent getComponent() {
		return scrollPane;
	}

	/**
	 * Returns the renderer used to render the dotplot view
	 * 
	 * @return
	 * 		renderer used to render the dotplot view
	 */
	public Renderer getViewRenderer() {
		return viewRenderer;
	}

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
