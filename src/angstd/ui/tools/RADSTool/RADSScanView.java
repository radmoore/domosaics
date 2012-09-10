package angstd.ui.tools.RADSTool;

import java.awt.Graphics2D;
import java.io.File;


import angstd.ui.tools.Tool;
import angstd.ui.tools.ToolFrameI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;
import angstd.webservices.RADS.ui.RADSScanPanel;

/**
 * This class is used when RADSScan is called as a tool (via context menu of an arrangement).
 * It extends the abstract class {@link AbstractView} and implements the interface
 * {@link Tool}.
 *  
 * see {@link AbstractView}, {@link Tool}, {@link RADSScanPanel} for more details
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 */
public class RADSScanView extends AbstractView implements Tool{

	private static final long serialVersionUID = 1L;
	private RADSScanToolFrame parentFrame;
	private RADSScanPanel radsScanPanel;
	private ArrangementComponent arrComp;

	/**
	 * Creates a new scan View.  
	 */
	public RADSScanView() {
		radsScanPanel = new RADSScanPanel();
	}
	
	/**
	 * Sets the view
	 * 
	 * @param da - the ArrangementComponent to be displayed in this view
	 */
	public void setView(ArrangementComponent da) {
		this.arrComp = da;
		parentFrame.addView(this);
		parentFrame.setContent(radsScanPanel);
	}
	
	/**
	 * Get the ArrangementComponent of this view
	 * 
	 * @return - the ArrangementComponent
	 */
	public ArrangementComponent getArrangementComponent() {
		return this.arrComp;
	}
	
	/**
	 * See {@link Tool}
	 */
	public void setToolFrame(ToolFrameI frame) {
		parentFrame = (RADSScanToolFrame) frame;
	}

	/**
	 * See {@link Tool}
	 */
	public ToolFrameI getToolFrame() {
		return parentFrame;
	}

	/**
	 * see {@link AbstractView}
	 */
	public void setViewRenderer(Renderer renderer) {}
	
	/**
	 * see {@link AbstractView}
	 */
	public void setViewLayout(ViewLayout layout) {}
	
	/**
	 * see {@link AbstractView}
	 */
	public void export(File file) {}
	
	/**
	 * see {@link AbstractView}
	 */
	public void registerMouseListeners() { }
	
	/**
	 * see {@link AbstractView}
	 */
	public void renderView(Graphics2D g) { }
	

}
