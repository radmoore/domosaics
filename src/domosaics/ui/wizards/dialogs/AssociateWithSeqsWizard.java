package domosaics.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.io.File;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.workspace.ViewElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.wizards.pages.AssociateWithSeqsPage;
import domosaics.ui.wizards.pages.EditDatasetPage;




public class AssociateWithSeqsWizard {
	
	/** the view which is going to be changed */
	protected DomainViewI view;
	
	/**
	 * Constructor for a new EditDatasetWizard
	 * 
	 * @param view
	 * 		the view which is going to be changed
	 */
	public AssociateWithSeqsWizard(DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * Shows the wizard
	 *	
	 */
	public Object show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new AssociateWithSeqsPage()}, new AssociateWithSeqsProgress(view));
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
class AssociateWithSeqsProgress extends DeferredWizardResult implements WizardResultProducer{
	
	/** the view which is going to be changed */
	protected DomainViewI view;
	
	
	/**
	 * Constructor for a new EditDatasetProgress
	 * 
	 * @param view
	 * 		the view which is going to be changed
	 */
	public AssociateWithSeqsProgress(DomainViewI view) {
		this.view = view;
	}
	
	
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		try{
//			p.setBusy("Associate with sequences");
			
			ViewElement viewElt = (ViewElement) m.get(AssociateWithSeqsPage.SEQVIEW_KEY);

			// add arrangements from file
			if (viewElt == null) { 
				File file = new File((String) m.get(AssociateWithSeqsPage.FILEPATH_KEY));
				SequenceI[] seqs = new FastaReader().getDataFromFile(file);
				if (seqs == null) {
					p.failed("Error while parsing file.", true);
					return;
				}
				
				view.loadSequencesIntoDas(seqs, view.getDaSet(), true);
				
				p.finished(null);
				return;
			}
				
			// OR merge views
			SequenceView assocView =  ViewHandler.getInstance().getView(viewElt.getViewInfo());
			SequenceI[] seqs = assocView.getSequences();
			view.loadSequencesIntoDas(seqs, view.getDaSet(), true);
			
				
			if((Boolean) m.get(EditDatasetPage.DELETE_KEY))
				ViewHandler.getInstance().removeView(assocView.getViewInfo());

			p.finished(null);		
		}
		catch(Exception e){
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			p.failed("Error while editing data set.", false);
			p.finished(null);
		}	
	}
	
	@Override
	public boolean cancel(Map m) {
		return true;
	}
	
	@Override
	public Object finish(Map m) throws WizardException {
		return this;
	}	
}
