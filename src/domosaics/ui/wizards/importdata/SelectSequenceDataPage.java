package domosaics.ui.wizards.importdata;

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

import domosaics.model.sequence.io.FastaReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.wizards.GUIComponentFactory;




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
	
	/** the check for correct fasta format */
	protected boolean seqs = false;

	/**  list displaying all domain views of all projects */
	protected JComboBox selectViewList;

	private ProjectElement project;
	
	
	/**
	 * Constructor for a new SelectArrangementDataPage
	 */
	public SelectSequenceDataPage() {
		super("Select Arrangement Data");
		init();
	}

	public SelectSequenceDataPage(ProjectElement project) {
		super("Select sequence file");
		this.project = project;
		init();
	}
	
	
	private void init() {

		setLayout(new MigLayout());
		
		// init components
		path = new JTextField(20);
		path.setEditable(false);
		
		viewName = new JTextField(20);
		viewName.setEditable(true);
		
		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);	
		
		if (project == null) {
			selectViewList = GUIComponentFactory.createSelectDomViewBox(false);
		} else {
			selectViewList = GUIComponentFactory.createSelectDomViewBox(project);
		}
		// associate names
		path.setName(ImportDataBranchController.FILEPATH_KEY);
		viewName.setName(ImportDataBranchController.VIEWNAME_KEY);
		selectViewList.setName(ImportDataBranchController.DOMVIEW_KEY);
		
		// layout panel
		add(new JXTitledSeparator("Select sequence file"),"growx, span, wrap");
		add(new JLabel("Select file:"), 	"gap 10");
		add(path, 							"w 150!, h 25!, gap 5");
		add(browse, 						"gap 5, gapright 10, wrap");
		
		//add(new JLabel("Enter a name:"), "gap 10");
		add(viewName,  "w 50!, h 40!, wrap");
		viewName.setVisible(false);
		
		add(new JXTitledSeparator("Associate with arrangement view"),"growx, span, wrap, gaptop 20");
		add(new JLabel("Select view:"), 	"gap 10");
		add(selectViewList, 				"w 270!, gap 5, gapright 10, span 2, wrap");
	}
	
	/**
	 * Action performed when the browse button was clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		File file = FileDialogs.showOpenDialog(DoMosaicsUI.getInstance());
		if(file != null) {
			if (file.canRead()) {
				if ( FastaReader.isValidFasta(file) ) {
					path.setText(file.getAbsolutePath());
					viewName.setText(file.getName().split("\\.")[0]);
				}
				else {
					MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Failed to read file - unknown file format");
				}
			}
			else {
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Cannot read "+file.getName());
				path.setText("");
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
        return "Select Sequence Data";
    }
    
    /**
     * Checks if all necessary inputs are made.
     */
    @Override
	protected String validateContents (Component component, Object o) {
    	if (seqs)
    		return "Please select a correctly formatted fasta file";
    	if (path.getText().isEmpty())
			return "Please select a file";
    	//if (viewName.getText().trim().isEmpty())
    	//	return "Please select a view name";
        return null;
    }
    
}
