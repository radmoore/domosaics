package angstd.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.ViewType;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.pages.CreateDomTreePage;
import angstd.ui.wizards.pages.SelectNamePage;

public class CreateDomainTreeDialog {

	public static Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new CreateDomTreePage()}, new CreateDomTreeProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class CreateDomTreeProgress extends DeferredWizardResult implements WizardResultProducer{
	
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		// create a domainTree
		p.setBusy("Creating Domain Tree");
		
		try{
			
			ProjectElement project = null;
			
			// get selected tree view
			ViewElement treeElem = (ViewElement) m.get(CreateDomTreePage.TREEVIEW_KEY);
			TreeViewI treeView =  ViewHandler.getInstance().getView(treeElem.getViewInfo());
			String treeProjectName = treeElem.getProject().getTitle();
			
			// get selected domain view
			ViewElement domElem = (ViewElement) m.get(CreateDomTreePage.DOMVIEW_KEY);
			DomainViewI domView =  ViewHandler.getInstance().getView(domElem.getViewInfo());
			String domProjectName = domElem.getProject().getTitle();
			
			// if two fused views from same project, get that project 
			if ( domProjectName.equals(treeProjectName) )
				project = WorkspaceManager.getInstance().getProject(domProjectName);
			
			// create default name
			String viewName = domView.getViewInfo().getName()+"_tree";
			
			// ensure proposed name acceptable and doesnt exist
			// if project null, no project proposed
			Map results = WizardManager.getInstance().selectNameWizard(viewName, "domain tree view", project, true);
			
			// get selected view name and project name
			viewName = (String) results.get(SelectNamePage.VIEWNAME_KEY);
			String projectName = (String) results.get(SelectNamePage.PROJECTNAME_KEY);
			
			// get project
			project = WorkspaceManager.getInstance().getProject(projectName);
			
			// create domain tree view		
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, viewName);
			domTreeView.setBackendViews(treeView, domView);
			
			// and add to selected project
			ViewHandler.getInstance().addView(domTreeView, project);
			
			p.finished(domTreeView);
			return;
		}
		catch(Exception e){
			Configuration.getLogger().debug(e.toString());
			p.failed("Error while creating DomainTree view, please try again.", false);
		}

		p.finished(null);
	}
	
	public boolean cancel(Map m) {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Object finish(Map m) throws WizardException {
		return this;
	}	
}
