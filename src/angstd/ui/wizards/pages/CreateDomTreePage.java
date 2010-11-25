package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.wizards.GUIComponentFactory;

public class CreateDomTreePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	/** the key used to access the domain view after the wizard finished */
	public static final String DOMVIEW_KEY = "domview";
	
	/** the key used to access the domain view after the wizard finished */
	public static final String TREEVIEW_KEY = "treeview";
	
	protected static final Dimension p_size = new Dimension(350,300);

	protected JComboBox selectTreeViewList;
	protected JComboBox selectDomViewList;

	
	public CreateDomTreePage() {
		setLayout(new MigLayout());
		
		selectTreeViewList = GUIComponentFactory.createSelectTreeViewBox(true);
		selectDomViewList = GUIComponentFactory.createSelectDomViewBox(true);
		
		selectTreeViewList.setName(TREEVIEW_KEY);
		selectDomViewList.setName(DOMVIEW_KEY);
		
		setPreferredSize(p_size);

		add(new JXTitledSeparator("Select tree view"),"growx, span, wrap, gaptop 40");
		add(new JLabel("Select view:"), "gap 10");
		add(selectTreeViewList, 		"gap 10, span, growx, wrap");
		
		add(new JXTitledSeparator("Select arrangement view"),"growx, span, wrap, gaptop 40");
		add(new JLabel("Select view:"), "gap 10");
		add(selectDomViewList, 			"gap 10, span, growx, wrap");
		
		
	}

    public static final String getDescription() {
        return "Select Tree And Arrangement View";
    }
    
    protected String validateContents (Component component, Object o) {
    	if (selectTreeViewList.getSelectedItem() == null)
			return "Please select a tree view";
    	else if (selectDomViewList.getSelectedItem() == null)
			return "Please select an arrangement view";

        return null;
    }
	
	
}
