package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

public class ChangeSequencePage extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** the key used to access the tree view after the wizard finished */
	public static final String SEQ_KEY = "sequence";
	
	/** the text field displaying the chosen file path */
	protected JTextArea input;
	
	
	/**
	 * Constructor for a new CreateProjectPage
	 */
	public ChangeSequencePage(String initSeq) {
		super("Change sequence data");
		
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
		
		// layout panel
		add(new JXTitledSeparator("Edit the sequence"),"growx, span, wrap, gaptop 25");
		add(new JScrollPane(new JScrollPane(input)), "gap 10, growx, wrap");
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
        return null;
    }


}
