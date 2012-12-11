package angstd.ui.views.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import angstd.model.configuration.Configuration;
import angstd.ui.util.DigestUtil;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainView;
import angstd.ui.views.domainview.renderer.additional.DomainTooltipRenderer;
import angstd.ui.views.view.components.ZoomController;
import angstd.ui.views.view.renderer.Renderer;

/**
 * AbstractView is the basic implementation for the {@link View} 
 * interface. It defines all methods for the handling of a view 
 * and its embedding panel. (Look also into {@link View} for more details)
 * <p>
 * This class also defines the {@link #paintComponent(Graphics)} method 
 * which starts the view rendering by invoking {@link #render(Graphics)}. 
 * When rendering the view the rendering hints as well as the zoom
 * transformation are set. Then the abstract method renderView() is called
 * which has to be implemented by a subclass, for instance {@link DomainView}.
 * After the view was rendered this class calls the render method
 * for each additionally added renderer (this could for instance be a
 * {@link DomainTooltipRenderer}. <br>
 * Those additionally renderer can be added by using the addRenderer(Renderer)
 * method.
 * <p>
 * A {@link ZoomController} is added to this class which controls the 
 * zoom activity on this view. Basically a AffineTransformation is calculated
 * which is applied to the graphics context when rendering the view.
 * For more details on the ZoomController, see {@link ZoomController}.
 * <p>  
 * The views information object storing important view information such 
 * as its unique id is also provided by this class.
 * <p>
 * Some general methods for the layout process (setting the views width and height)
 * are also provided by this class.
 * 
 * 
 * @author Andreas Held
 *
 */
public abstract class AbstractView extends JComponent implements View {
	private static final long serialVersionUID = 1L;
	
	/** the views information object, holding for instance its unique id */
	protected ViewInfo viewInfo;
	
	/** the parent pane embedding this view */
	protected ViewPanel parentPane;	
	
	/** List of active (additional) renderer used to render the view */
	protected List<Renderer> active_renderer;
	
	/** the controller managing the zoom mode of a view */
	protected ZoomController zoomHandler;
	
	/** the manager controlling the action states */
	protected LayoutManager layoutManager;
	
	/** For xml export of the file */
	protected Element root, viewType;
	protected Document document;
	
//	protected boolean changed;
	
	/**
	 * Basic constructor for a new View object
	 */
	public AbstractView() {
		super();
		active_renderer = new ArrayList<Renderer>();
		
		setFocusable(true);
		setAutoscrolls(true);
				
		// set up the border and layout
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		zoomHandler = new ZoomController(this);
	}
//	
//	public boolean isChanged() {
//		return changed;
//	}
//	
//	public void setChanged (boolean changed) {
//		if (this.changed == changed)
//			return;
//		this.changed = changed;
//		
//		// set icon to changed icon
//		if (changed) {
//			if (viewInfo.getIcon().equals(viewInfo.getAssociatedIcon()))
//				viewInfo.setUsedIcon(viewInfo.getAssocChangedIcon());
//			else
//				viewInfo.setUsedIcon(viewInfo.getChangedIcon());
//		} else {
//			if (viewInfo.getIcon().equals(viewInfo.getAssocChangedIcon()))
//				viewInfo.setUsedIcon(viewInfo.getDefaultIcon());
//			else
//				viewInfo.setUsedIcon(viewInfo.getAssociatedIcon());
//		}
//		WorkspaceManager.getInstance().forceRepaint();
//	}
	
	/**
	 * Set the ViewInfo which contains all backend data for the view (e.g.
	 * its unique id, icons ...).
	 *  
	 * @param info
	 * 		the view information to store
	 */
	public void setViewInfo(ViewInfo info) {
		this.viewInfo = info;
	}
	
	/**
	 * Returns the views information such as unique id various icons and so on
	 * all coupled within the ViewInfo object.
	 * 
	 * @return
	 * 		wieInfo object holding all relevant information about the view.
	 */
	public ViewInfo getViewInfo() {
		return viewInfo;
	}
	
	/* ******************************************************************* *
	 *   				Methods important for the layout process		   *
	 * ******************************************************************* */
	
	 /**
     * Sets a new view width
     * 
     * @param newWidth
     * 		the new width for the view
     */
	public void setNewViewWidth(final int newWidth) {
		if (getWidth() == newWidth) 
			return;
		
		if (getVisibleRect().width < newWidth) {
			setPreferredSize(new Dimension(newWidth, getHeight()));
			setSize(newWidth, getHeight());
		} else if (getVisibleRect().width > newWidth) {
			setPreferredSize(new Dimension(getVisibleRect().width, getHeight()));
			setSize(getVisibleRect().width, getHeight());
		}
    }
	
	/**
	 * Sets a new view height
	 * 
	 * @param newHeight
	 * 		the new height for the view
	 */
    public void setNewViewHeight(final int newHeight) {
    	if (getHeight() == newHeight) 
			return;
		if (getVisibleRect().height < newHeight) {
			setPreferredSize(new Dimension(getWidth(), newHeight));
			setSize(getWidth(), newHeight);
		} else if (getVisibleRect().height > newHeight) {
			setPreferredSize(new Dimension(getWidth(),getVisibleRect().height));
			setSize(getWidth(), getVisibleRect().height);
		}
    }

	/* ******************************************************************* *
	 *   							Renderer methods	 				   *
	 * ******************************************************************* */

	/**
	 * Adds a new renderer to the view
	 * 
	 * @param r 
	 * 		the renderer to add
	 */
	public void addRenderer(Renderer r) {
		this.active_renderer.add(r);
	}
	
	/**
	 * Removes a renderer from the view
	 * 
	 * @param r 
	 * 		the renderer to remove
	 */
	public void removeRenderer (Renderer r) {
		this.active_renderer.remove(r);
	}
	
	/**
	 * Removes all additional renderer
	 */
	public void removeAllRenderer () {
		this.active_renderer.clear();;
	}
	
	/* ******************************************************************* *
	 * 				Methods regarding ViewPanel and the view itself		   *
	 * ******************************************************************* */

	/**
	 * Returns the component containing the view.
	 * This can be the view itself or a surrounding component, e.g. ScrollPane
	 * 
	 * @return 
	 * 		the component embedding the view
	 */
	public JComponent getComponent() {
		return this;
	}
	
	/**
	 * Returns the real view component. This is used to split the view component
	 * from a component that is returned by getComponent(). 
	 * <p>
	 * For example, {@link #getComponent()} might return a ScrollPane that 
	 * contains the JComponent returned by this method.
	 * 
	 * @return 
	 * 		the real view
	 */
	public JComponent getViewComponent(){
		return this;
	}
	
	/**
	 * Sets this views parent pane.
	 * 
	 * @param parentPane 
	 * 		the viewPanel containing the view
	 */
	public void setParentPane(ViewPanel parentPane) {
		this.parentPane = parentPane;
	}
	
	/**
	 * Returns the panel displaying this view
	 * 
	 * @return 
	 * 		the view panel
	 */
	public ViewPanel getParentPane() {
		return parentPane;
	}
	
	/* ******************************************************************* *
	 *   						 Rendering methods						   *
	 * ******************************************************************* */
	
	/**
	 * Method which is invoked when the view has to be painted, e.g. when the
	 * user changes the panels size.
	 * <p>
	 * Delegates to the render method.
	 */
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		render(g);
	}

	/**
	 * Renders the view. If the view is within zoom mode the 
	 * calculated and needed zoom transformation is requested
	 * from the ZoomController and set to the graphics context
	 * at the beginning of the rendering process. 
	 * <p>
	 * This method iterates over the list of active renderer and 
	 * paints all renderer and the view.
	 * <p>
	 * Delegates to {@link #renderView(Graphics2D)} where the view 
	 * itself is rendered.
	 */
	public void render(Graphics g) {
		 // create a graphics2d 
		Graphics2D g2 = (Graphics2D) g.create();

		// if in zoom mode use the affine transformation calculated by the zoomHandler
		if (zoomHandler.isZoomMode()) 
			g2.setTransform(zoomHandler.getZoomTransform());

		// set Antialiasing on
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

		// render the view first
		renderView(g2);
		
		// then go over all active renderer
		if (active_renderer.size() > 0) 		
			for (Renderer r : active_renderer) 
				r.render(g2);
			
		g2.dispose();
	}

	/**
	 * The view rendering for subclasses, e.g. {@link DomainView}
	 * 
	 * @param g 
	 * 		the graphics context used to render the view
	 */
	public abstract void renderView(Graphics2D g);
	
	
	/* ******************************************************************* *
	 *   						 Zoom controlling methods				   *
	 * ******************************************************************* */
	
	/**
	 * Wrapper around the ZoomControllers isZoomMode method. Returns
	 * whether or not the view is in zoom mode.
	 * 
	 * @return
	 * 		whether or not the view is within zoom mode
	 */
	public boolean isZoomMode() {
		return zoomHandler.isZoomMode();
	}
	
	/**
	 * Wrapper around the ZoomControllers toggleZoomMode method.
	 * Switches the zoom mode for a view between on and off and
	 * makes the {@link ZoomController} manage the details.
	 */
	public void toggleZoomMode() {
		zoomHandler.toggleZoomMode();
	}
	
	/**
	 * registers the zoom control as mouse listener to the view.
	 */
	protected void addZoomControlMouseListener() {
		addMouseWheelListener(zoomHandler);
		addMouseMotionListener(zoomHandler);
		addMouseListener(zoomHandler);
	}
	
	/**
	 * Removes all registered mouse listeners
	 */
	public void removeMouseListeners() {
		MouseListener[] ml = getMouseListeners();
		MouseMotionListener[] mml = getMouseMotionListeners();
		MouseWheelListener[] mwl = getMouseWheelListeners();
		
		for (int i = 0; i < ml.length; i++)
			removeMouseListener(ml[i]);
		
		for (int i = 0; i < mml.length; i++)
			removeMouseMotionListener(mml[i]);
		
		for (int i = 0; i < mwl.length; i++)
			removeMouseWheelListener(mwl[i]);
	}
	
	public void export(File file) { 
		root = new Element("DOMOSAICS_VIEW");
		document = new Document(root); 
		viewType = new Element("VIEW");
		root.setAttribute(new Attribute("hash",DigestUtil.createDigest(this.getViewInfo().getName())));
		root.addContent(viewType);
		this.xmlWriteViewType();
		Attribute viewName = new Attribute("name",this.getViewInfo().getName());
		viewType.setAttribute(viewName);
		this.xmlWrite(viewType);
        XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        try {
			sortie.output(document, System.out);
	        sortie.output(document, new FileOutputStream(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Configuration.getLogger().debug(e.toString());
		}
	}
	
	public void importXML(File file) { 
		try {
			SAXBuilder sxb = new SAXBuilder();

			//Create a new JDOM document with the XML file
			document = sxb.build(file);

			//Initialize a new element to the root of the document.
			viewType = document.getRootElement().getChild("DOMOSAICS_VIEW");
			this.xmlRead(viewType);
			
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Configuration.getLogger().debug(e.toString());
		}
	}

	public static ViewType detectViewType(File viewFile) {
		
		try {
			SAXBuilder sxb = new SAXBuilder();

			//Create a new JDOM document with the XML file
			Document doc = sxb.build(viewFile);

			//Initialize a new element to the root of the document.
			Element r = doc.getRootElement();

			System.out.println(r.getName());
			if(r.getName()!="DOMAICS_VIEW")
				if (!MessageUtil.showDialog( viewFile.getName()+" does not appear to be a DoMosaic file. Continue?"))
					return null;

			String viewType = r.getChildren("VIEW").get(0).getAttributeValue("type");
			if (viewType.equals("SEQUENCES"))
				return  ViewType.SEQUENCE;

			if (viewType.equals("DOMAINTREE"))
				return  ViewType.DOMAINTREE;

			if (viewType.equals("TREE"))
				return  ViewType.TREE;

			if (viewType.equals("ARRANGEMENTS"))
				return  ViewType.DOMAINS;

		}
		catch(Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		
		return null;
		
	}
	
}
