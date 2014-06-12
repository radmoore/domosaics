package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.EvalueSliderTool;




public class ShowEvalueSliderAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	protected EvalueSliderTool evalueTool = null;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (evalueTool == null) {
			DomainViewI view = ViewHandler.getInstance().getActiveView();
			evalueTool = new EvalueSliderTool(view);
			evalueTool.showDialog(DoMosaicsUI.getInstance(), "Evalue Resolver");
		} else
		{
			evalueTool.setVisible(true);
		}
	}

}
