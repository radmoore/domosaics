package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.model.arrangement.DomainArrangement;

public class ChangeSequencePage extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** the key used to access the tree view after the wizard finished */
	public static final String SEQ_KEY = "sequence";
	public static final String SEQTYPE_KEY = "sequence_type";
	
	/** the text field displaying the chosen file path */
	private JTextArea input;
	private JTextField lengthField;
	
	private JButton removeSequence, restore;
	
	private String startingSeq;
	private DomainArrangement selectedArr;
	
	
	/**
	 * Constructor for a new CreateProjectPage
	 * @param selectedArrangement 
	 * @param initSeq
	 */
	public ChangeSequencePage(String initSeq, DomainArrangement selectedArr) {
		super("Change sequence data");
		
		this.selectedArr = selectedArr;
		this.startingSeq = initSeq;
		
		setLayout(new MigLayout());
		
		// holds the current length of the sequence
		lengthField = new JTextField();
		lengthField.setText(""+initSeq.length());
		lengthField.setEditable(false);
		
		input = new JTextArea (initSeq);
		input.setFont(new Font ("Courier", 0, 12));	// style plain, size 14
		input.setColumns(60);
		input.setLineWrap(true);
		input.setRows(12);
		input.setWrapStyleWord(false);				// wrap on chars
		input.setEditable(true);
		input.setName(SEQ_KEY);
		
		input.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				lengthField.setText(""+input.getText().length());
			}
			
			public void insertUpdate(DocumentEvent e) {
				lengthField.setText(""+input.getText().length());
			}
			
			public void changedUpdate(DocumentEvent e) {}
		});
		
		removeSequence = new JButton("Remove sequence");
		removeSequence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setText("");
			}
		});
		
		restore = new JButton("Restore sequence");
		restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setText("");
				input.setText(startingSeq);
				lengthField.setText(""+startingSeq.length());
			}
		});
		
		if (startingSeq.isEmpty()) {
			removeSequence.setEnabled(false);
			restore.setEnabled(false);
		}

		// layout panel
		add(new JXTitledSeparator("Edit the sequence"),"growx, span, wrap, gaptop 15");
		add(new JScrollPane(new JScrollPane(input)), "gap 10, growx, wrap");
		add(new JLabel("Length"), "gap 10, split 2");
		add(lengthField, "gap 5, w 70!, split 2, span x, wrap");
		add(removeSequence, "gap 10, split 2");
		add(restore, "gap 5, split 2");
	}

	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
	public static final String getDescription() {
        return "Change sequence data";
    }
    
	/**
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {

    	String newSeq = input.getText(); 
    	//System.out.println("This is the new sequence length: "+newSeq.length());
    	if (! newSeq.isEmpty() ) {
    		
    		// check if it contains no non-valid chars
    		
    	}
    	
        return null;
    }


}
