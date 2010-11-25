package angstd.localservices.hmmer2.ui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
//import angstd.localservices.hmmer2.Hmmer2Engine;
//import angstd.localservices.hmmer2.Hmmer2ProgramType;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;

/**
 * Permanent panel within the Hmmer2Frame which allows the choosing between
 * supported Hmmer2 executables.
 * 
 * @author Andrew Moore, Andreas Held
 *

public class Hmmer2ChooserPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the chooser allowing to choose a hmmer2 executable 
	protected JComboBox binChooser;
	
	/** the parent frame embedding this panel
	protected Hmmer2Frame parent;

	
	/**
	 * Constructor for a new Hmmer2ChooserPanel
	 * 
	 * @param parent
	 * 		the parent frame embedding this panel

	public Hmmer2ChooserPanel(Hmmer2Frame parent) {
		super(new MigLayout("", "[left]"));
		this.parent = parent;

		// set border
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "Hmmer binaries");
		title.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title);
		
		// init bin chooser
		binChooser = new JComboBox();
		binChooser.addItem("Choose program");
		binChooser.setEnabled(false);
		binChooser.setActionCommand("chooseBin");
		binChooser.addActionListener(this);
		
		// init load button
		JButton loadBin = new JButton("Hmmer binaries");
		loadBin.setActionCommand("load");
		loadBin.addActionListener(this);
		
		// layout panel
		add(loadBin,    "gap 10");
		add(binChooser, "gap 10, span2, growX, wrap");
	}

	/**
	 * @see ActionListener
	 
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("load"))
			loadAction();
		else if (e.getActionCommand().equals("chooseBin"))
			binChooserAction();
     }
	
	/**
	 * Action which is performed when the load button is pressed
	 * This action also fills the program list of the Hmmer2Engine.
	 
	private void loadAction() {
		Hmmer2Engine.getInstance().reset();
		
		// select hmmer2 directory
		File binDir = FileDialogs.openChooseDirectoryDialog(this);
		if (binDir == null)
			return;
		
		// retrieve file names within the folder
		String[] bins = binDir.list();
        if (bins == null) {
            MessageUtil.showWarning("Folder was empty");
            return;
        }
            	
        // create the binChooser
        String binPath = binDir.getAbsolutePath();
        for (int i = 0; i < bins.length; i++) {
        	File file = new File(binPath+System.getProperty("file.separator")+bins[i]);
            if ( file.canExecute() ) {	   
            	if (Hmmer2Engine.getInstance().addProgram(file.getName(), file.getAbsolutePath()))
            		binChooser.addItem(file.getName());
            }
        }
        
        binChooser.setEnabled(true);
	}
	
	/**
	 * Action which is performed when the binChooser state changes.
	 * Sets the correct parameter panel. 
	 
	private void binChooserAction() {
		String selectedProg = (String)binChooser.getSelectedItem();
		
		// if "choose program" was selected just kill the parameter panel
		if (binChooser.getSelectedIndex() == 0) {
			parent.addProgFrame(new JPanel());
			return;
		}
		
		// get the chosen program type
		Hmmer2ProgramType type = Hmmer2Engine.getInstance().getProgramType(selectedProg);
		
		// if the type is unknown talk to the user
		if (type == null) {
			MessageUtil.showWarning(selectedProg+" not supported yet.");
			parent.requestFocus();
			return;
		}
		
		// else change parameter panel and start service
		parent.addProgFrame(type.getPanel());
	}

}
 */