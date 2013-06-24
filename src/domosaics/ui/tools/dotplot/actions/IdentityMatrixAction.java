package domosaics.ui.tools.dotplot.actions;

import java.awt.event.ActionEvent;

import domosaics.model.matrix.MatrixType;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.dotplot.DotplotView;
import domosaics.ui.views.ViewType;




/**
 * Action which makes the dotplot use the identity matrix as 
 * substitution matrix.
 * 
 * @author Andreas Held
 *
 */
public class IdentityMatrixAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		if (!getState()) {
			setState(!getState());
		} else {
			DotplotView view = ViewHandler.getInstance().getTool(ViewType.DOTPLOT);
			view.getDotplotComponent().getDotplot().setSubstitutionMatrix(MatrixType.IDENTITY);
			view.getDotplotLayoutManager().setBlosumFalse();
			view.repaint();
		}
	}


}
