package domosaics.ui.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.domainview.DomainViewI;



import net.miginfocom.swing.MigLayout;

public class NoteEditor extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the view providing this feature */
	protected DomainViewI view;
	
	/** button to cancel this feature */
	protected JButton jbtCancel;
	
	/** button to apply the settings */
	protected JButton jbtApply;
	
	/** the text area displaying the message */
	protected JTextArea noteArea;
	
	/** the domain arrangement which note is going to be changed */
	protected DomainArrangement selectedDA;
	
	
	/**
	 * Constructor for a new NoteEditor dialog.
	 * 
	 * @param view
	 * 		the view providing this feature
	 */
	public NoteEditor(DomainViewI view, DomainArrangement selectedDA) {
		this.view = view;
		this.selectedDA = selectedDA;
		
		// create components
		jbtCancel = new JButton("Cancel");
		jbtCancel.addActionListener(this);
		jbtApply = new JButton("Apply");
		jbtApply.addActionListener(this);
		
		noteArea = new JTextArea ();
		noteArea.setFont(new Font ("Courier", 0, 12));	// style plain, size 14
		noteArea.setColumns(60);
		noteArea.setLineWrap(true);
		noteArea.setRows(12);
		noteArea.setWrapStyleWord(false);				// wrap on chars
		noteArea.setEditable(true);
		
		if (view.getNoteManager().getNote(selectedDA) != null)
			noteArea.setText(view.getNoteManager().getNote(selectedDA));

		// layout the content
		JPanel contentPanel = new JPanel(new MigLayout());
		
		contentPanel.add(new JScrollPane(new JScrollPane(noteArea)), "gaptop 10, gap 10, growx, span, wrap");	// 2 panes make a better border
		contentPanel.add(jbtApply, "gap 10");
		contentPanel.add(jbtCancel, "gap 10, wrap");
		
		// set up the dialog
		getContentPane().add(contentPanel);
		pack();
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

	/**
	 * Handles the button events
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jbtCancel) 
			dispose();
		if(e.getSource() == jbtApply) {
			view.getNoteManager().setNode(selectedDA, noteArea.getText());
			dispose();
		}
	}
	

}
