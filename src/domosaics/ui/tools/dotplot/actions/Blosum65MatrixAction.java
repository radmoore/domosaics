package domosaics.ui.tools.dotplot.actions;

import java.awt.event.ActionEvent;

import domosaics.model.matrix.MatrixType;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.dotplot.DotplotView;
import domosaics.ui.views.ViewType;




/**
 * Action which makes the dotplot use the Blosum65  matrix as 
 * substitution matrix.
 * 
 * @author Andreas Held
 *
 */
public class Blosum65MatrixAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		if (!getState()) {
			setState(!getState());
		} else {
			DotplotView view = ViewHandler.getInstance().getTool(ViewType.DOTPLOT);
			view.getDotplotLayoutManager().setIdentityFalse();
			view.getDotplotComponent().getDotplot().setSubstitutionMatrix(MatrixType.BLOSUM65);
			view.repaint();
		}
	}

}
