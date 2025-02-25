package domosaics.ui.views.domaintreeview.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.algos.indels.AbstractReconstructionAlgo;
import domosaics.algos.indels.Dollo;
import domosaics.algos.indels.Dollo4Sets;
import domosaics.algos.indels.Sankoff;
import domosaics.algos.indels.Sankoff4Sets;
import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.util.MyMetalSliderUI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.manager.DomainTreeLayoutManager.DomainTreeAction;

public class ReconstructionTool extends JDialog implements ChangeListener, ActionListener, PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	
	protected static final int DOLLO = 0;
	protected static final int SANKOFF = 1;
	protected static final int DOLLO4SETS = 2;
	protected static final int SANKOFF4SETS = 3;
	
	protected JButton jbtApply, jbtCancel;
	
	protected JButton jbtDollo, jbtSankoff;
	
	protected JCheckBox useSets;
	
	protected JSlider inSlider;
	protected JSlider delSlider;
	
	/** the view providing this feature */
	protected DomainTreeViewI view;
	
	protected int oldInThres = 2;
	protected int oldDelThres = 1;
	
	protected final JProgressBar progress;
	
	protected AbstractReconstructionAlgo algo;
	
	protected JPanel componentHolder;
	
	protected JTextField inCount, delCount;
	
	
	public ReconstructionTool(DomainTreeViewI view) {
		this.view = view;
		if(view.getParsimonyMeth()==Integer.MAX_VALUE) {
			view.setParsimonyMeth(SANKOFF);
		}

		// create components
		jbtDollo = new JButton("Dollo");
		jbtDollo.addActionListener(this);
		
		jbtSankoff = new JButton("Sankoff");
		jbtSankoff.addActionListener(this);
		
		jbtCancel = new JButton("Cancel");
		jbtCancel.addActionListener(this);
		
		jbtApply = new JButton("Apply");
		jbtApply.addActionListener(this);
		
		useSets = new JCheckBox("Treat arrangements as domain sets", false);
		useSets.addActionListener(this);
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(1), new JLabel("1"));
		labelTable.put(new Integer(5), new JLabel("5"));
		labelTable.put(new Integer(10), new JLabel("10"));
		
		inSlider = new JSlider(1, 10, oldInThres);
		inSlider.setName("inCost");
		inSlider.setToolTipText("insertion cost");
		inSlider.setLabelTable(labelTable);
		inSlider.setPaintLabels(true); 

		//Correction for MAC OS
	    LookAndFeel save = UIManager.getLookAndFeel();
	    LookAndFeel laf = new MetalLookAndFeel();
	    try {
			UIManager.setLookAndFeel(laf);
		} 
	    catch (UnsupportedLookAndFeelException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
	    inSlider.setUI(new MyMetalSliderUI(inSlider));
	    inSlider.addChangeListener(this);
		
		delSlider = new JSlider(1, 10, oldDelThres);
		delSlider.setName("delCost");
		delSlider.setToolTipText("deletion cost");
		delSlider.setLabelTable(labelTable);
		delSlider.setPaintLabels(true); 

		//Correction for MAC OS
		delSlider.setUI(new MyMetalSliderUI(delSlider));
		try {
			UIManager.setLookAndFeel(save);
		} 
		catch (UnsupportedLookAndFeelException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		delSlider.addChangeListener(this);	
		
		progress = new JProgressBar(0, 100);
		progress.setIndeterminate(false);
		progress.setStringPainted(true);
		
		inCount = new JTextField("0", 4);
		inCount.setEditable(false);
		inCount.setBorder(BorderFactory.createEmptyBorder());
		inCount.setFont(new Font("Arial", Font.BOLD, 12));
		
		delCount = new JTextField("0", 4);
		delCount.setEditable(false);
		delCount.setBorder(BorderFactory.createEmptyBorder());
		delCount.setFont(new Font("Arial", Font.BOLD, 12));
		
		layoutTool();

		// set up the dialog
		//setSize(370,450);
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
		setLocation(15, getLocation().y);
		this.setVisible(true);
		runChosenAlgo();

		return 0;
	}
	
	private void layoutTool() {
		// remove components when switching to another algorithm
		if (componentHolder != null) {
			getContentPane().remove(componentHolder);
			componentHolder = null;
		}
		componentHolder = new JPanel(new MigLayout());
		// layout the panel depending on the chosen algorithm
		componentHolder.add(new JXTitledSeparator("Choose reconstruction algorithm "),"growx, span, wrap, gaptop 10");
		componentHolder.add(new JLabel(" "), "gap 10");
		componentHolder.add(jbtSankoff);
		componentHolder.add(jbtDollo, "w 80!, gap 10");
		componentHolder.add(new JLabel(" "), "w 60!, gap 10, wrap");
		componentHolder.add(useSets, "gaptop 5, growx, span, wrap");
		
		if (view.getParsimonyMeth() == SANKOFF || view.getParsimonyMeth() == SANKOFF4SETS) {
			componentHolder.add(new JXTitledSeparator("Adjust insertion deletion costs "),"growx, span, wrap, gaptop 10");
//			componentHolder.add(new JLabel("Insertion cost"), "gap 10");
			componentHolder.add(inSlider, "gap 10, gapright10, growx, span, wrap");
//			componentHolder.add(new JLabel("Deletion cost"), "gap 10");
			componentHolder.add(delSlider, "gap 10, gapright10, growx, span, wrap");
		}
		
		componentHolder.add(new JXTitledSeparator("Stats"),"growx, span, wrap, gaptop 10");
		componentHolder.add(new JLabel("# Gain:"), "gap 10");
		componentHolder.add(inCount, "wrap");
		componentHolder.add(new JLabel("# Loss:"), "gap 10");
		componentHolder.add(delCount, "wrap");
		
		componentHolder.add(new JXTitledSeparator("Progress "),"growx, span, wrap, gaptop 10");
		componentHolder.add(progress, "gap 10, gapright10, growx, span, wrap");
	
		componentHolder.add(new JXTitledSeparator("Apply settings"),"growx, span, wrap, gaptop 10");
		componentHolder.add(new JLabel(" "), "gap 10");
		componentHolder.add(jbtApply, "growx");
		componentHolder.add(jbtCancel, "gap 10, growx, wrap");
		
		getContentPane().add(componentHolder);
		pack();
	}
	
	/** 
	 * Method reacting on slider changes, e.g. when using
	 * Sankoff parsimony to adjust costs for insertions and deletions.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		if (slider.equals(inSlider)) {
			if (oldInThres == slider.getValue())
				return;
			oldInThres = slider.getValue();
			runChosenAlgo();
		}
		if (slider.equals(delSlider)) {
			if (oldDelThres == slider.getValue())
				return;
			oldDelThres = slider.getValue();
			runChosenAlgo();
		}
	}
	
	/**
	 * Reacts on progress changes of the actually running parsimony
	 * algorithm. When the algorithm finishes (progress = 100%) a 
	 * structural change is fired.
	 * 
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			final int percent = (Integer) evt.getNewValue();

			SwingUtilities.invokeLater(new Runnable() {
	            @Override
				public void run() {
	            	progress.setValue(percent);
	    			progress.setString(percent+"%");
	    			
	    			if (percent == 100 && algo != null) {
	    				
	    				// retrieve number of deletion / Insertions
//	    				int[] indels = algo.getEvtCount();
//	    				inCount.setText(""+indels[0]);
//	    				delCount.setText(""+indels[1]);
	    				inCount.setText(""+algo.getInCount());
	    				delCount.setText(""+algo.getDelCount());
	    				
	    				algo = null;
	    				view.getDomainTreeLayoutManager().structuralChange();
	    			}
	            }
	        });
		}
	}
	
	/**
	 * Handles the button events
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// layout tool for dollo parsimony and start the algorithm
		if(e.getSource() == jbtDollo) {
			if (useSets.isSelected())
				view.setParsimonyMeth(DOLLO4SETS);
			else
				view.setParsimonyMeth(DOLLO);
			layoutTool();
			runChosenAlgo();
			return;
		}
		
		// layout tool for sankoff parsimony and start the algorithm
		if(e.getSource() == jbtSankoff) {
			if (useSets.isSelected())
				view.setParsimonyMeth(SANKOFF4SETS);
			else
				view.setParsimonyMeth(SANKOFF);
			layoutTool();
			runChosenAlgo();
			return;
		}
		
		if(e.getSource() == useSets) {
			if (view.getParsimonyMeth() == DOLLO)
				view.setParsimonyMeth(DOLLO4SETS);
			else if(view.getParsimonyMeth() == SANKOFF)
				view.setParsimonyMeth(SANKOFF4SETS);
			else if(view.getParsimonyMeth() == DOLLO4SETS)
				view.setParsimonyMeth(DOLLO);
			else if(view.getParsimonyMeth() == SANKOFF4SETS)
				view.setParsimonyMeth(SANKOFF);
				
			runChosenAlgo();
			return;
		}
		
		// close tool
		if(e.getSource() == jbtApply) {
			if (algo != null && !algo.isDone()) {
				setAlwaysOnTop(false);
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Wait for algorithm to finish");
				setAlwaysOnTop(true);
				return;
			}
			
			this.dispose();
			return;
		}
		
		if(e.getSource() == jbtCancel) {
			if (algo != null && !algo.isDone()) 
				algo.cancel(true);
			
			AbstractReconstructionAlgo.removeAllEvents(view);
			view.getInnerNodeArrangementManager().reset();
			
			// set the correct setting for the show indel action
			view.getDomainTreeLayoutManager().setState(DomainTreeAction.SHOW_INDELS, false);
			view.getDomainTreeLayoutManager().structuralChange();
			
			this.dispose();
			return;
		}
	}
	
	/**
	 * Runs the actually chosen algorithm
	 */
	private void runChosenAlgo() {
		if (algo != null && !algo.isDone()) 
			algo.cancel(true);
		switch (view.getParsimonyMeth()) {
		case DOLLO: runDollo(); break;
		case SANKOFF: runSankoff(); break;
		case DOLLO4SETS: runDollo4Sets(); break;
		case SANKOFF4SETS: runSankoff4Sets(); break;
		}
	}
	
	/**
	 * Starts the Dollo algorithm
	 */
	private void runDollo() {
		algo = new Dollo(view, false);
		algo.addPropertyChangeListener(this);
		((Dollo) algo).setParams(view.getDomTree());
		algo.execute();
	}
	
	private void runDollo4Sets() {
		algo = new Dollo4Sets(view, true);
		algo.addPropertyChangeListener(this);
		((Dollo4Sets) algo).setParams(view.getDomTree());
		algo.execute();
	}
	
	/**
	 * Starts the Sankoff algorithm
	 */
	private void runSankoff() {
		algo = new Sankoff(view, false);
		algo.addPropertyChangeListener(this);
		((Sankoff) algo).setParams(view.getDomTree(), inSlider.getValue(), delSlider.getValue());
		algo.execute();
	}
	
	private void runSankoff4Sets() {
		algo = new Sankoff4Sets(view, true);
		algo.addPropertyChangeListener(this);
		((Sankoff4Sets) algo).setParams(view.getDomTree(), inSlider.getValue(), delSlider.getValue());
		algo.execute();
	}

}
