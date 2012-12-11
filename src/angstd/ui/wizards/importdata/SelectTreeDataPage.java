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

import angstd.model.tree.TreeI;
import angstd.model.tree.io.NewickTreeReader;
import angstd.model.workspace.ProjectElement;
import angstd.ui.util.FileDialogs;
import angstd.ui.wizards.GUIComponentFactory;

/**
 * WizardPage allowing to choose a file which is used to create a tree view.
 * Additionally a domain view can be chosen which then will be used to create a 
 * domain tree.
 * 
 * @author Andreas Held
 *
 */
public class SelectTreeDataPage extends WizardPage implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the text field displaying the chosen file path */
	protected JTextField path;
	
	/** the text field for choosing a view name */
	protected JTextField viewName;
	
	private ProjectElement project;
	
	public SelectTreeDataPage(ProjectElement project) {
		super("Select Tree for "+ project.getShortTitle(5));
		this.project = project;
		init();
	}
	
	public SelectTreeDataPage() {
		super("Select Protein Data");
		init();
	}
	
	/**
	 * Constructor for a new SelectTreeDataPage
	 */
	public void init() {
		
		setLayout(new MigLayout());
		
		// init components
		path = new JTextField(20);
		path.setEditable(false);
		
		viewName = new JTextField(20);
		viewName.setEditable(true);
	
		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);
		
		JComboBox selectViewList;
		
		if (project == null)
			selectViewList = GUIComponentFactory.createSelectDomViewBox(false);
		else
			selectViewList = GUIComponentFactory.createSelectDomViewBox(project);
		
		// associate names
		path.setName(ImportDataBranchController.FILEPATH_KEY);
		viewName.setName(ImportDataBranchController.VIEWNAME_KEY);
		selectViewList.setName(ImportDataBranchController.DOMVIEW_KEY);

		// layout panel
		add(new JXTitledSeparator("Select tree file"),"growx, span, wrap");
		add(new JLabel("Select file:"), "gap 10");
		add(path, "w 150!, h 25!, gap 5");
		add(browse, "gapright 10, wrap");
		
		add(new JLabel("Enter a name:"), "gap 10");
		add(viewName, "w 150!, h 25!, gap 5, gaptop 5, gapright 10, wrap");
		
		add(new JXTitledSeparator("Associate with arrangements to domain tree"),"growx, span, wrap, gaptop 20");
		add(new JLabel("Select view: "), 	"gap 10");
		add(selectViewList, 				"w 270!, gap 5, gapright 10, span, wrap");
	}

	/**
	 * Action performed when the browse button was clicked
	 */
	public void actionPerformed(ActionEvent e) {	
		File file = FileDialogs.showOpenDialog(this);
		if(file != null) {
				TreeI tree = new NewickTreeReader().getTreeFromFile(file);
				if (tree != null) {
					path.setText(file.getAbsolutePath());
					viewName.setText(file.getName().split("\\.")[0]);
				}
		}
	}	

	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Select Tree Data";
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
