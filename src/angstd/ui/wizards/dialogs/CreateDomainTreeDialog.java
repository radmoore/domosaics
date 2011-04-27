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
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.views.ViewType;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.wizards.pages.CreateDomTreePage;

public class CreateDomainTreeDialog {

	public static Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new CreateDomTreePage()}, new CreateDomTreeProgress());
		return WizardDisplayer.showWizard(wiz);				 
	}
}

class CreateDomTreeProgress extends DeferredWizardResult implements WizardResultProducer{
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		// create a domainTree
		p.setBusy("Creating Domain Tree");
		
		try{
			// get selected tree view
			ViewElement treeElem = (ViewElement) m.get(CreateDomTreePage.TREEVIEW_KEY);
			TreeViewI treeView =  ViewHandler.getInstance().getView(treeElem.getViewInfo());
			
			// get selected domain view
			ViewElement domElem = (ViewElement) m.get(CreateDomTreePage.DOMVIEW_KEY);
			DomainViewI domView =  ViewHandler.getInstance().getView(domElem.getViewInfo());
			
			// create domain tree view
			String viewName = domView.getViewInfo().getName()+"_tree";
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, viewName);
			domTreeView.setBackendViews(treeView, domView);
			ViewHandler.getInstance().addView(domTreeView, null);
			
			p.finished(domTreeView);
			return;
		}
		catch(Exception e){
			Configuration.getLogger().debug(e.toString());
			p.failed("Error while creating DomainTree view, please try again.", false);
		}

		p.finished(null);
	}
	
	@SuppressWarnings("unchecked")
	public boolean cancel(Map m) {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Object finish(Map m) throws WizardException {
		return this;
	}	
}
