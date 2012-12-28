package domosaics.ui.tools.distmatrix.actions;

import java.awt.event.ActionEvent;

import domosaics.algos.distance.JaccardDistance;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.distmatrix.DistMatrixView;
import domosaics.ui.views.ViewType;




/**
 * Sets the distance measure for the active distance matrix to 
 * jaccard distance.
 * 
 * @author Andreas Held
 *
 */
public class CalcJacardAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DistMatrixView view = (DistMatrixView) ViewHandler.getInstance().getTool(ViewType.DISTANCEMATRIX);
		
		if (!getState() && !view.getMatrixLayoutManager().isDomainDistance()) {
			setState(!getState());
			return;
		}
		
		DomainArrangement[] daSet = view.getView().getDaSet();
		double[][] matrix = new JaccardDistance().calc(daSet, true);
		view.getMatrixLayoutManager().setToJaccardMode();
		view.setMatrix(matrix);
	}
}
