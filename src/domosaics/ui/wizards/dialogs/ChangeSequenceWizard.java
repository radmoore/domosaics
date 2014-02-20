package domosaics.ui.wizards.dialogs;

import java.awt.EventQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainVector;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.sequence.util.SeqUtil;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.view.manager.SelectionManager;
import domosaics.ui.wizards.pages.ChangeSequencePage;




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
		Wizard wiz = WizardPage.createWizard(new WizardPage[]{new ChangeSequencePage(initSeq, selectedDA.getDomainArrangement())}, new ChangeSequenceProgress(view, selectedDA));
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
class ChangeSequenceProgress extends DeferredWizardResult implements WizardResultProducer{
	
	/** the view which is going to be changed */
	protected DomainViewI view;
	
	/** the arrangement to be changed */
	protected ArrangementComponent selectedDA;
	
	private DomainArrangement selectedArr;
	
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
	
	
	public void start(Map m, ResultProgressHandle p) {
		assert !EventQueue.isDispatchThread();
		
		try{
			p.setBusy("Changing sequence");
			
			String fastaSeq = (String) m.get(ChangeSequencePage.SEQ_KEY);
			fastaSeq=fastaSeq.replaceAll("\\s", "");
			fastaSeq=fastaSeq.replace("-", "");
	    	// delete sequence from arrangement if there is no sequence anymore
	    	if (fastaSeq.isEmpty()) {
	    		selectedDA.getDomainArrangement().setSequence(null);
	    		if (view.getSequences().length == 0)
	    			view.setSequencesLoaded(false);
				
	    		p.finished(null);
	    		return;
	    	}
			
	    	// check if the sequence is real
    		if ( SeqUtil.checkFormat(fastaSeq) == SeqUtil.UNKNOWN ) {
    				MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Cannot determine sequence format");
    				return;
    		}
	    	
    		// get arrangement of component
			selectedArr = selectedDA.getDomainArrangement();


        	DomainVector doms = selectedArr.getDomains();
        	Domain dom;
        	DomainComponent dc;
        	boolean remove = false;

    		// check if arrangement is effected by the sequence change
        	//
        	// taking only the last domain is not reasonable,
        	// as a domain may be in a domain. We have to check
        	// if there is _any_ domain with a from pos.
        	// that extends beyond the end of the sequence
        	// (compareTo() sorts by first position)
        	
        	// for each domain in the arrangement
    		for (int i = doms.size()-1; i >= 0; i--) {

    			dom = doms.get(i);
    			
   				// figure out whether it extends beyond 
   				// sequence length
    			if (dom.getTo() > fastaSeq.length()) {
    				
    				// if so, comunicate (and dont ask again)
    				if (!remove) {
    					if (MessageUtil.showDialog(DoMosaicsUI.getInstance(),"The sequence is too short. Remove effected domains?") )
    						remove = true;
    				
    					else {
    						cancel(m);
        					//p.failed("Sequence too short", false);
        					p.finished(null);
        					return;
    					}
    				}
        	
        			// if chosen, get the graphical domain coponent
        			dc = selectedDA.getDomain(dom);
        			// remove the component
        			dc.setVisible(false);
        			// and remove the underlying domain
        			doms.remove(dom);
        		}
        		
    		}
    		DomainVector hiddendoms = selectedArr.getHiddenDoms();
    		for (int i = hiddendoms.size()-1; i >= 0; i--) {
    			dom = hiddendoms.get(i);
   				// figure out whether it extends beyond 
   				// sequence length
    			if (dom.getTo() > fastaSeq.length()) {
    				if(!remove)
    					if (MessageUtil.showDialog(DoMosaicsUI.getInstance(),"Some hidden domains go beyond new sequence length. Remove effected domains?") )
    						remove = true;
    					else {	
    						cancel(m);
    						//p.failed("Sequence too short", false);
    						p.finished(null);
    						return;
    					}
        			hiddendoms.remove(dom);
        		}        		
    		}
    		Collections.sort(hiddendoms);
        	
    		
    		// resort after meddling (likely not needed - but see bug
    		// related to domain position)
    		Collections.sort(doms);
        	
    		
        	// if all is well, set the new sequence... 
		    SequenceI seq = new FastaReader().getDataFromString(fastaSeq)[0];
		    if(seq!=null) {

		    	if (seq.getName() == null)
		    		seq.setName(selectedArr.getName());

		    	// ... and flag that manual modification occured
		    	if(!selectedArr.getSequence().getSeq(false).equals(seq.getSeq(false))) {
		    		selectedArr.seqModifiedManually();
		    	}

		    	selectedArr.setSequence(seq);
		    	if(!view.isSequenceLoaded())
		    		view.setSequencesLoaded(true);

		    	// ... and update the graphical backbone
		    	selectedDA.setBounds(selectedDA.getX(), selectedDA.getY(), fastaSeq.length(), selectedDA.getHeight());

		    	// finally, trigger update to view
		    	DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		    	view.getDomainLayoutManager().firevisualChange();
		    }
		    p.finished(null);
		    return;
    		
		}
		catch(Exception e){
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			//p.failed("Error while editing data set.", false);
			p.finished(null);
		}
		
		p.finished(null);
		return;
	}
	
	public boolean cancel(Map m) {
		return true;
	}
	
	public Object finish(Map m) throws WizardException {
		return this;
	}	
	
	private void assignSequence(String sequence) {
		
	}
	
}
