package domosaics.ui.tools.distmatrix;

import domosaics.ui.tools.ToolFrame;
import domosaics.ui.views.view.View;

/**
 * The distance matrix frame containing a DistMatrixPanel.
 * 
 * @author Andreas Held
 *
 */
public class DistMatrixFrame extends ToolFrame {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Adds the view to the frame by just setting the frames title
	 */
	@Override
	public void addView(View view) {
		setTitle(view.getViewInfo().getName());
	}
    
	/**
	 * the real deal is done here when the matrix panel is added to
	 * the frame during the views creation. 
	 * 
	 * @param matrixPanel
	 * 		the matrix panel to be shown within the frame
	 */
    public void showMatrix(DistMatrixPanel matrixPanel) {
		getContentPane().add(matrixPanel);
    }
}
