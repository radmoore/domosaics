package angstd.ui.wizards.importdata;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.util.FileDialogs;
import angstd.ui.wizards.GUIComponentFactory;

/**
 * WizardPage allowing to choose a file which is used to create a sequence view.
 * Additionally a domain view can be chosen which then will be used to associate the
 * sequences with it.
 * 
 * @author Andreas Held
 *
 */
public class SelectSequenceDataPage extends WizardPage implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the text field displaying the chosen file path */
	protected JTextField path;
	
	/** the text field for choosing a view name */
	protected JTextField viewName;

	
	/**
	 * Constructor for a new SelectSequenceDataPage
	 */
	public SelectSequenceDataPage() {
		super("Select Sequence Data");
		setLayout(new MigLayout());
		
		// init components
		path = new JTextField(20);
		path.setEditable(false);
		
		viewName = new JTextField(20);
		viewName.setEditable(true);
		
		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);	
		JComboBox selectViewList = GUIComponentFactory.createSelectDomViewBox(false);
		
		// associate names
		path.setName(ImportDataBranchController.FILEPATH_KEY);
		viewName.setName(ImportDataBranchController.VIEWNAME_KEY);
		selectViewList.setName(ImportDataBranchController.DOMVIEW_KEY);
		
		// layout panel
		add(new JXTitledSeparator("Select sequence file"),"growx, span, wrap");
		add(new JLabel("Select file: "), 	"gap 10");
		add(path, 							"gap 10");
		add(browse, 						"gap 10, wrap");
		
		add(new JLabel("Select the views name:"), "gap 10");
		add(viewName, "gap 10, gaptop 5, wrap");
		
		add(new JXTitledSeparator("Associate with arrangement view"),"growx, span, wrap, gaptop 25");
		add(new JLabel("Select view: "), 	"gap 10");
		add(selectViewList, 				"gap 10, span, wrap");
	}
	
	/**
	 * Action performed when the browse button was clicked
	 */
	public void actionPerformed(ActionEvent e) {	
		File file = FileDialogs.showOpenDialog(this);
		if(file != null) {
			path.setText(file.getAbsolutePath());
			if (viewName.getText().trim().isEmpty())
				viewName.setText(file.getName().split("\\.")[0]);
		}
	}	

	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Select Sequence Data";
    }
    
    /**
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
    	if (path.getText().isEmpty())
			return "Please select a file";
    	if (viewName.getText().trim().isEmpty())
    		return "Please select a view name";
        return null;
    }
    
}
