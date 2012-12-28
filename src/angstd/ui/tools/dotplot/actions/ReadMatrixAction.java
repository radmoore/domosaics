package angstd.ui.tools.dotplot.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import angstd.model.matrix.Matrix;
import angstd.model.matrix.MatrixParser;
import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.dotplot.DotplotView;
import angstd.ui.util.FileDialogs;
import angstd.ui.views.ViewType;



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
		File file = FileDialogs.showOpenDialog(AngstdUI.getInstance());
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
