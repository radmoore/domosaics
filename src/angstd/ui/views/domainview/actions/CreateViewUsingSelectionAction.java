package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.view.View;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.pages.SelectNamePage;

/**
 * Creates a new view out of the current selected arrangements. Therefore
 * the selected arrangements as well as their associated sequences are 
 * cloned. 
 * The new view is independent (except that by default its added to the active view).
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class CreateViewUsingSelectionAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		// get a grip on the active domain view
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();

		// check the number of selected proteins, if its zero warn the user
		int numDAs = view.getArrangementSelectionManager().getSelection().size();
		if (numDAs == 0) {
			MessageUtil.showWarning("No proteins selected, please select at least one arrangement");
			return;
		}
		
		// take the active viewName + subset as default name
		String defaultName = view.getViewInfo().getName()+"_subset";
		String viewName, projectName;
		
		// get currently active project
		ViewElement elem = WorkspaceManager.getInstance().getViewElement(view.getViewInfo());
		ProjectElement project = elem.getProject();
		
		// get info provided by the user
		Map m = WizardManager.getInstance().selectNameWizard(defaultName, "domain view", project);
		viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
		projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
		project = WorkspaceManager.getInstance().getProject(projectName);
		
		
		// ask the user to enter a valid name for the view
		if (viewName == null) 
			return;
		
		// clone selected arrangements as well as their sequences into a new dataset
		DomainArrangement[] daSet = new DomainArrangement[numDAs];
		List<SequenceI> seqs = new ArrayList<SequenceI>();
		int i = 0;
		Iterator<ArrangementComponent> iter = view.getArrangementSelectionManager().getSelectionIterator();
		while (iter.hasNext()) {
			try {
				DomainArrangement da =  iter.next().getDomainArrangement();
				if (da.getSequence() != null)
					seqs.add((Sequence) da.getSequence().clone());
				daSet[i] = (DomainArrangement)da.clone();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			i++;
		}
		
		// clear the selection from this view
		view.getArrangementSelectionManager().clearSelection();

		// and create with the new dataset a new domainview with the selected name
		DomainViewI newView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		
		newView.setDaSet(daSet);
		
		//if there are sequences loaded clone them as well
		if (view.isSequenceLoaded()) 
			newView.loadSequencesIntoDas(seqs.toArray(new SequenceI[seqs.size()]), newView.getDaSet());
		
		ViewHandler.getInstance().addView(newView, project);
	}
	
}
