package angstd.ui.tools.radscan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import angstd.ui.tools.Tool;
import angstd.ui.tools.ToolFrameI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;

public class RadScanView extends AbstractView implements Tool{

	private RadScanFrame parentFrame;
	private JPanel radsPanelHolder, radsPanel;
	private ArrangementComponent arrComp = null;


	public RadScanView() {
		radsPanelHolder = new JPanel(new BorderLayout());
		radsPanelHolder.setBackground(Color.white);
		radsPanelHolder.setSize(780,400);
		radsPanelHolder.add(radsPanel = new RadScanPanel(this), BorderLayout.CENTER);
	}
	
	public void setView(ArrangementComponent da) {
//		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
//		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
//		Border compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
		this.arrComp = da;
		parentFrame.addView(this);
		parentFrame.setContent(radsPanelHolder);
	}
	
	public ArrangementComponent getArrangementComponent() {
		System.out.println("This is the arrangements here: "+arrComp.toString());
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
