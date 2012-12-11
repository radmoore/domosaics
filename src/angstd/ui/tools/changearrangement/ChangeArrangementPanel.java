package angstd.ui.tools.changearrangement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.model.arrangement.io.GatheringThresholdsReader;
import angstd.model.configuration.Configuration;
import angstd.ui.util.MessageUtil;
import angstd.util.StringUtils;

/**
 * Panel where the domain attributes can be edited.
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** textfields to edit the from, to, evalue, and name attribute */
	protected JTextField id, from, to, evalue;
	
	/** buttons to apply the changes and reset the form */
	protected JButton add, reset, restore, close;
	
	/** the view providing the change mechanism */
	protected ChangeArrangementView view;

	
	/**
	 * Constructor for a new ChangeArrangementPanel
	 * 
	 * @param view
	 * 		view providing the change mechanism
	 */
	public ChangeArrangementPanel(ChangeArrangementView view) {
		super(new MigLayout());
		this.view = view;
		initComponents();
		
		add(new JXTitledSeparator("Change/Add Domains "),"growx, spanx 3, gaptop 25");
		add(new ChangeArrangementHelpPanel(), "spany, wrap");
		add(new JLabel("ID/Name: "), "gap 10, gaptop 10");
		add(id, "span 2, h 25!, wrap");
		
		add(new JLabel("From: "), "gap 10");
		add(from, "h 25!, wrap");
		
		add(new JLabel("To: "), "gap 10");
		add(to, "h 25!, wrap");
		
		add(new JLabel("E-value: "), "gap 10");
		add(evalue, "h 25!, wrap");

		add(new JLabel(" "), "gap 10, gaptop 10");
		add(add);
		add(reset, "gap 10, wrap");
		add(new JLabel(" "), "gap 10, gaptop 10");
		add(restore, "growx");
		add(close, "gap 10, growx, wrap");
		setSize(770,380);
	}
	
	/**
	 * Helper method to initialize the panel components 
	 */
	private void initComponents() {
		id = new JTextField(12);
		from = new JTextField(5);
		to = new JTextField(5);
		evalue = new JTextField(5);
		
		restore = new JButton("Restore");
		restore.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				restore();
			}	
		});
		
		add = new JButton("Add/Change");
		add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				apply();
			}	
		});
	
		reset = new JButton ("Reset");
		reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				reset();
			}
		});

		close = new JButton ("Close");
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				close();
			}
		});
	}
	
	/**
	 * Method which refreshes the domain attribute fields. This method
	 * is invoked when the user clicks on a domain.
	 * 
	 * @param dom
	 * 		the selected domain which attributes should be filled into the forms
	 */
	public void refreshDomain(Domain dom) {
		if(Configuration.isNamePreferedToAcc())
			id.setText(dom.getName());
		else
			id.setText(dom.getID());
		from.setText(""+dom.getFrom());
		to.setText(""+dom.getTo());
		if (dom.getEvalue() == Double.POSITIVE_INFINITY)
			evalue.setText("");
		else
			evalue.setText(""+dom.getEvalue());
	}
	
	/**
	 * Resets the domain attribute fields
	 */
	public void reset() {
		id.setText("");
		from.setText("");
		to.setText("");
		evalue.setText("");
		view.clearSelection();
	}
	
	
	public void restore() {
		view.restore();
		reset();
	}
	
	public void close() {
		view.closeWindow();
	}
	
	/**
	 * Apples the changes for a domain or uses the values to create a new domain
	 * TODO
	 * - there is an exception thrown here
	 * - double check DomainFamily issue
	 */
	public void apply() {
		if (!checkCorrectness()) 
			return;
		
	
		// arrangement to change
		DomainArrangement da = view.getDA();
		
		// domain to change if any
		Domain selectedDom = view.getSelectedDomain();
		
		// values for new/changing domain
		DomainFamily fam = GatheringThresholdsReader.getInstance().get(id.getText()); 
		
		if(fam == null)
			fam = GatheringThresholdsReader.getInstance().get(GatheringThresholdsReader.getAccFromID(id.getText()));
		
		if(fam == null)
			fam = new DomainFamily(id.getText(), id.getText(), DomainType.UNKNOWN);
		
		GatheringThresholdsReader.getInstance().put(id.getText(), fam);
		int fromVal = Integer.parseInt(from.getText());
		int toVal = Integer.parseInt(to.getText());
		
		double evalueVal = (evalue.getText().trim().isEmpty()) ? Double.POSITIVE_INFINITY : Double.parseDouble(evalue.getText());
		 
		Domain dom = new Domain(fromVal, toVal, fam, evalueVal);
		
		// perform the change / adding
		if (selectedDom != null)  	// change domain
			da.changeDomain(selectedDom, dom);
		else 						// add domain
			da.addDomain(dom);
		
		// update the sequence
		if (da.getSequence() != null) 
			if (da.getSequence().getLen(true) >= toVal)
				da.setSequence(da.getSequence());
		
		// sort the domains
		Collections.sort((Vector<Domain>)da.getDomains());
		
		reset();
		view.refreshViews(dom);
	}
	
	/**
	 * Helper method to check for correctness within the attribute fields.
	 * 
	 * @return
	 * 		whether or not the values are correct
	 */
	protected boolean checkCorrectness() {

		if(id.getText().isEmpty()){
			MessageUtil.showWarning(this, "Please provide an ID/name for the domain.");
			return false;
		}
		
		if (!StringUtils.isNumber(from.getText()) ||  Integer.parseInt(from.getText())<=0) {
			MessageUtil.showWarning(this, "The FROM field does not contain a valid number.");
			return false;
		}
		
		if (!StringUtils.isNumber(to.getText())){
			MessageUtil.showWarning(this, "The TO field does not contain a valid number.");
			return false;
		}

		if (!evalue.getText().trim().isEmpty() && !StringUtils.isNumber(evalue.getText())){
			MessageUtil.showWarning(this, "The EVALUE field does not contain a valid number.");
			return false;
		}

		if (Integer.parseInt(to.getText()) <= Integer.parseInt(from.getText())) {
			MessageUtil.showWarning(this, "The TO value is smaller than the FROM value.");
			return false;
		}
		
		if (view.getDA().getSequence() != null) {
			if (view.getDA().getSequence().getLen(true) < Integer.parseInt(from.getText()) || view.getDA().getSequence().getLen(true) < Integer.parseInt(to.getText())){
				MessageUtil.showWarning(this, "The domain exceeds sequence length.");
				return false;
			}
		}
			
		return true;
	}
	
}
