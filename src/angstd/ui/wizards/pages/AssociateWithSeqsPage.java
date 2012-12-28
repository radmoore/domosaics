package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.util.FileDialogs;
import angstd.ui.wizards.GUIComponentFactory;



public class AssociateWithSeqsPage extends WizardPage implements ActionListener {
	private static final long serialVersionUID = 1L;

	/** the key used to access the file path after the wizard finished */
	public static final String FILEPATH_KEY = "filepath";
	
	/** the key used to access the tree view after the wizard finished */
	public static final String SEQVIEW_KEY = "seqview";
	
	/** the key used to access the tree view after the wizard finished */
	public static final String DELETE_KEY = "delete";
	
	/** the text field displaying the chosen file path */
	protected JTextField path;
	
	/** combo box containing sequence views which can be used for association */
	protected JComboBox selectViewList;
	
	
	
	/**
	 * Constructor for a new CreateProjectPage
	 */
	public AssociateWithSeqsPage() {
		super("Select a file or view");
		
		setLayout(new MigLayout());
		
		// init components
		path = new JTextField(10);
		path.setEditable(false);
		
		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);		
		
		selectViewList = GUIComponentFactory.createSelectSeqViewBox(false);
		selectViewList.addActionListener(this);	
		
		JCheckBox delete = new JCheckBox("Delete sequence view from workspace?");
		
		// associate names
		path.setName(FILEPATH_KEY);
		selectViewList.setName(SEQVIEW_KEY);
		delete.setName(DELETE_KEY);
		
		// layout panel
		add(new JXTitledSeparator("Select fasta file"),"growx, span, wrap, gaptop 25");
		add(path, "gap 10, h 25!, growx");
		add(browse, "gap 5, gapright 10, wrap");
		
		add(new JXTitledSeparator("Merge with sequence view"),"growx, span, wrap, gaptop 25");
		add(new JLabel("Select view: "), 	"gap 10, split 2");
		add(selectViewList, 				"gap 10, wrap");
		add(delete, 						"gap 10, gaptop 10, wrap");
	}

	/**
	 * Action performed when the browse button was clicked
	 */
	public void actionPerformed(ActionEvent e) {	
		if (e.getSource() instanceof JButton) {
			File file = FileDialogs.showOpenDialog(this);
			if(file != null) 
				path.setText(file.getAbsolutePath());
			if(!path.getText().isEmpty() && selectViewList.getSelectedItem() != null)
				selectViewList.setSelectedItem(null);
		} else {
			if (!path.getText().isEmpty() && selectViewList.getSelectedItem() != null)
				path.setText("");
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
    	if (path.getText().isEmpty() && selectViewList.getSelectedItem() == null)
			return "Please select a file OR a sequence view";
        return null;
    }


}
