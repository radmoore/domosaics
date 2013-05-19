package domosaics.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.algos.treecreation.SpeciesTreeCreationUtil;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;
import domosaics.model.tree.TreeI;
import domosaics.model.workspace.ViewElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.wizards.pages.CreateSpeciesTreePage;
import domosaics.webservices.pidparse.LineageUtil;




/**
 * Wizard dialog asking the user for a domain view which is 
 * used to create a species tree which is then added to the workspace.
 * 
 * @author Andreas Held
 *
 */
public class CreateSpeciesTreeDialog {

	/**
	 * Shows the wizard
	 * 
	 * @return
	 * 		the created species tree view
	 */
	public static Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new CreateSpeciesTreePage()}, new CreateSpeciesTreeProgress());
		return WizardDisplayer.showWizard(wiz);	
	}
}

/**
 * ResultProducer processing the wizards information and creating a new 
 * species tree which is added to the workspace.
 * 
 * @author Andreas Held
 *
 */
@SuppressWarnings("unchecked")
class CreateSpeciesTreeProgress extends DeferredWizardResult implements WizardResultProducer{
	
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		try{	
			// get the selected views data set.
			ViewElement viewElt = (ViewElement) m.get(CreateSpeciesTreePage.DOMVIEW_KEY);
			DomainViewI view =  ViewHandler.getInstance().getView(viewElt.getViewInfo());
			DomainArrangement[] daSet = view.getDaSet();
			int numProts = daSet.length;
				
			
			// retrieve lineages
			Map<String, String> name2lineage = new HashMap<String, String>(); // name 2 lineage
			for (int i = 0; i < daSet.length; i++) {
				String accsession = (daSet[i].getDesc() != null) ? daSet[i].getDesc().trim() : daSet[i].getName().trim();
				p.setProgress ("Try to retrieve lineage for: "+accsession, i, numProts);
				
				String lineage = LineageUtil.getLineage(accsession);
				if (lineage == null) {
					p.failed("Failed to create species tree. \n Could not retrieve lineage for "+accsession, false);
					return;
				}

				
				name2lineage.put(daSet[i].getName(), lineage);
			}
			
			TreeI tree = SpeciesTreeCreationUtil.createSpeciesTree(name2lineage);
	
			String treeViewName = "SpeciesTreeTest";
			TreeViewI treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
			treeView.setTree(tree);
			ViewHandler.getInstance().addView(treeView, null);

			DomainArrangement[] clonedSet = new DomainArrangement[view.getDaSet().length];
			try {
				for (int i = 0; i< clonedSet.length; i++)
					clonedSet[i] = (DomainArrangement) daSet[i].clone();
			} 
			catch (Exception e) {
				if (Configuration.getReportExceptionsMode(true))
					Configuration.getInstance().getExceptionComunicator().reportBug(e);
				else			
					Configuration.getLogger().debug(e.toString());
			}
			
			DomainViewI domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, treeViewName+"2");
			domView.setDaSet(clonedSet);
			ViewHandler.getInstance().addView(domView, null);
			
			String domTreeViewName = "SpeciesDomainTreeTest";
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, domTreeViewName);
			domTreeView.setBackendViews(treeView, domView);
			ViewHandler.getInstance().addView(domTreeView, null);
			
			// now change layout options for the new domaintree view to make it look cooler.
			domTreeView.getTreeLayoutManager().setExpandLeaves(false);
			domTreeView.getTreeLayoutManager().setUseDistances(true);
			
			domTreeView.getDomainLayoutManager().structuralChange();

			p.finished(domTreeView);
		}
		catch(Exception e){
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			p.failed("Error while creating species tree, please try again.", false);
			p.finished(null);
		}
	}
	
	public boolean cancel(Map m) {
		return true;
	}
	
	public Object finish(Map m) throws WizardException {
		return this;
	}	
}


