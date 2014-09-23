package domosaics.ui.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.sequence.SequenceI;

public class ShowDataDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String FASTA = "FASTA";
	public static String XDOM  = "XDOM";
	
	private DomainArrangement selectedDA;
	private String type;
	
	private JButton copyToClipboard;
	private JButton close;
	private JTextArea textArea;
	
	public ShowDataDialog(DomainArrangement selectedDA, String TYPE) {
		
		this.selectedDA = selectedDA;
		this.type = TYPE;
		
		JPanel contentPanel = new JPanel(new MigLayout());
		
		textArea = new JTextArea();
		textArea.setFont(new Font ("Courier", 0, 12));
		textArea.setColumns(70);
		textArea.setLineWrap(true);
		textArea.setRows(14);
		textArea.setWrapStyleWord(false);
		textArea.setEditable(false);
		
		// this will always set something - even if just an empty string
		setContent();
	
		copyToClipboard = new JButton("Copy to clipboard");
		copyToClipboard.addActionListener(this);
		
		close = new JButton("Close");
		close.addActionListener(this);
		
		contentPanel.add(new JScrollPane(textArea), "gaptop 10, gap 10, growx, span, wrap");
		contentPanel.add(copyToClipboard, "gap 10");
		contentPanel.add(close, "gap 10");
		
		getContentPane().add(contentPanel);
		pack();
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
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
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == close ) 
			dispose();
		if ( e.getSource() == copyToClipboard ) {
			copyContentToCilpboard();
		}
	}

	private void setContent() {
		String content = "";
		if ( type == ShowDataDialog.FASTA) {
			SequenceI seq = selectedDA.getSequence();
			if ( seq != null )
				content = seq.toFasta(false);
		}
		else if ( type == ShowDataDialog.XDOM ) {
			content = selectedDA.toXdom();
		}
		textArea.setText(content);		
	}
	
	private void copyContentToCilpboard() {
		String content = textArea.getText();
		StringSelection stringSelection = new StringSelection(content);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
	
}
