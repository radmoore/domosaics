package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.actions.ShowShapesAction;
import domosaics.ui.views.domainview.components.ShapeChooser;




/**
 * Action which opens the shape chooser dialog. The managing of this
 * process is done by the opening ShapeChooserDIalog. If the flag
 * "showShapes" is not enabled, it enables automatically.
 * 
 * @author Andreas Held
 *
 */
public class ChangeShapeAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ChangeShapeAction (){
		super();
		putValue(Action.NAME, "Change Shape");
		putValue(Action.SHORT_DESCRIPTION, "Changes the domains shape");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// dataset must be loaded because its a domain pop up action
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();

		// automatic enabling of show shapes.
		if (!view.getDomainLayoutManager().isShowShapes()) {
			view.getDomainLayoutManager().fireStructuralChange();
			view.getViewInfo().getActionManager().getAction(ShowShapesAction.class).setState(true);
		}
		
		// open shape chooser the rest is handled in there
		ShapeChooser sc = new ShapeChooser(view);
		sc.showDialog(DoMosaicsUI.getInstance(), "Shape Chooser");
	}
	
}