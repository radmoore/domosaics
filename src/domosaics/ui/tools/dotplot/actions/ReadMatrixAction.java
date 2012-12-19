package domosaics.ui.tools.dotplot.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import domosaics.model.matrix.Matrix;
import domosaics.model.matrix.MatrixParser;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.dotplot.DotplotView;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.views.ViewType;


/**
 * Action which allows the loading of a new substitution matrix.
 * 
 * @author Andreas Held
 *
 */
public class ReadMatrixAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		//choose matrix file
		File file = FileDialogs.showOpenDialog(DoMosaicsUI.getInstance());
		if (file == null)
			return;
		
		// parse the matrix
		Matrix matrix = new MatrixParser().getDataFromFile(file)[0];
		if (matrix == null)
			return;
		
		DotplotView view = ViewHandler.getInstance().getTool(ViewType.DOTPLOT);
		view.getDotplotComponent().getDotplot().setSubstitutionMatrix(matrix);
		view.getToolFrame().requestFocus();
		view.repaint();
	}


}
