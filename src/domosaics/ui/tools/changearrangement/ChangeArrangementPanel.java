package domosaics.ui.tools.changearrangement;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.localservices.hmmer3.ui.HmmScanPanel;
import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.DomainType;
import domosaics.model.arrangement.io.GatheringThresholdsReader;
import domosaics.ui.util.MessageUtil;
import domosaics.util.StringUtils;




/**
 * Panel where the domain attributes can be edited.
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** textfields to edit the from, to, evalue, and name attribute */
	protected JTextField id, from, to, evalue, name;
	
	/** buttons to apply the changes and reset the form */
	protected JButton add, reset, restore, close;
	
	/** the view providing the change mechanism */
	protected ChangeArrangementView view;

	/** the view providing the change mechanism */
	protected JFrame parent;
	
	/**
	 * Constructor for a new ChangeArrangementPanel
	 * 
	 * @param view
	 * 		view providing the change mechanism
	 */
	public ChangeArrangementPanel(ChangeArrangementView view, JFrame parentFrame) {
		super(new MigLayout());
		this.parent=parentFrame;
		this.view = view;
		initComponents();
		
		add(new JXTitledSeparator("Change/Add Domains "),"growx, spanx 6, gaptop 25");
		add(new ChangeArrangementHelpPanel(), "spany, wrap");
		add(new JLabel("ID: "), "gap 10, gaptop 10");
		add(id, "h 25!, span 3");
		
		add(new JLabel("Name: "), "gap 5, gaptop 10");
		add(name, "h 25!, wrap");
		
		add(new JLabel("From: "), "gap 10");
		add(from, "h 25!");
		
		add(new JLabel("To: "), "gap 3");
		add(to, "h 25!");
		
		add(new JLabel("E-value: "), "gap 5");
		add(evalue, "h 25!, wrap");

		add(add, "span 4, growx, gap 10");
		add(reset, "span 2, growx, gap 5, gaptop 30, wrap");
		add(restore, "span 4, growx, gap 10");
		add(close, "span 2, gap 5, growx, wrap");
		setSize(870,330);
	}
	
	/**
	 * Helper method to initialize the panel components 
	 */
	private void initComponents() {
		id = new JTextField(12);
		name = new JTextField(12);
		from = new JTextField(4);
		to = new JTextField(4);
		evalue = new JTextField(6);
		
		restore = new JButton("Restore");
		restore.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				restore();
			}	
		});
		
		add = new JButton("Add/Change");
		add.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				apply();
			}	
		});
	
		reset = new JButton ("Reset");
		reset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				reset();
			}
		});

		close = new JButton ("Close");
		close.addActionListener(new ActionListener(){
			@Override
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
		id.setText(dom.getID());
		name.setText(dom.getName());
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
		name.setText("");
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
		if(fam == null) {
			fam = new DomainFamily(id.getText(), name.getText(), DomainType.UNKNOWN);
			GatheringThresholdsReader.add(fam);
		}
		
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
		Collections.sort(da.getDomains());
		
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

		id.setBackground(Color.white);
		name.setBackground(Color.white);
		evalue.setBackground(Color.white);
		from.setBackground(Color.white);
		to.setBackground(Color.white);
		DomainFamily fam;
		String complement="", singularOrPlural;
		if(id.getText().isEmpty()) {
			if(name.getText().isEmpty()) {
				MessageUtil.showWarning(parent, "Please provide an ID/Name for the domain.");
				id.setBackground(HmmScanPanel.highlightColor);
				name.setBackground(HmmScanPanel.highlightColor);
				return false;
			} else {
				fam = GatheringThresholdsReader.getInstance().get(name.getText());
				if(fam==null) {
					Vector<String> vectFam = GatheringThresholdsReader.getIDFromName(name.getText());
					if(vectFam!=null) {
						Iterator<String> si=vectFam.iterator();
						while(si.hasNext()) {
							DomainFamily df = GatheringThresholdsReader.getInstance().get(si.next());
							complement+= df.getDomainType().getName()+" database (ID="+df.getId();
							complement+="),\n";
						}
						if(vectFam.size()==1)
							singularOrPlural="a domain ID";
						else
							singularOrPlural="several domain IDs";
						if(!MessageUtil.showDialog(parent, "This Name "+name.getText()+" corresponds to "+singularOrPlural+" in DoMosaics:\n"+complement.substring(0,complement.length()-2)+".\nDo you really want to create a new domain family (ID=Name)?")) {
							id.setBackground(HmmScanPanel.highlightColor);
							if(vectFam.size()==1)
								id.setText(GatheringThresholdsReader.getInstance().get(vectFam.firstElement()).getId());
							return false;
						} else {
							id.setText(name.getText());
						}
					} else {
						if(!MessageUtil.showDialog(parent, "This Name "+name.getText()+" is new for DoMosaics.\nCreate a new domain family? (ID=Name)")) {
							name.setBackground(HmmScanPanel.highlightColor);
							return false;
						} else {
							id.setText(name.getText());
						}
					}
				} else {
					if(!MessageUtil.showDialog(parent, "The Name "+name.getText()+" corresponds to a domain ID from the\n"+fam.getDomainType().getName()+" database (Name "+fam.getName()+"). Accept Correction?")) {
						name.setBackground(HmmScanPanel.highlightColor);
						return false;
					} else {
						id.setText(name.getText());
						name.setText(fam.getName());
					}	
				}
			}
		} else {
			if(name.getText().isEmpty()) {
				fam = GatheringThresholdsReader.getInstance().get(id.getText());
				if(fam!=null) {
					if(!MessageUtil.showDialog(parent, "The ID "+id.getText()+" corresponds to a domain from\nthe "+fam.getDomainType().getName()+" database, named "+fam.getName()+". Accept?")) {
						name.setBackground(HmmScanPanel.highlightColor);
						return false;
					} else {
						name.setText(fam.getName());
					}
				} else {
					if(!MessageUtil.showDialog(parent, "This ID "+id.getText()+" is new for DoMosaics.\nCreate a new domain family?")) {
						id.setBackground(HmmScanPanel.highlightColor);
						return false;
					} else {
						name.setText(id.getText());
					}
				}
			} else	{
				fam = GatheringThresholdsReader.getInstance().get(id.getText());
				if(fam!=null) {
					if(name.getText().equals(fam.getName())) {
						if(!MessageUtil.showDialog(parent, "The ID "+id.getText()+" and Name "+fam.getName()+" correspond to a domain\nfrom "+fam.getDomainType().getName()+" database. Validate?")) {
							id.setBackground(HmmScanPanel.highlightColor);
							name.setBackground(HmmScanPanel.highlightColor);
							return false;
						}
					} else	{
						if(!MessageUtil.showDialog(parent, "The ID "+id.getText()+" corresponds to a domain from the "+fam.getDomainType().getName()+" database,\nbut with a DIFFERENT Name: "+fam.getName()+". Accept correction?")) {
							name.setBackground(HmmScanPanel.highlightColor);
							return false;
						} else {
							name.setText(fam.getName());
						}
					}
				} else {
					Vector<String> vectFam = GatheringThresholdsReader.getIDFromName(name.getText());
					if(vectFam!=null) {
						Iterator<String> si=vectFam.iterator();
						while(si.hasNext()) {
							DomainFamily df = GatheringThresholdsReader.getInstance().get(si.next());
							complement+= df.getDomainType().getName()+" database (ID "+df.getId();
							complement+="),\n";
						}
						if(vectFam.size()==1)
							singularOrPlural="a domain ID";
						else
							singularOrPlural="several domain IDs";
							if(!MessageUtil.showDialog(parent, "This ID "+id.getText()+" is new for DoMosaics, but the Name "+name.getText()+" corresponds to "+singularOrPlural+" in\n"+complement.substring(0,complement.length()-2)+".\nDo you really want to create a new domain family?")) {
							id.setBackground(HmmScanPanel.highlightColor);
							if(vectFam.size()==1)
								id.setText(GatheringThresholdsReader.getInstance().get(vectFam.firstElement()).getId());
							return false;
						}
					} else {
						if(!MessageUtil.showDialog(parent, "These ID "+id.getText()+" and Name "+name.getText()+" are new for DoMosaics.\nCreate a new domain family?")) {
							id.setBackground(HmmScanPanel.highlightColor);
							name.setBackground(HmmScanPanel.highlightColor);
							return false;
						}
					}
				}
			}
		}
		
		
		if (!StringUtils.isNumber(from.getText()) ||  Integer.parseInt(from.getText())<=0) {
			MessageUtil.showWarning(parent, "The FROM field does not contain a valid number.");
			from.setBackground(HmmScanPanel.highlightColor);
			return false;
		}
		
		if (!StringUtils.isNumber(to.getText())){
			MessageUtil.showWarning(parent, "The TO field does not contain a valid number.");
			to.setBackground(HmmScanPanel.highlightColor);
			return false;
		}

		if (!evalue.getText().trim().isEmpty() && !StringUtils.isNumber(evalue.getText())){
			MessageUtil.showWarning(parent, "The EVALUE field does not contain a valid number.");
			evalue.setBackground(HmmScanPanel.highlightColor);
			return false;
		}

		if (Integer.parseInt(to.getText()) <= Integer.parseInt(from.getText())) {
			MessageUtil.showWarning(parent, "The TO value is smaller than the FROM value.");
			from.setBackground(HmmScanPanel.highlightColor);
			to.setBackground(HmmScanPanel.highlightColor);
			return false;
		}
		
		if (view.getDA().getSequence() != null) {
			if (view.getDA().getSequence().getLen(true) < Integer.parseInt(from.getText())) {
				MessageUtil.showWarning(parent, "The domain exceeds sequence length.");
				from.setBackground(HmmScanPanel.highlightColor);
				return false;
			}
			if(view.getDA().getSequence().getLen(true) < Integer.parseInt(to.getText())) {
				MessageUtil.showWarning(parent, "The domain exceeds sequence length.");
				to.setBackground(HmmScanPanel.highlightColor);
				return false;
			}
		}
			
		return true;
	}
	
}
