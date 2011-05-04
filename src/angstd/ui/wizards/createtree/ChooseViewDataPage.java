package angstd.ui.wizards.createtree;

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

import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.wizards.GUIComponentFactory;

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
		
		selectSeqViewList = GUIComponentFactory.createSelectSeqViewBox(true);
		selectDomViewList = GUIComponentFactory.createSelectDomViewBox(true);
		
		selectSeqViewList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectSeqViewList.getSelectedItem() != null && selectDomViewList.getSelectedItem() != null)
					selectDomViewList.setSelectedItem(null);	
			}
		});
		
		selectDomViewList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectDomViewList.getSelectedItem() != null && selectSeqViewList.getSelectedItem() != null)
					selectSeqViewList.setSelectedItem(null);
			}
		});

		useUnderlayingSeqs = new JCheckBox();
		useUnderlayingSeqs.addItemListener(this);
		
		selectSeqViewList.setName(CreateTreeBranchController.SEQUENCEVIEW_KEY);
		selectDomViewList.setName(CreateTreeBranchController.DOMVIEW_KEY);
		useUnderlayingSeqs.setName(CreateTreeBranchController.USEUNDERLYINGSEQS_KEY);

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
    protected String validateContents (Component component, Object o) {
    	if (selectSeqViewList.getSelectedItem() == null && selectDomViewList.getSelectedItem() == null)
			return "Please select a sequence or domain view";
        return null;
    }
    
    /**
     * controlling method when the checkbox item is triggered.
     */
	public void itemStateChanged(ItemEvent e) {
	    JCheckBox source = (JCheckBox) e.getItemSelectable();

	    // check if there are sequences associated with the view
	    if (e.getStateChange() == ItemEvent.SELECTED) {
	    	ViewElement view = (ViewElement) selectDomViewList.getSelectedItem();
	    	
	    	if (view == null) {
	    		MessageUtil.showWarning("Please select a view first");
	    		source.setSelected(false);
	    		return;
	    	}
	    	
	    	DomainViewI domView = ViewHandler.getInstance().getView(view.getViewInfo());
	    	if (!domView.isSequenceLoaded()) {
	    		MessageUtil.showWarning("No sequences associated with this view");
	    		source.setSelected(false);
	    		return;
	    	}
	    }
	}

}
