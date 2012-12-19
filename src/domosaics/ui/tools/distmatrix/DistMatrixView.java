package domosaics.ui.tools.distmatrix;

import java.awt.Graphics2D;
import java.io.File;

import org.jdom2.Element;

import domosaics.ui.tools.Tool;
import domosaics.ui.tools.ToolFrameI;
import domosaics.ui.tools.distmatrix.components.MatrixLayoutManager;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.view.AbstractView;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.layout.ViewLayout;
import domosaics.ui.views.view.renderer.Renderer;


/**
 * The DistanceMatrixView created by the view manager. This class
 * is just used for initialization the real matrix is realized
 * by a JTable and the {@link DistMatrixPanel} which is embedded in the
 * ToolFrame.
 * 
 * @author Andreas Held
 *
 */
public class DistMatrixView extends AbstractView implements Tool {
	protected static final long serialVersionUID = 1L;

	/** the parent tool frame */
	protected DistMatrixFrame parentFrame;
	
	/** the matrix header (column names) */
	protected String[] colNames;
	
	/** the matrix data without header */
	protected double[][] data;
	
	/** the matrix panel */
	protected DistMatrixPanel matrixPanel;

	/** the layout manager messing around with the actions */
	protected MatrixLayoutManager layoutManager;
	
	/** the domain view used to create the matrix */
	protected DomainViewI view;
	
	
	/**
	 * @see View
	 */
	public void export(File file) {}
	
	/**
	 * Sets the view used to create the distance matrix
	 * 
	 * @param view
	 * 		the view used to create the distance matrix
	 */
	public void setView(DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * Returns the view used to create the distance matrix
	 * 
	 * @return
	 * 		view used to create the distance matrix
	 */
	public DomainViewI getView() {
		return view;
	}
	
	/**
	 * Sets initially the matrix data which can be changed then using
	 * setMatrix. When this method is triggered (optimally after the tools creation)
	 * the matrixPanel and everything is initialized and set up.
	 * 
	 * @param data
	 * 		distance matrix data (without header)
	 * @param colNames
	 * 		distance matrix header
	 */
	public void setData (double[][] data, String[] colNames) {
		this.colNames = colNames;
		this.data = data;
		
		// init manager
		layoutManager = new MatrixLayoutManager(viewInfo.getActionManager());

		matrixPanel  = new DistMatrixPanel(data, colNames);
		parentFrame.showMatrix(matrixPanel);
	}
	
	/**
	 * Returns the matrix data
	 * 
	 * @return
	 * 		matrix data (without header)
	 */
	public double[][] getData() {
		return data;
	}
	
	/**
	 * Return the column names (table header)
	 * 
	 * @return
	 * 		column names
	 */
	public String[] getColNames() {
		return colNames;
	}
	
	/**
	 * Wrapper around the DistMatrixPanel to set the matrix.
	 * 
	 * @param data
	 * 		the new matrix data
	 */
	public void setMatrix(double[][] data) {
		matrixPanel.setMatrix(data);
	}
	
	/**
	 * Get a grip on the MatrixLayoutManager
	 * 
	 * @return
	 * 		the MatrixLayoutManager managing the action states
	 */
	public MatrixLayoutManager getMatrixLayoutManager() {
		return layoutManager;
	}
	
	/**
	 * No manually rendering takes place here, the table cell renderer 
	 * does the job.
	 * 
	 * @see AbstractView
	 */
	public void renderView(Graphics2D g) { }

	/**
	 * @see Tool
	 */
	public void setToolFrame(ToolFrameI frame) {
		this.parentFrame = (DistMatrixFrame) frame;
	}
	
	/**
	 * @see Tool
	 */
	public ToolFrameI getToolFrame() {
		return parentFrame;
	}
	
	/**
	 * @see AbstractView
	 */
	public void registerMouseListeners() {}
	
	/**
	 * @see ViewI
	 */
	public void setViewLayout(ViewLayout layout) { }

	
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
