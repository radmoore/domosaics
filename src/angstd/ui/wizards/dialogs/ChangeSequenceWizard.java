package angstd.ui.wizards.dialogs;

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

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainVector;
import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.model.sequence.util.SeqUtil;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.view.manager.SelectionManager;
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
			
	    	// delete sequence from arrangement if there is no sequence anymore
	    	if (fastaSeq.isEmpty()) {
	    		selectedDA.getDomainArrangement().setSequence(null);
	    		if (view.getSequences().length == 0)
	    			view.setSequencesLoaded(false);
				
	    		p.finished(null);
	    		return;
	    	}
			
			selectedArr = selectedDA.getDomainArrangement();

    		// check if arrangement is effected by the sequence change
        	DomainVector doms = selectedArr.getDomains();
        	Collections.sort(doms);
        	Domain dom = doms.lastElement();
        	System.out.println("This is the last domain: "+dom);
        	
        	if (dom.getTo() > fastaSeq.length()) {

        		if (MessageUtil.showDialog("The sequence is too short. Remove effected domains?")) {
    				DomainComponent dc;
    				// for each domain in the arrangement
        			for (int i = doms.size()-1; i >= 0; i--) {
        				dom = doms.get(i);
        				// figure out whether it extends beyond 
        				// sequence length
        				if (dom.getTo() > fastaSeq.length()) {
        					// if so, get the graphical domain coponent
        					dc = selectedDA.getDomain(dom);
        					// remove the component
        					dc.setVisible(false);
        					// and remove the underlying domain
        					doms.remove(dom);
        				}
        				else
        					break;
        			}
        			Collections.sort(doms);
    			}
    			else {
    				cancel(m);
    				p.failed("Sequence too short", false);
    				return;
    			}
    		}
        	// check if the sequence is real
    		else if ( SeqUtil.checkFormat(fastaSeq) == SeqUtil.UNKNOWN ) {
    			if (! MessageUtil.showDialog("Cannot determine sequence type. Continue?")) {
    				cancel(m);
    				p.failed("Could not determine sequence type", false);
    				return;
    			}
    		}
    		
        	// if all is well, set the new sequence... 
		    SequenceI seq = new FastaReader().getDataFromString(fastaSeq)[0];
		    if (seq.getName() == null)
		    	seq.setName(selectedDA.getDomainArrangement().getName());
			
		    selectedDA.getDomainArrangement().setSequence(seq);
		    if(!view.isSequenceLoaded())
		    	view.setSequencesLoaded(true);
		    
		    // ... and update the graphical backbone
			selectedDA.setBounds(selectedDA.getX(), selectedDA.getY(), fastaSeq.length(), selectedDA.getHeight());
		    		
			// finally, trigger update to view
	   		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
	   		view.getDomainLayoutManager().firevisualChange();
			
		    p.finished(null);
		    return;
    		
		}
		catch(Exception e){
			Configuration.getLogger().debug(e.toString());
			e.printStackTrace();
			p.failed("Error while editing data set.", false);
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
