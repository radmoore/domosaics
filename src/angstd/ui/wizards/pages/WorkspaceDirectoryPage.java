package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.util.FileDialogs;


public class WorkspaceDirectoryPage extends WizardPage implements ActionListener{
	private static final long serialVersionUID = 1L;

	/** the key used to access the project name after the wizard finished */
	public static final String FILE_KEY = "file";
	
	protected JTextField fileField;
	protected Component parent;
	
	protected static String text = "<html>" +
			"Select a Workspace directory.<br>" +
			"The Directory is used to store imported data<br>" +
			"and configuration information as well as<br>" +
			"computational results.<br>" +
			"</html>";
	
	public WorkspaceDirectoryPage(Component parent, String defaultDir) {
		super("Select a workspace directory");
		
		this.parent = parent;
		
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(300,200));
		
		//init components
		fileField = new JTextField(defaultDir);
		fileField.setName(FILE_KEY);
		
		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);	

		add(new JLabel(text),  	"gap 10, wrap");
		add(fileField, 			"gap 10, growx");
		add(browse, 			"wrap");
	}
	
	protected String validateContents(Component component, Object event) {
		File f = new File(fileField.getText());
        if (f == null || (component == fileField || component == null) && fileField.getText().trim().length() == 0 ){
        	return "Select a Directory";
        }
    	if(f.exists() && !f.canWrite())
    		return "Can not write to selected Directory";		        	
    	else if(!f.exists() && (f.getParentFile() == null || !f.getParentFile().canWrite())){
    		return "Can not write to selected location";
    	}			        
        return null;
    }
	
	public void actionPerformed(ActionEvent e) {
		File file = FileDialogs.openChooseDirectoryDialog(parent);
		if(file == null)
			return;
		
		fileField.setText(file.getAbsolutePath());
	}
}
