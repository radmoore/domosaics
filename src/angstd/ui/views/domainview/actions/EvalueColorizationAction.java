package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.HelpManager;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.DomainViewI;

/**
 * Actions which triggers the evalue colorization mode.
 * Each domain is colored transparently depending on its evalue.
 * 
 * @author Andreas Held
 *
 */
public class EvalueColorizationAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	/** message shown within a help dialog */
	private static final String HELPMSG = 
			"The domains transparency grade is related to its \n" +
			"expected value. If no domains are transparent either \n" +
			"the annotation is very good or no evalues were assigned. \n";
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();

		view.getDomainLayoutManager().toggleEvalueColorization();
		
		HelpManager.showHelpDialog("Evalue_colors", HELPMSG);
	}
}
