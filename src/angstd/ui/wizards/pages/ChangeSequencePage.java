package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainVector;
import angstd.model.sequence.util.SeqUtil;

public class ChangeSequencePage extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** the key used to access the tree view after the wizard finished */
	public static final String SEQ_KEY = "sequence";
	public static final String SEQTYPE_KEY = "sequence_type";
	
	/** the text field displaying the chosen file path */
	private JTextArea input;
	
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
		
		// init components
		input = new JTextArea (initSeq);
		input.setFont(new Font ("Courier", 0, 12));	// style plain, size 14
		input.setColumns(60);
		input.setLineWrap(true);
		input.setRows(12);
		input.setWrapStyleWord(false);				// wrap on chars
		input.setEditable(true);
		
		input.setName(SEQ_KEY);
		
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
			}
		});
		
		if (startingSeq.isEmpty()) {
			removeSequence.setEnabled(false);
			restore.setEnabled(false);
		}
		
		// layout panel
		add(new JXTitledSeparator("Edit the sequence"),"growx, span, wrap, gaptop 15");
		add(new JScrollPane(new JScrollPane(input)), "gap 10, growx, wrap");
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
    	
    	if (! newSeq.isEmpty() ) {
    		
    		// check if it contains no non-valid chars
    		
    	}
    	
        return null;
    }


}
