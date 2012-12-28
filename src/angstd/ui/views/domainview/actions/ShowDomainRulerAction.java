package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.HelpManager;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;



/**
 * Actions which triggers the drawing of a amino acid ruler below
 * the rendered arrangements.
 * 
 * @author Andreas Held
 *
 */
public class ShowDomainRulerAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	/** message shown within a help dialog */
	private static final String HELPMSG = 
			"An amino acid ruler is drawn below all rendered arrangements.";

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		view.getDomainLayoutManager().fireStructuralChange();
		
		HelpManager.showHelpDialog("Show_Ruler", HELPMSG);
	}
	
}