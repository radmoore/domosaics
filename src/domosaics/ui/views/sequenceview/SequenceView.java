package domosaics.ui.views.sequenceview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdom2.Attribute;
import org.jdom2.Element;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.sequence.Sequence;
import domosaics.model.sequence.SequenceI;
import domosaics.ui.views.sequenceview.io.SequenceViewExporter;
import domosaics.ui.views.view.AbstractView;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;
import domosaics.ui.views.view.renderer.Renderer;




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

	@Override
	public void xmlWrite(Element viewType) {
		SequenceI[] seqs = this.getSeqs();
		for (int i = 0; i < seqs.length; i++) {
			// Protein
			Element prot = new Element("PROTEIN");
			viewType.addContent(prot);
			Attribute protId = new Attribute("id",seqs[i].getName());
			prot.setAttribute(protId);

			// AA sequence
			Element seq = new Element("SEQUENCE");
			prot.addContent(seq);
			seq.setText(seqs[i].getSeq(true));
		}
		
	}

	@Override
	public void xmlWriteViewType() {
		Attribute type = new Attribute("type","SEQUENCES");
		viewType.setAttribute(type);
	}

	@Override
	public void xmlRead(Element viewType) {
		this.setName(viewType.getName());
		// Read proteins
		List<Element> prots = viewType.getChildren("PROTEIN");
		List<SequenceI> list = new ArrayList<SequenceI>();
		// Iterate over proteins
		Iterator<Element> p = prots.iterator();
		while(p.hasNext()) {
			Element protein = p.next();
			list.add(new Sequence(protein.getAttributeValue("id"),protein.getChildTextTrim("SEQUENCE")));
		}
		seqs = list.toArray(new Sequence[list.size()]);
	}

}
