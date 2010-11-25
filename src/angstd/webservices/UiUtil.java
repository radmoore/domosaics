package angstd.webservices;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class UiUtil {

	public static JTextArea createConsole() {
		JTextArea console = new JTextArea ();
		console.setFont(new Font ("Courier", 0, 12));	// style plain, size 14
		console.setColumns(50);
		console.setLineWrap(true);
		console.setRows(8);
		console.setWrapStyleWord(false);				// wrap on chars
		console.setEditable(false);
		return console;
	}
	
	
	public static JTextField createEmailField (String defaultManil) {
		final JTextField email = new JTextField(defaultManil);
		email.setForeground(new Color(210, 60, 60));
		email.getDocument().addDocumentListener(new DocumentListener() {
			// colorize green, if email is valid
			public void changedUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}

			public void insertUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}

			public void removeUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}
			
			private Color getCorrectColor(String adress) {
				if (isValidEmail(adress))
					return new Color(60, 120, 30);
				return new Color(210, 60, 60);
			}
		}); 
		return email;
	}

	public static boolean isValidEmail (String adress) {
		if (adress.contains(" "))			// white spaces
			return false;

		String[] nameDomain = adress.split("@");
		if (nameDomain.length != 2 		||	// no or more than one @
			nameDomain[0].length() == 0	||	// empty name
			!nameDomain[1].contains(".")||	// no .
			nameDomain[1].substring(0, nameDomain[1].lastIndexOf(".")).length() < 1 ||
			nameDomain[1].substring(nameDomain[1].lastIndexOf(".")).length()-1 < 2
		   )								// x.xx
			return false;
		
		// x@x.xx
		return true;
	}


}
