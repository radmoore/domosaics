package domosaics.ui.wizards.dialogs;

import java.awt.EventQueue;
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
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.wizards.pages.ClustalW2Page;
import domosaics.webservices.clustalw.ClustalW2ResultParser;




/**
 * Wizard dialog allowing the user to perform a ClustalW2 alignment on
 * a set of sequences. Therefore the ebi webservice ClustalW2 is used.
 * 
 * @author Andreas Held
 *
 */
public class ClustalW2Dialog {

	/** the sequences to be aligned */
	protected SequenceI[] seqs = null;
	
	
	/**
	 * Constructor for a new ClustalW2Dialog
	 * 
	 * @param seqs
	 * 		the sequences to be aligned
	 */
	public ClustalW2Dialog(SequenceI[] seqs) {
		this.seqs = seqs;
	}
	
	/**
	 * Shows the dialog
	 * 
	 * @return
	 * 		aligned set of sequences
	 */
	public SequenceI[] show() {
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new ClustalW2Page(seqs)}, new ClustalW2Progress());
		return (SequenceI[]) WizardDisplayer.showWizard(wiz);				 
	}
}

@SuppressWarnings("unchecked")
class ClustalW2Progress extends DeferredWizardResult implements WizardResultProducer{
	
	@Override
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		try {
			String alignmentStr = (String) m.get(ClustalW2Page.ALIGNMENT_KEY);
			
			if (alignmentStr == null || alignmentStr.isEmpty()) {
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Alignment creation was not successful.");
				p.finished(null);
				return;
			}
			p.finished(new ClustalW2ResultParser().parseResult(alignmentStr));
		}
		catch(Exception e){
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			p.failed("Error while creating Project, please try again.", false);
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
