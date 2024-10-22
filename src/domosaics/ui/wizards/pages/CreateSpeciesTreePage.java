package domosaics.ui.wizards.pages;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.ui.wizards.GUIComponentFactory;




public class CreateSpeciesTreePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	/** the key used to access the domain view after the wizard finished */
	public static final String DOMVIEW_KEY = "domview";
	
	/** size of the page */
	//private final static Dimension p_size = new Dimension(300,200);
	
	protected JComboBox selectDomViewList;

	
	public CreateSpeciesTreePage() {
		setLayout(new MigLayout());
		
		selectDomViewList = GUIComponentFactory.createSelectDomViewBox(true);
		selectDomViewList.setName(DOMVIEW_KEY);
		
		//setPreferredSize(p_size);
		
		add(new JXTitledSeparator("Select arrangement view"),"growx, span, wrap, gaptop 10");
		add(new JLabel("Select view:"), "gap 10");
		add(selectDomViewList, 			"w 270!, gap 5, gapright 10,span, growx, wrap");
		add(new JLabel(""), 		"h 161!, gap 5, wrap");
		
	}


    public static final String getDescription() {
        return "Select view for species tree creation";
    }
    
    @Override
	protected String validateContents (Component component, Object o) {
    	if (selectDomViewList.getSelectedItem() == null)
    		return "Please select domain view";

        return null;
    }
}
