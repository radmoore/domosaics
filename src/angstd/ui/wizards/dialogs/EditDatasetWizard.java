package angstd.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.File;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.io.ArrangementImporterUtil;
import angstd.model.arrangement.io.XdomReader;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.wizards.pages.EditDatasetPage;

/**
 * Wizard dialog asking the user for arrangements which are the going to be
 * added to the data set.
 * 
 * @author Andreas Held
 *
 */
public class EditDatasetWizard {
	
	/** the view which is going to be changed */
	protected DomainViewI view;
	
	/**
	 * Constructor for a new EditDatasetWizard
	 * 
	 * @param view
	 * 		the view which is going to be changed
	 */
	public EditDatasetWizard(DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * Shows the wizard
	 *	
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new EditDatasetPage()}, new EditDatasetProgress(view));
		return WizardDisplayer.showWizard(wiz);	
	}
}

/**
 * ResultProducer processing the wizards information and modifying 
 * the data set
 * 
 * @author Andreas Held
 *
 */
@SuppressWarnings("unchecked")
class EditDatasetProgress extends DeferredWizardResult implements WizardResultProducer{
	
	/** the view which is going to be changed */
	protected DomainViewI view;
	
	
	/**
	 * Constructor for a new EditDatasetProgress
	 * 
	 * @param view
	 * 		the view which is going to be changed
	 */
	public EditDatasetProgress(DomainViewI view) {
		this.view = view;
	}
	
	
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		try{
			p.setBusy("Edit Data Set");
			
			ViewElement viewElt = (ViewElement) m.get(EditDatasetPage.DOMVIEW_KEY);
		
			// add arrangements from file
			if (viewElt == null) { 
				File file = new File((String) m.get(EditDatasetPage.FILEPATH_KEY));
				
				// GLOBAL ARRANGEMENT PARSING METHOD for files
				DomainArrangement[] daSet = ArrangementImporterUtil.importData(file);
				
//				DomainArrangement[] daSet = new XdomReader().getDataFromFile(file);
				if (daSet == null) {
					p.failed("Error while parsing file.", true);
					return;
				}
				
				// add successively the arrangements to the dataset
				for (DomainArrangement da : daSet)
					view.addArrangement(da);
				
				p.finished(null);
				return;
			}
				
			// OR merge views
			DomainViewI mergeView =  ViewHandler.getInstance().getView(viewElt.getViewInfo());
			DomainArrangement[] daSet = mergeView.getDaSet();
			
			// add successively the arrangements to the dataset
			for (DomainArrangement da : daSet) {
				view.addArrangement((DomainArrangement) da.clone());
			}
				
			if((Boolean) m.get(EditDatasetPage.DELETE_KEY))
				ViewHandler.getInstance().removeView(mergeView.getViewInfo());

			p.finished(null);		
		}catch(Exception e){
			p.failed("Error while editing data set.", false);
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
