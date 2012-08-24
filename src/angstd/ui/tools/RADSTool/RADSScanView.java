package angstd.ui.tools.RADSTool;

import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JPanel;

import angstd.ui.tools.Tool;
import angstd.ui.tools.ToolFrameI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;
import angstd.webservices.RADS.ui.RADSFrame;
import angstd.webservices.RADS.ui.RADSScanPanel;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSScanView extends AbstractView implements Tool{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RADSScanToolFrame parentFrame;
	private RADSScanPanel radsScanPanel;
	private ArrangementComponent arrComp;

	public RADSScanView() {
		radsScanPanel = new RADSScanPanel(parentFrame);
	}
	
	public void setView(ArrangementComponent da) {
		this.arrComp = da;
		parentFrame.addView(this);
		parentFrame.setContent(radsScanPanel);
		//radsScanPanel.setQueryComponent(da);
	}
	
	public ArrangementComponent getArrangementComponent() {
		return this.arrComp;
	}
	
	public void setToolFrame(ToolFrameI frame) {
		parentFrame = (RADSScanToolFrame) frame;
	}

	public ToolFrameI getToolFrame() {
		return parentFrame;
	}

	public void closeWindow() {
		parentFrame.dispose();
	}

	public void setViewRenderer(Renderer renderer) {}
	public void setViewLayout(ViewLayout layout) {}
	public void export(File file) {}
	public void registerMouseListeners() { }
	public void renderView(Graphics2D g) { }
	

}
