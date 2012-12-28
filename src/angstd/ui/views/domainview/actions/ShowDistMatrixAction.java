package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.algos.distance.JaccardDistance;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.distmatrix.DistMatrixView;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;



/**
 * Shows the distance matrix tool
 * 
 * @author Andreas Held
 *
 */
public class ShowDistMatrixAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		if (ViewHandler.getInstance().getTool(ViewType.DISTANCEMATRIX) != null) 
			ViewHandler.getInstance().removeTool(ViewHandler.getInstance().getTool(ViewType.DISTANCEMATRIX));
		
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// calculate initially jacard distance
		DomainArrangement[] daSet = domView.getDaSet();
		double[][] matrix = new JaccardDistance().calc(daSet, true);
		
		String[] colNames = new String[daSet.length];
		for (int i = 0; i < daSet.length; i++)
			colNames[i] = new String(daSet[i].getName());

		DistMatrixView view = ViewHandler.getInstance().createTool(ViewType.DISTANCEMATRIX);
		view.setView(domView);
		view.setData(matrix, colNames);
		ViewHandler.getInstance().addTool(view);
	}

}