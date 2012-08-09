package angstd.ui.tools.radscan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JPanel;
import angstd.ui.tools.Tool;
import angstd.ui.tools.ToolFrameI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RadScanView extends AbstractView implements Tool{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RadScanFrame parentFrame;
	private JPanel radsPanelHolder, radsPanel;
	private ArrangementComponent arrComp;


	public RadScanView() {
		radsPanelHolder = new JPanel(new BorderLayout());
		radsPanelHolder.setBackground(Color.white);
		radsPanelHolder.setSize(780,400);
		radsPanel = new RadScanPanel(this);
		radsPanelHolder.add(radsPanel, BorderLayout.CENTER);
	}
	
	public void setView(ArrangementComponent da) {
		this.arrComp = da;
		parentFrame.addView(this);
		parentFrame.setContent(radsPanelHolder);
	}
	
	public ArrangementComponent getArrangementComponent() {
		return this.arrComp;
	}
	
	public void setToolFrame(ToolFrameI frame) {
		parentFrame = (RadScanFrame) frame;
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
