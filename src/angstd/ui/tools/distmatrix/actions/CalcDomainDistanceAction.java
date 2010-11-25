package angstd.ui.tools.distmatrix.actions;

import java.awt.event.ActionEvent;

import angstd.algos.distance.DomainDistance;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.distmatrix.DistMatrixView;
import angstd.ui.views.ViewType;

/**
 * Sets the distance measure for the active distance matrix to 
 * domain edit distance.
 * 
 * @author Andreas Held
 *
 */
public class CalcDomainDistanceAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
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
