package angstd.webservices.interproscan.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * Console box within the annotator panel. The box is used to print 
 * messages about the annotation process.
 * 
 * @author Andreas Held
 *
 */
public class ConsoleBox extends Box {
	private static final long serialVersionUID = 1L;
	
	private JTextArea console;
	
	public ConsoleBox() {
		super(BoxLayout.Y_AXIS);
		
		console = new JTextArea ();
		console.setFont(new Font ("Courier", 0, 14));	// style plain, size 14
		console.setColumns(60);
		console.setLineWrap(true);
		console.setRows(12);
		console.setWrapStyleWord(false);				// wrap on chars
		console.setEditable(false);

		add(new JScrollPane(new JScrollPane(console)));	// 2 panes make a better border

		setBackground(Color.WHITE);
		setBorder(BorderFactory.createTitledBorder(
				new LineBorder(new Color(150, 150, 150), 1, true), 	// Border (Color color, int thickness, boolean roundedCorners) 
				"Console", 											// title
				TitledBorder.LEFT, 									// titleJustification
				TitledBorder.DEFAULT_POSITION, 						// titlePosition
				new Font("Tahoma", Font.PLAIN, 18), 				// titleFont
				new Color(80, 80, 80)								// titleColor
		));
	}
	
	/**
	 * Prints a message to the console
	 * 
	 * @param text
	 * 		the message to print
	 */
	public void print (String text) {
		console.append(text);
		console.setCaretPosition(console.getText().length());
		this.repaint();
	}
	
	/**
	 * Returns the printed text of the console
	 * 
	 * @return
	 * 		the consoles text
	 */
	public String getText() {
		return console.getText();
	}
	
	/**
	 * Resets the console
	 */
	public void reset() {
		console.setText("");
	}

}
