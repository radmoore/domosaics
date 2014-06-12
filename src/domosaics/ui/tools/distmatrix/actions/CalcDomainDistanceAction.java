package domosaics.ui.tools.distmatrix.actions;

import java.awt.event.ActionEvent;

import domosaics.algos.distance.DomainDistance;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.distmatrix.DistMatrixView;
import domosaics.ui.views.ViewType;




/**
 * Sets the distance measure for the active distance matrix to 
 * domain edit distance.
 * 
 * @author Andreas Held
 *
 */
public class CalcDomainDistanceAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DistMatrixView view = (DistMatrixView) ViewHandler.getInstance().getTool(ViewType.DISTANCEMATRIX);
		
		if (!getState() && !view.getMatrixLayoutManager().isJacard()) {
			setState(!getState());
			return;
		}
		
		DomainArrangement[] daSet = view.getView().getDaSet();
		double[][] matrix = new DomainDistance().calc(daSet, true);
		view.getMatrixLayoutManager().setToDomainDistanceMode();
		view.setMatrix(matrix);
	}

}
