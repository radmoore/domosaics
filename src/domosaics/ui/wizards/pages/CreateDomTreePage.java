package domosaics.ui.wizards.pages;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.model.workspace.ViewElement;
import domosaics.model.tree.Tree;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.wizards.GUIComponentFactory;




public class CreateDomTreePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	/** the key used to access the domain view after the wizard finished */
	public static final String DOMVIEW_KEY = "domview";
	
	/** the key used to access the domain view after the wizard finished */
	public static final String TREEVIEW_KEY = "treeview";
	
	public static final String PROJECT_KEY = "project";
	
	public static final String DOMVIEWNAME_KEY = "domviewname";
	
	//protected static final Dimension p_size = new Dimension(300,200);

	protected JComboBox selectTreeViewList, selectDomViewList, selectProjectList;
	
	
	public CreateDomTreePage() {

		setLayout(new MigLayout());
		
		selectTreeViewList = GUIComponentFactory.createSelectTreeViewBox(true);
		selectDomViewList = GUIComponentFactory.createSelectDomViewBox(true);
		selectProjectList = GUIComponentFactory.createSelectProjectBox(null);
		
		selectTreeViewList.setName(TREEVIEW_KEY);
		selectDomViewList.setName(DOMVIEW_KEY);
		selectProjectList.setName(PROJECT_KEY);
		
		//setPreferredSize(p_size);

		add(new JXTitledSeparator("Select tree view"),"growx, span, wrap, gaptop 10");
		add(new JLabel("Select view:"), "gap 10");
		add(selectTreeViewList, 		"w 270!, gap 5, gapright 10, span, growx, wrap");
		
		add(new JXTitledSeparator("Select arrangement view"),"growx, span, wrap, gaptop 35");
		add(new JLabel("Select view:"), "gap 10");
		add(selectDomViewList, 			"w 270!, gap 5, gapright 10, span, growx, wrap");
		add(new JLabel(""), 		"h 85!, gap 5, wrap");
		
		
	}

    public static final String getDescription() {
        return "Select Tree And Arrangement View";
    }
    
    protected String validateContents (Component component, Object o) {
    	if (selectTreeViewList.getSelectedItem() == null)
			return "Please select a tree view";
    	else if (selectDomViewList.getSelectedItem() == null)
			return "Please select an arrangement view";
    	else {
    		ArrayList<String> treeLabels = ((Tree)((TreeViewI)ViewHandler.getInstance().getView(((ViewElement)selectTreeViewList.getSelectedItem()).getViewInfo())).getTree()).getLeavesName();
    		ArrayList<String> arrLabels = ((DomainView)ViewHandler.getInstance().getView(((ViewElement)selectDomViewList.getSelectedItem()).getViewInfo())).getLabels();
    		int mem = treeLabels.size();
    		treeLabels.retainAll(arrLabels);
    		if(treeLabels.size()==0) {
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Domain and tree views do not have any common sequence!");
				return "Please select corresponding views";  
    		} else {
    			if(treeLabels.size()!=arrLabels.size() || treeLabels.size()!=mem)
    				MessageUtil.showInformation(DoMosaicsUI.getInstance(),"Protein sets in domain and tree views do not perfectly overlap!");    			
    		}
    	}
        return null;
    }
	
	
}
