package angstd.ui.views.sequenceview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import angstd.model.sequence.SequenceI;
import angstd.ui.views.sequenceview.io.SequenceViewExporter;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.View;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;

public class SequenceView extends AbstractView{
	private static final long serialVersionUID = 1L;

	protected JScrollPane scrollPane;
	
	protected SequenceI[] seqs;
	
	protected Renderer viewRenderer;
	
	protected int maxWidth = 0;
	protected int maxHeight = 0;
	
	
	public SequenceView () {
		super();
		
		// set up the scrollPane
		scrollPane = new JScrollPane(super.getComponent());
		scrollPane.setFocusable(false);
	}
	
	public void removeMouseListeners() {

	}
	
	public void addMouseListeners() {

	}
	
	/**
	 * @see View
	 */
	public void export(File file) {
		new SequenceViewExporter().write(file, this);
//		setChanged(false);
	}
	
	public SequenceI[] getSequences() {
		return seqs;
	}
	
	public void setSeqs(SequenceI[] seqs) {
		this.seqs = seqs;
	
//		setLayout(new DefaultDomainLayout());
		viewRenderer = new DefaultSequenceViewRenderer(this);
		
		// get max seg
		int maxLen = -1;
		for (int i = 0 ; i < seqs.length; i++)
			if(seqs[i].getLen(true) > maxLen)
				maxLen = seqs[i].getLen(true);
		
		int charwidth = 8; //Courier, normal, size 14
		int lineheight = 18; //Courier, normal, size 14 + 4
		
		maxWidth = maxLen*charwidth+50;
		maxHeight = 15+(seqs.length+1)*lineheight;
			
		doLayout();
		repaint();
	}
	
	@Override
	public void doLayout() {
		if (!isVisible() || isZoomMode())
			return;
		
		if (maxWidth != 0 && maxHeight != 0) {
			setPreferredSize(new Dimension(Math.max(getVisibleRect().width, maxWidth), Math.max(getVisibleRect().height, maxHeight)));
			setSize(Math.max(getVisibleRect().width, maxWidth), Math.max(getVisibleRect().height, maxHeight));
		}
	}
	
	@Override
	public void renderView(Graphics2D g) {
		viewRenderer.render(g);	
	}
	
	public SequenceI[] getSeqs() {
		return seqs;
	}
	
	public JComponent getComponent() {
		return scrollPane;
	}
	
	public Dimension getViewSize() {
		return scrollPane.getViewport().getViewSize();
	}

	public void registerMouseListeners() {
		// TODO Auto-generated method stub
		
	}

	
	public void setViewLayout(ViewLayout layout) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @see View
	 */
	public void setViewRenderer(Renderer renderer) {
		this.viewRenderer = renderer;
	}

}
