package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.SearchProteinTool;


/**
 * Action which focus the scroll bar 
 * to the required protein
 * 
 * @author Nicolas Terrapon
 *
 */

public class SearchProteinAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	protected SearchProteinTool searchProtTool = null;

	@Override
	public void actionPerformed(ActionEvent e) {

		DomainViewI view = ViewHandler.getInstance().getActiveView();
		ViewType type = view.getViewInfo().getType();

		if (searchProtTool == null)
			searchProtTool=new SearchProteinTool(view);
		searchProtTool.showDialog(DoMosaicsUI.getInstance(), "Find protein");
		
	}		
}
