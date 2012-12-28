package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.EvalueSliderTool;



public class ShowEvalueSliderAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;

	protected EvalueSliderTool evalueTool = null;
	
	public void actionPerformed(ActionEvent e) {
		if (evalueTool == null || !evalueTool.isVisible()) {
			DomainViewI view = ViewHandler.getInstance().getActiveView();
			evalueTool = new EvalueSliderTool(view);
			evalueTool.showDialog(AngstdUI.getInstance(), "Evalue Resolver");
		}
	}

}
