package domosaics.util;

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
		console.setColumns(55);
		console.setLineWrap(true);
		console.setRows(10);
		console.setWrapStyleWord(false);				// wrap on chars
		console.setEditable(false);
		return console;
	}
	
	
	public static JTextField createEmailField (String address) {
		final JTextField email = new JTextField(address, 20);
		Color color = ( isValidEmail(address) ) ? new Color(60, 120, 30) : new Color(210, 60, 60);
		email.setForeground(color);
		
		email.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}
			
			private Color getCorrectColor(String address) {
				if (isValidEmail(address))
					return new Color(60, 120, 30);
				return new Color(210, 60, 60);
			}
		}); 
		return email;
	}

	/**
	 * Checks whether or not the email address is valid.
	 * 
	 * @param
	 * 		the address entered in the email text field
	 * @return
	 * 		whether or not the address is valid
	 */
	public static boolean isValidEmail (String address) {
		if (address.contains(" "))			// white spaces
			return false;

		String[] nameDomain = address.split("@");
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
