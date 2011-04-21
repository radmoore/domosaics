package angstd.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.wizards.pages.ChangeSequencePage;

public class ChangeSequenceWizard {
	
	/** the view which is going to be changed */
	protected DomainViewI view;
	
	/** the arrangement to be changed */
	protected ArrangementComponent selectedDA;
	
	
	/**
	 * Constructor for a new ChangeSequenceWizard
	 * 
	 * @param view
	 * 		the view which is going to be changed
	 * @param selectedDA 
	 * 		the arrangement to be changed
	 */
	public ChangeSequenceWizard(DomainViewI view, ArrangementComponent selectedDA) {
		this.view = view;
		this.selectedDA = selectedDA;
	}
	
	/**
	 * Shows the wizard
	 *	
	 */
	public Object show() {
		String initSeq = (selectedDA.getDomainArrangement().hasSeq()) ? selectedDA.getDomainArrangement().getSequence().getSeq(false) : "";
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new ChangeSequencePage(initSeq)}, new ChangeSequenceProgress(view, selectedDA));
		return WizardDisplayer.showWizard(wiz, new Rectangle (20, 20, 720, 400));	
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
class ChangeSequenceProgress extends DeferredWizardResult implements WizardResultProducer{
	
	/** the view which is going to be changed */
	protected DomainViewI view;
	
	/** the arrangement to be changed */
	protected ArrangementComponent selectedDA;
	
	/**
	 * Constructor for a new ChangeSequenceProgress
	 * 
	 * @param view
	 * 		the view which is going to be changed
	 * @param selectedDA 
	 * 		the arrangement to be changed
	 */
	public ChangeSequenceProgress(DomainViewI view, ArrangementComponent selectedDA) {
		this.view = view;
		this.selectedDA = selectedDA;
	}
	
	
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		try{
			p.setBusy("Changing sequence");
			
			String fastaSeq = (String) m.get(ChangeSequencePage.SEQ_KEY);
			
			// delete sequence from arrangement
			if (fastaSeq.isEmpty()) {
				selectedDA.getDomainArrangement().setSequence(null);
				if (view.getSequences().length == 0)
					view.setSequencesLoaded(false);
				
				p.finished(null);
				return;
			}
			
			SequenceI seq = new FastaReader().getDataFromString(fastaSeq)[0];
			if (seq.getName() == null)
				seq.setName(selectedDA.getDomainArrangement().getName());
			
			selectedDA.getDomainArrangement().setSequence(seq);
			if(!view.isSequenceLoaded())
				view.setSequencesLoaded(true);

			p.finished(null);		
		}
		catch(Exception e){
			Configuration.getLogger().debug(e.toString());
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
