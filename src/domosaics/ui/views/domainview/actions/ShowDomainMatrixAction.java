package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domainmatrix.DomainMatrixView;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;




/**
 * Action which opens a domain matrix.
 * 
 * @author Andreas Held
 *
 */
public class ShowDomainMatrixAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		DomainMatrixView view = ViewHandler.getInstance().createTool(ViewType.DOMAINMATRIX);
		view.setData(domView, domView.getDaSet());
		ViewHandler.getInstance().addTool(view);

	}

}
