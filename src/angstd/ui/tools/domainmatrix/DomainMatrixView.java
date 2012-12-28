package angstd.ui.tools.domainmatrix;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdom2.Element;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainSet;
import angstd.ui.tools.Tool;
import angstd.ui.tools.ToolFrameI;
import angstd.ui.tools.domainmatrix.components.DefaultDomainMatrixLayout;
import angstd.ui.tools.domainmatrix.components.DefaultDomainMatrixRenderer;
import angstd.ui.tools.domainmatrix.components.DomainMatrixEntry;
import angstd.ui.tools.domainmatrix.components.Pair;
import angstd.ui.views.domainview.DomainView;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.View;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.manager.ViewManager;
import angstd.ui.views.view.renderer.Renderer;



/**
 * The DomainMatrixView follows the structure of the main views like
 * {@link DomainView}. It has a layout, a renderer and manager controling
 * the view.
 * <p>
 * In short: this class manages the display of a domain matrix
 * based on a backend domain view.
 * <p>
 * A short note: MatrixEntrys can be labels (would be the case for the
 * header fields) or domain components (all other). 
 * Therefore each matrix entry can be represented by a {@link Pair} of 
 * the label and domain component where one of them is null, e.g. if
 * the domain component is null, it must be a header label. Sounds like
 * a dirty job to me. =)
 * 
 * @author Andreas Held
 *
 */
public class DomainMatrixView extends AbstractView implements PropertyChangeListener, Tool {
	protected static final long serialVersionUID = 1L;
	
	/** the component embedding this view if it exceeds the frame size */
	protected JScrollPane scrollPane;
	
	/** the frame embedding the view */
	protected ToolFrameI parentFrame;
	
	/** the backend domain view */
	protected DomainViewI domView;
	
	/** the data of this matrix represented by a 2d array of DomainMatrixEntry components */
	protected DomainMatrixEntry[][] data;
	
	/** the layout used to layout the components */
	protected DefaultDomainMatrixLayout layout;
	
	/** the renderer used to render the view */
	protected DefaultDomainMatrixRenderer viewRenderer;
	
	/** flag indicating whether or not the tool frame is already build */
	protected boolean frameInitialized = false;
	
	/**
	 * Constructor for a new DomainMatrixView
	 */
	public DomainMatrixView() {
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
	public void export(File file) {}

	/**
	 * @see Tool
	 */
	public void setToolFrame(ToolFrameI frame) {
		this.parentFrame = frame;
	}
	
	/**
	 * @see Tool
	 */
	public ToolFrameI getToolFrame() {
		return parentFrame;
	}
	
	/**
	 * Returns a 2D array containing the table data as DomainMatrixEntry
	 * components.
	 * 
	 * @return
	 * 		table data as DomainMatrixEntry components
	 */
	public DomainMatrixEntry[][] getData() {
		return data;
	}
	
	/**
	 * Returns the backend domain view
	 * 
	 * @return
	 * 		backend domain view
	 */
	public DomainViewI getDomainView() {
		return domView;
	}
	
	/**
	 * Initializes all needed data to display the view.
	 * 
	 * @param domView
	 * 		the backend domain view
	 * @param daSet
	 * 		the backend dataset
	 */
	public void setData (DomainViewI domView, DomainArrangement[] daSet) {
		this.domView = domView;
		
		// create non redundant domain list
		DomainSet domSet = new DomainSet();
		for (int i = 0; i < daSet.length; i++) 
			domSet.add(daSet[i].getDomains());

		// init matrix
		int N = daSet.length;
		int M = domSet.size();
		boolean[][] matrix = new boolean[N][M];
		
		for (int i = 0; i < N; i++)
			for (int j = 0; j < M; j++)
				matrix[i][j] = (daSet[i].contains(domSet.get(j))) ? true : false;
				
		// init data
		N +=1; M +=1;
		this.data = new DomainMatrixEntry[N][M];
		this.data[0][0] = new DomainMatrixEntry(new Pair(null, " "));
		
		// set col and rownames
		for (int i = 0; i < N-1; i++) 
			this.data[1+i][0] = new DomainMatrixEntry(new Pair(null, daSet[i].getName()));
		for (int i = 0; i < M-1; i++) 
			this.data[0][1+i] =  new DomainMatrixEntry(new Pair(null, domSet.get(i).getID()));	

		
		// fill matrix with data as string
		for (int r = 0; r < N-1; r++)
			for (int c = 0; c < M-1; c++)
				if (matrix[r][c])
					this.data[1+r][1+c] = new DomainMatrixEntry(new Pair(domView.getArrangementComponentManager().getDomainComponent(domSet.get(c)),  "1" ));
				else
					this.data[1+r][1+c] = new DomainMatrixEntry(new Pair(null,  null ));
		
		setViewLayout(new DefaultDomainMatrixLayout());
		viewRenderer = new DefaultDomainMatrixRenderer(this);
	}
	
	/**
	 * Method which adjusts the tool size to the table data
	 */
	public void autoAdjustViewSize() {
		int viewWidth = getWidth()-getInsets().left-getInsets().right;
		Dimension neededDim = layout.getNeededDim(viewWidth);
		setNewViewWidth(neededDim.width);
		setNewViewHeight(neededDim.height);
	}
	
	/**
	 * @see PropertyChangeListener
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ViewManager.PROPERTY_STRUCTURAL_CHANGE)) {
			doLayout();
		}
		//implicit PROPERTY_VISUAL_CHANGE
		repaint();
	}
	
	/**
	 * Renders the domain matrix
	 */
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

		autoAdjustViewSize();
		layout.layoutContainer(this);
	}
	
	/**
	 * Sets the layout used to layout the domains within the table
	 *  
	 * @param layout
	 * 		the layout used to layout the domains within the table
	 */
	public void setViewLayout(ViewLayout layout) {
		super.setLayout(null);
		layout.setView(this);
		this.layout = (DefaultDomainMatrixLayout) layout;
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
	public JComponent getComponent() {
		return scrollPane;
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
