package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.Sequence;
import domosaics.model.sequence.SequenceI;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.ViewInfo;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.SelectViewPage;


/**
 * Creates a new view out of the current selected arrangements. Therefore
 * the selected arrangements as well as their associated sequences are 
 * cloned. 
 * The new view is independent (except that by default its added to the active view).
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class AddSelectionToViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		
		ViewType activeViewType = ViewHandler.getInstance().getActiveView().getViewInfo().getType();
		
		// this operation currently only works for pure domain views (not for trees / sequences)
		if (! activeViewType.equals(ViewType.DOMAINS)) {
			MessageUtil.showWarning("Only domain views can be merged");
			return;
		}
		
		DomainViewI activeDomView = (DomainViewI) ViewHandler.getInstance().getActiveView();

		// check the number of selected proteins, if its zero warn the user
		int numDAs = activeDomView.getArrangementSelectionManager().getSelection().size();
		if (numDAs == 0) {
			MessageUtil.showWarning("No proteins selected, please select at least one arrangement");
			return;
		}
		
		// get currently active project
		ViewElement elem = WorkspaceManager.getInstance().getViewElement(activeDomView.getViewInfo());
		ProjectElement project = elem.getProject();
		
		// get info provided by the user
		int selectedItems = activeDomView.getArrangementSelectionManager().getSelection().size();
		Map m = WizardManager.getInstance().selectViewWizard(project, selectedItems);
		
		// in case user canceled
		if (m == null) 
			return;
		
		// Find the target view based on user selection
		WorkspaceElement wsElem = (WorkspaceElement) m.get(SelectViewPage.VIEW_KEY);
		DomainViewI targetView = null; 
		ViewInfo domViewInfo = null;
		for (View dView : ViewHandler.getInstance().getViews()) {
			if ( dView.getViewInfo().getName().equals(wsElem.getTitle()) ) {
				targetView = (DomainViewI) dView;		
				break;
			}
		}
		
		
		// clone selected arrangements and sequences of selection
		DomainArrangement[] daSet = new DomainArrangement[numDAs];
		List<SequenceI> selectedDAseqs = new ArrayList<SequenceI>();
		int i = 0;
		Iterator<ArrangementComponent> iter = activeDomView.getArrangementSelectionManager().getSelectionIterator();
		while (iter.hasNext()) {
			try {
				DomainArrangement da =  iter.next().getDomainArrangement();
				if (da.getSequence() != null)
					selectedDAseqs.add((Sequence) da.getSequence().clone());
				daSet[i] = (DomainArrangement)da.clone();
			} 
			catch (Exception ex) {
				Configuration.getLogger().debug(ex.toString());
			}
			i++;
		}
		
		// create new set of sequences with all sequences (selection and target view)
		List<SequenceI> allSeqs = new ArrayList<SequenceI>();
		if (selectedDAseqs.size() > 0) {
			// from selection
			try {
			for (SequenceI s : selectedDAseqs) {
				allSeqs.add(s);
			}
			// from the targetView
			for (DomainArrangement da : targetView.getDaSet()) {
				if (da.getSequence() != null)
					allSeqs.add((Sequence) da.getSequence().clone());
			}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// clear the selection from this view
		activeDomView.getArrangementSelectionManager().clearSelection();
		// add arrangements to targetView
		targetView.addDaSet(daSet);
		
		// FIXME this is ineffcient, as we should only add
		// new sequences to targetView (as opposed to re-adding the existing ones as well)
		// This was done due to the relatively complex loadSequencesIntoDas method, which also
		// triggers the match sequence dialog if ambiguoity should occur - to avoid code duplication
		// all sequences are readded.
		
		// add all sequences to targetView (existing + selection)
		if (allSeqs != null || allSeqs.size() > 0 )
			targetView.loadSequencesIntoDas(allSeqs.toArray(new SequenceI[allSeqs.size()]), targetView.getDaSet());
		// Switch to new view containing imported arrangements
		WorkspaceManager.getInstance().showViewInMainFrame(WorkspaceManager.getInstance().getViewElement(targetView.getViewInfo()));
		// Set selection in target to selected arrangements (doesnt work)
		//viewToAddTo.getArrangementSelectionManager().setSelection(activeDomView.getArrangementSelectionManager().getSelection());
		MessageUtil.showInformation(null, "Added "+selectedItems+" arrangements from "+activeDomView.getViewInfo().getName());
		
	}
	
}
