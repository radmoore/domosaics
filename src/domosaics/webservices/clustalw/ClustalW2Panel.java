package domosaics.webservices.clustalw;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.ui.tools.configuration.ConfigurationFrame;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.wizards.pages.ClustalW2Page;
import domosaics.util.UiUtil;
import domosaics.webservices.WebservicePrinter;




/**
 * A Panel to access the ClustalW2 webservice in a GUI form.
 * The panel can also be embedded within a wizard page, but can be 
 * null within the constructor also. Therefore when the job is done, 
 * the wizard page has to be notified that it can update itself.
 * 
 * @author Andreas Held
 *
 */
public class ClustalW2Panel extends JPanel implements WebservicePrinter {
	private static final long serialVersionUID = 1L;

	/** default email address */
	private static String DEFAULT_EMAIL = "enter your email here";
	
	/** email field for input parameter */
	private JTextField email;
	
	/** start button to set things running */
	private JButton submit;
	
	/** console to update user about the service status */
	private JTextArea console;
	
	/** clustalW2 web service interface */
	//private ClustalW2Service clustalW2;
	private ClustalW2ServiceII clustalW2;
	
	/** controller if the service is finished */
	private boolean jobDone = false;
	
	/** the embedding wizard page if there is any */
	private ClustalW2Page wizardPage;
	
	private Configuration config;
	
	public ClustalW2Panel(SequenceI[] seqs, ClustalW2Page wizardPage) {
		this.wizardPage = wizardPage;
		initComponents();
		clustalW2 = new ClustalW2ServiceII(ClustalW2ServiceII.type1, this);
		clustalW2.setQuerySequences(seqs);
		initPanel();
	}
	
	
    /* ************************************************************* *
	 * 							PANEL SETUP							 *
	 * ************************************************************* */
	
	private void initPanel() {
		setLayout(new MigLayout());
		
		// parameter
		add(new JLabel("Your e-mail:"), "gap 5");
		add(email, "h 25!, span, growX, wrap");
	 
		// buttons
		add(submit, "gaptop 10, w 165!, wrap");
		// console
		add(new JXTitledSeparator("Console"),	"growx, span, wrap, gaptop 25");
		add(new JScrollPane(console),			"gap 10, span, wrap");	
	}
	
	private void initComponents() {
		config = Configuration.getInstance();
		String email_text = (config.getEmailAddr().isEmpty()) ? DEFAULT_EMAIL : config.getEmailAddr() ;
		email = UiUtil.createEmailField(email_text);
		console = UiUtil.createConsole();				// wrap on chars
		console.setRows(6);
		
		submit = new JButton("Submit Job");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				submitJob();
			}
		});
	}
	
	/* ************************************************************* *
	 * 						WebservicePrinter Methods				 *
	 * ************************************************************* */
	
	public String getResult() {
		return clustalW2.getResult();
	}
	
	public void print(String text) {
		console.append(text);
		console.setCaretPosition(console.getText().length());
	}
	
	public void setJobDone(boolean done) {
		jobDone = done;
		if (jobDone == true && wizardPage != null)
			wizardPage.finish(getResult());
	}
	
	public boolean isJobDone() {
		return jobDone;
	}
	
	/* ************************************************************* *
	 * 								Other							 *
	 * ************************************************************* */

	public String getEmail() {
		return email.getText();
	}
	
	/**
	 * Submits a new annotation job for all sequences
	 */
	public void submitJob() {
		console.setText("");
		
		if(clustalW2.isRunning()) {
			MessageUtil.showWarning("ClustalW2 is currently running please wait!");
			//print("ClustalW2 is currently running please wait! \n");
			return;
		}
		
		if (clustalW2.getSequences() == null) {
			MessageUtil.showWarning("Please load sequences first!");
			//print("Please load sequences first! \n");
			return;
		}
		
		if (!UiUtil.isValidEmail(email.getText())) {
			MessageUtil.showWarning("Please enter a valid email!");
			//print("Please enter a correct email address! \n");
			return;
		} else {
			if(Configuration.getInstance().getEmailAddr().equals("")) {
				Configuration.getInstance().setEmailAddr(email.getText());
			} else {
				if(!email.getText().equals(Configuration.getInstance().getEmailAddr()))
					if(MessageUtil.showDialog(this.getParent(),"A distinct email is saved in settings. Overwrite?"))
					{
						Configuration.getInstance().setEmailAddr(email.getText());
						if(Configuration.getInstance().getFrame()!=null) {
							Configuration.getInstance().getFrame().dispose();
							Configuration.getInstance().setFrame(new ConfigurationFrame());
						}				
					}
			}
		}
		
		clustalW2.setParams(email.getText());
		clustalW2.execute();
	}

}
