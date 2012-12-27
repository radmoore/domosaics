package angstd.ui.tools.changearrangement;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

/**
 * ChangeArrangementHelpPanel displays a help text in a text area which
 * gives some instructions on how to operate with the ChangeArrangementFrame.
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementHelpPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/** help text to be displayed */
	String helpTxt = 
		"This frame allows the change of domain attributes and the\n" +
		"adding of new domains into the arrangement.\n\n" +
		"To change an existing domain just click on a domain below,\n" +
		"change its attributes and trigger the add/change button.\n\n" + 
		"If you want to add a new domain, please make sure that no\n" +
		"domain is selected, fill out the form for this domain and hit\n" +
		"the add/change button again.\n\n" +
		"The domain boundaries cannot exceed the sequence length\n" +
		"if a sequence is associated with the current arrangement."
	;
	
	/**
	 * Constructor for a new HelpPanel which can be used within the 
	 * ChangeArrangementFrame
	 */
	public ChangeArrangementHelpPanel() {
		super(new MigLayout());
		
		JTextArea txt = new JTextArea (helpTxt);
		txt.setFont(new Font ("Arial", 0, 14));		// style plain, size 14
		txt.setColumns(33);
		txt.setLineWrap(true);
		txt.setRows(13);
		txt.setWrapStyleWord(false);				// wrap on chars
		txt.setEditable(false);
		txt.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
		
		JScrollPane paneScrollPane = new JScrollPane(txt);
		paneScrollPane.setMinimumSize(new Dimension(10, 10));
		add(paneScrollPane, "gaptop 10");
	}
}

/* the same thing with a styled text pane */
//private String newline = "\n";
//public ChangeArrangementHelpPanel() {
//	JTextPane textPane = createTextPane();
//	JScrollPane paneScrollPane = new JScrollPane(textPane);
//    paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
////    paneScrollPane.setPreferredSize(new Dimension(250, 155));
//    paneScrollPane.setMinimumSize(new Dimension(10, 10));
//    
//    add(paneScrollPane);
//}
//
//private JTextPane createTextPane() {
//	String[] initString =
//    	{ 	//regular
//			"This frame allows the change of domain attributes" + newline +
//    		" and the adding of new domains into the arrangement." + newline +
//    		"To ",
//    		
//    		// bold
//    		"change an existing domain",
//    		
//    		// regular
//    		" just click on a domain below," + newline + 
//    		"change its attributes and trigger the add/change button." + newline +
//    		"If you want to ",
//    		
//    		// bold
//    		"add a new domain",
//    		
//    		// regular
//    		", please make sure that" + newline +
//    		"no domain is selected, fill out the form for this domain" + newline +
//    		"and hit the add/change button again." + newline + 
//    		"The ",
//    		
//    		// bold
//    		"sequences", 
//    		
//    		// regular
//    		" are adapted to a new domain automatically if" + newline +
//    		"a sequence is associated with the current arrangement."
//			
//             };
//
//    String[] initStyles =
//            { "regular", "bold", "regular", "bold",
//    		  "regular", "bold", "regular"
//            };
//
//    JTextPane textPane = new JTextPane();
//    StyledDocument doc = textPane.getStyledDocument();
//    addStylesToDocument(doc);
//
//    try {
//        for (int i=0; i < initString.length; i++) 
//            doc.insertString(doc.getLength(), initString[i], doc.getStyle(initStyles[i]));
//    } catch (BadLocationException ble) {
//        System.err.println("Couldn't insert initial text into text pane.");
//    }
//    return textPane;
//}
//
//protected void addStylesToDocument(StyledDocument doc) {
//    //Initialize some styles.
//    Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
//
//    Style regular = doc.addStyle("regular", def);
//    StyleConstants.setFontFamily(def, "Arial");
//    StyleConstants.setFontSize(def, 14);
//
//    Style s = doc.addStyle("bold", regular);
//    StyleConstants.setBold(s, true);
//}
