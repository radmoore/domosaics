package domosaics.ui.wizards.importdata;

import java.awt.Component;
import java.awt.Dimension;
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

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.io.ArrangementImporterUtil;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.wizards.GUIComponentFactory;




/**
 * WizardPage allowing to choose a file which is used to create a domain view.
 * Additionally a tree view can be chosen which then will be used to create a 
 * domain tree.
 * 
 * @author Andreas Held
 *
 */
public class SelectArrangementDataPage extends WizardPage implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the text field displaying the chosen file path */
	protected JTextField path;
	
	/** the text field for choosing a view name */
	protected JTextField viewName;
	
	/** the check for correct file format */
	DomainArrangement[] daSet = null;
	
	private ProjectElement project = null;
	
	/** size of the page */
	private final static Dimension p_size = new Dimension(400,300);
	
	
	/**
	 * Constructor for a new SelectArrangementDataPage
	 */
	public SelectArrangementDataPage() {
		super("Select Arrangement Data");
		init();
	}

	public SelectArrangementDataPage(ProjectElement project) {
		super("Select Arrangements file");
		this.project = project;
		init();
	}
	
	
	public void init() {
		
		setLayout(new MigLayout());
		setPreferredSize(p_size);
		
		// init components
		path = new JTextField(20);
		path.setEditable(false);
		
		viewName = new JTextField(20);
		viewName.setEditable(true);
	
		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);
		
		JComboBox selectTreeViewList;
		JComboBox selectSeqViewList;
		
		if (project == null) {
			selectTreeViewList = GUIComponentFactory.createSelectTreeViewBox(false);
			selectSeqViewList = GUIComponentFactory.createSelectSeqViewBox(false);
		}
		else {
			selectTreeViewList = GUIComponentFactory.createSelectTreeViewBox(project);
			selectSeqViewList = GUIComponentFactory.createSelectSeqViewBox(project);
		}
		
		// associate names
		path.setName(ImportDataBranchController.FILEPATH_KEY);
		viewName.setName(ImportDataBranchController.VIEWNAME_KEY);
		selectTreeViewList.setName(ImportDataBranchController.TREEVIEW_KEY);
		selectSeqViewList.setName(ImportDataBranchController.SEQVIEW_KEY);
		
		// layout panel
		add(new JXTitledSeparator("Select arrangement file"),"growx, span, wrap");
		add(new JLabel("Select file:"), "gap 10");
		add(path, "w 150!, h 25!, gap 5");
		add(browse, "wrap");
		
		//add(new JLabel("Enter a name:"), "gap 10");
		add(viewName, "w 50!, h 20!, wrap");
		viewName.setVisible(false);
		
		add(new JXTitledSeparator("Associate with tree to domain tree"),"growx, span, wrap, gaptop 20");
		add(new JLabel("Select view: "), 	"gap 10");
		add(selectTreeViewList, 				"w 270!, gap 5, gapright 10, span, wrap");
		
		add(new JXTitledSeparator("Associate with sequence view"),"growx, span, wrap, gaptop 20");
		add(new JLabel("Select view: "), 	"gap 10");
		add(selectSeqViewList, 				"w 270!, gap 5, gapright 10, span, wrap");
	}

	
	/**
	 * Action performed when the browse button was clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {	
		File file = FileDialogs.showOpenDialog(DoMosaicsUI.getInstance());
		if(file != null) {
			File test = new File(file.getAbsolutePath());
			daSet = ArrangementImporterUtil.importData(test);
		}
		if(daSet != null) {
			path.setText(file.getAbsolutePath());
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
			return "Select Arrangement Data";
    }
    
	/**
     * Checks if all necessary inputs are made.
     */
    @Override
	protected String validateContents (Component component, Object o) {
    	if (daSet == null)
    		return "Please select a correctly formatted xdom file";
    	if (path.getText().isEmpty())
			return "Please select a file";
    	//if (viewName.getText().trim().isEmpty())
    	//	return "Please select a view name";
        return null;
    }
    
	
 
}