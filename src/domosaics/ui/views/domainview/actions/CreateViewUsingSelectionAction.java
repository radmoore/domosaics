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
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.view.View;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.SelectNamePage;

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
		String viewName = null;
		String projectName;
		
		// get currently active project
		ViewElement elem = WorkspaceManager.getInstance().getViewElement(view.getViewInfo());
		ProjectElement project = elem.getProject();
		
		while(viewName == null) {
			// get info provided by the user
			Map m = WizardManager.getInstance().selectNameWizard(defaultName, "domain view", project, true);

			// in case user canceled renaming
			if (m != null) {
				viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
				projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
				project = WorkspaceManager.getInstance().getProject(projectName);
			} else {
				return;
			}
		}
				
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
			} 
			catch (Exception ex) {
				if (Configuration.getReportExceptionsMode())
					Configuration.getInstance().getExceptionComunicator().reportBug(ex);
				else			
					Configuration.getLogger().debug(ex.toString());
			}
			i++;
		}
		
		// clear the selection from this view
		view.getDomainLayoutManager().deselectAll();
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
