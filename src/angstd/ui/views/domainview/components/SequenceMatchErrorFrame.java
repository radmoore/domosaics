package angstd.ui.views.domainview.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.ui.views.domainview.DomainViewI;


import net.miginfocom.swing.MigLayout;

public class SequenceMatchErrorFrame extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	/** the list of unassigned domain arrangements */
	protected JComboBox arrangementList, sequenceList;
	
	/** button to end this feature */
	protected JButton jbtAssign, jbtClose;
	
	/** the view providing this feature */
	protected DomainViewI view;
	
	protected JTextArea sequenceInput;
	
	
	/**
	 * Constructor for a new SequenceMatchErrorFrame dialog.
	 * 
	 * @param view
	 * 		the view providing this feature
	 */
	public SequenceMatchErrorFrame(DomainViewI view, List<DomainArrangement> daSet, List<SequenceI> seqs) {
		this.view = view;
		
		JPanel componentHolder = new JPanel();
		componentHolder.setLayout(new MigLayout());

		// create components
//		arrangementList = new JComboBox(daSet.toArray(new DomainArrangement[daSet.size()]));
		
		arrangementList = new JComboBox(new DefaultComboBoxModel());
		for (DomainArrangement da : daSet)
			((DefaultComboBoxModel) arrangementList.getModel()).addElement(da);
		arrangementList.addActionListener(this);
		arrangementList.setSelectedIndex(0);
//		sequenceList  = new JComboBox(seqs.toArray(new SequenceI[seqs.size()]));
		sequenceList = new JComboBox(new DefaultComboBoxModel());
		for (SequenceI seq : seqs)
			((DefaultComboBoxModel) sequenceList.getModel()).addElement(seq);
		sequenceList.addActionListener(this);
		sequenceList.setSelectedIndex(0);
		
		jbtAssign = new JButton("Assign");
		jbtAssign.addActionListener(this);
		
		jbtClose = new JButton("Close");
		jbtClose.addActionListener(this);
		
		sequenceList.setActionCommand("sequenceList");
		jbtAssign.setActionCommand("assign");
		jbtClose.setActionCommand("cancel");
		
		sequenceInput = new JTextArea();
		sequenceInput.setFont(new Font ("Courier", 0, 12));	// style plain, size 14
		sequenceInput.setColumns(60);
		sequenceInput.setLineWrap(true);
		sequenceInput.setRows(12);
		sequenceInput.setWrapStyleWord(false);				// wrap on chars
		sequenceInput.setEditable(true);
		sequenceInput.setText(((SequenceI) sequenceList.getSelectedItem()).toOutputString());
		
//		add(new JXTitledSeparator("Select unassigned "),"growx, span, wrap, gaptop 25");
		componentHolder.add(arrangementList, "gap 10, growx");
		componentHolder.add(jbtAssign, "gap 10");
		componentHolder.add(jbtAssign, "gap 10");
		componentHolder.add(jbtClose, "gap 10");
		componentHolder.add(sequenceList, "gap 10, wrap");
		componentHolder.add(new JScrollPane(new JScrollPane(sequenceInput)), "gaptop 10, gap 10, growx, span, wrap");
		
		// set up the dialog
		getContentPane().add(componentHolder);
		pack();
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
	}
	
	/**
	 * Shows the dialog
	 * 
	 * @param parent
	 * 		the component used to show the dialog
	 * @param title
	 * 		the dialogs title
	 * @return
	 * 		always 0
	 */
	public int showDialog(Component parent, String title) {
		this.setTitle(title);
		this.setLocationRelativeTo(parent);
		setLocation(10, getLocation().y);
		this.setVisible(true);
		return 0;
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals("sequenceList")) {
			SequenceI seq = (SequenceI) ((JComboBox) evt.getSource()).getSelectedItem();
			if (seq == null)
				sequenceInput.setText("");
			else
				sequenceInput.setText(seq.toOutputString());
		} else if(evt.getActionCommand().equals("assign")) {
			// do the assigning
			assign();
			
			// remove assigned elements from the lists
			((DefaultComboBoxModel) arrangementList.getModel()).removeElement(arrangementList.getSelectedItem());
			((DefaultComboBoxModel) sequenceList.getModel()).removeElement(sequenceList.getSelectedItem());
			
			// if both lists are empty now close the dialog
			if (((DefaultComboBoxModel) sequenceList.getModel()).getSize() == 0 && ((DefaultComboBoxModel) arrangementList.getModel()).getSize() == 0)
				this.dispose();
		} else if(evt.getActionCommand().equals("cancel")) {
			this.dispose();
		}
	}
	
	public void assign() {
		DomainArrangement da = (DomainArrangement) arrangementList.getSelectedItem();
		String fastaSeq = sequenceInput.getText();
		
		// delete sequence from arrangement
		if (fastaSeq.isEmpty()) 
			return;
		
		SequenceI newSeq = new FastaReader().getDataFromString(fastaSeq)[0];
		if (newSeq.getName() == null)
			newSeq.setName(da.getName());
		
		da.setSequence(newSeq);
		
		if(!view.isSequenceLoaded())
			view.setSequencesLoaded(true);
	}
}
