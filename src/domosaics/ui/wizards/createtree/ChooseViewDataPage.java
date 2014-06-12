package domosaics.ui.wizards.createtree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.model.workspace.ViewElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.wizards.GUIComponentFactory;
import domosaics.util.CheckConnectivity;




/**
 * WizardPage which allows the user to choose the backend data used to 
 * create the new tree (e.g. a sequence view, domain view or the 
 * associated sequences of a domain view).
 * 
 * @author Andreas Held
 *
 */
public class ChooseViewDataPage extends WizardPage implements ItemListener{
	private static final long serialVersionUID = 1L;

	/** list displaying all sequence views of all projects */
	protected JComboBox selectSeqViewList;
	
	/** list displaying all domain views of all projects */
	protected JComboBox selectDomViewList;
	
	/** check box indicating whether or not the associated sequences of the selected domain view should be used to create the tree  */
	protected JCheckBox useUnderlayingSeqs;

	
	/**
	 * Constructor for a new ChooseViewDataPage
	 */
	public ChooseViewDataPage() {
		setLayout(new MigLayout());
		
		useUnderlayingSeqs = new JCheckBox();
		useUnderlayingSeqs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (useUnderlayingSeqs.isSelected()) {
					ViewElement view = (ViewElement) selectDomViewList.getSelectedItem();
					DomainViewI domView = ViewHandler.getInstance().getView(view.getViewInfo());
					if ( !domView.isSequenceLoaded() ) {
						MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No underlying sequences in this domain arrangement view.");
						useUnderlayingSeqs.setSelected(false);
					} else
					{
						// If less than 2 sequences has sequences no treecould be computed based on sequences
						if(domView.getSequences().length<3) {
							MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Less than three arrangments have undermying sequences.");
							useUnderlayingSeqs.setSelected(false);
						} else
						{
							if(domView.getSequences().length!=domView.getDaSet().length)
								MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Not all arrangement have underlying sequences.");
						}
					}
				}
			}
		});
		useUnderlayingSeqs.setName(CreateTreeBranchController.USEUNDERLYINGSEQS_KEY);
		
		
		selectDomViewList = GUIComponentFactory.createSelectDomViewBox(true);
		selectDomViewList.setName(CreateTreeBranchController.DOMVIEW_KEY);
		selectDomViewList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (selectDomViewList.getSelectedItem() != null) {
					
					if (selectSeqViewList.getSelectedItem() != null)
						selectSeqViewList.setSelectedItem(null);
					
					ViewElement view = (ViewElement) selectDomViewList.getSelectedItem();
					DomainViewI domView = ViewHandler.getInstance().getView(view.getViewInfo());
					useUnderlayingSeqs.setEnabled(true);
				}
				else {
					useUnderlayingSeqs.setSelected(false);
					useUnderlayingSeqs.setEnabled(false);
				}
			
			}
			
		});
		
		selectSeqViewList = GUIComponentFactory.createSelectSeqViewBox(true);
		selectSeqViewList.setName(CreateTreeBranchController.SEQUENCEVIEW_KEY);
		selectSeqViewList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useUnderlayingSeqs.setSelected(false);
				useUnderlayingSeqs.setEnabled(false);
				if (selectSeqViewList.getSelectedItem() != null && selectDomViewList.getSelectedItem() != null)
					selectDomViewList.setSelectedItem(null);
			}
		});
		
		

		

		
		

		add(new JXTitledSeparator("Select arrangement view"),"growx, span, wrap");
		add(new JLabel("Select view:"), "gap 10");
		add(selectDomViewList, 			"w 270!, gap 5, gapright 10,span, growx, wrap");
		
		add(new JXTitledSeparator("Use underlying sequences instead of domains?"),"growx, span, wrap, gaptop 25");
		add(new JLabel("Use Sequences:"), "gap 10");
		add(useUnderlayingSeqs, 		"gap 5, wrap");
	
		add(new JXTitledSeparator("Select sequence view"),"growx, span, wrap, gaptop 25");
		add(new JLabel("Select view:"), "gap 10");
		add(selectSeqViewList, 		"w 270!, gap 5, gapright 10,span, growx, wrap");
		add(new JLabel(""), 		"h 21!, gap 10, wrap");
	}
	
	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Select view for tree creation";
    }
    
    /**
     * Checks if all necessary input is made.
     */
    @Override
	protected String validateContents (Component component, Object o) {
    	if (selectSeqViewList.getSelectedItem() == null && selectDomViewList.getSelectedItem() == null)
			return "Please select a sequence or domain view";
		if ((useUnderlayingSeqs.isSelected()  || (selectSeqViewList.getSelectedItem() != null && selectDomViewList.getSelectedItem() == null)) && !CheckConnectivity.checkInternetConnectivity()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please check your intenet connection (connection failed).");
			if(selectDomViewList.getSelectedItem() == null)
				return "Internet required for tree based on sequences";
		}
        return null;
    }
    
    /**
     * controlling method when the checkbox item is triggered.
     */
	@Override
	public void itemStateChanged(ItemEvent e) {
	    JCheckBox source = (JCheckBox) e.getItemSelectable();

	    // check if there are sequences associated with the view
	    if (e.getStateChange() == ItemEvent.SELECTED) {
	    	ViewElement view = (ViewElement) selectDomViewList.getSelectedItem();
	    	
	    	if (view == null) {
	    		MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Please select a view first");
	    		source.setSelected(false);
	    		return;
	    	}
	    	
	    	DomainViewI domView = ViewHandler.getInstance().getView(view.getViewInfo());
	    	if (!domView.isSequenceLoaded()) {
	    		MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No sequences associated with this view");
	    		source.setSelected(false);
	    		return;
	    	}
	    }
	}

}
