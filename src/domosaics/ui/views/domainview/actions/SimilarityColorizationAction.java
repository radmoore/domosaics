package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;




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
		if(view.getDomainLayoutManager().isCollapseBySimilarity()) {
			if (view.getArrangementSelectionManager().getSelection().size() != 1) {
				if(view.getDomainLayoutManager().isSelectSequences())
				{
					view.getDomainLayoutManager().toggleSelectArrangements();
					view.getSequenceSelectionMouseController().clearSelection();
					view.registerMouseListeners();
				}
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please select one arrangement as reference for the similarity calculation");
				setState(!getState());
				view.getDomainLayoutManager().visualChange();
				return;
			}
			view.getDomainSimilarityManager().start(view);
		} else {
			view.getDomainSimilarityManager().end(view);
		}
	}

}
