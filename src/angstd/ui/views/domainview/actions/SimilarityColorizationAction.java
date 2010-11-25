package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainViewI;

/**
 * Starts the Similarity collapsing manager.
 * 
 * @author Andreas Held
 *
 */
public class SimilarityColorizationAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
	
		if (view.getArrangementSelectionManager().getSelection().size() != 1) {
			MessageUtil.showWarning("Please select one arrangement as reference for the similarity calculation");
			setState(!getState());
			view.getDomainLayoutManager().visualChange();
			return;
		}
		
		view.getDomainLayoutManager().toggleCollapseBySimilarity();
		
		view.getDomainSimilarityManager().start(view);
	}

}
