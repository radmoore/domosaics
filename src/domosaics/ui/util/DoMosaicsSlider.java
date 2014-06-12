package domosaics.ui.util;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import domosaics.model.configuration.Configuration;

//import domosaics.ui.views.domainview.components.SimilarityChooser.MySliderUI;

/**
 * Slider style within DoMosaics
 * 
 * @author Andreas Held
 *
 */
public class DoMosaicsSlider extends JSlider{
	private static final long serialVersionUID = 1L;
	
	public static final String WINSLIDER = "WinSlider";
	public static final String CUTOFFSLIDER = "CutOffSlider";
	
	protected int minThreshold;
	protected int maxThreshold;
	protected String name;

	public DoMosaicsSlider (String name, int minThres, int maxThres, int init) {
		super(minThres, maxThres, init);
		
		this.name = name;
		this.minThreshold = minThres;
		this.maxThreshold = maxThres;

		// set labels for min and max value
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
	
		labelTable.put(new Integer(minThreshold), new JLabel(""+minThreshold));	
		labelTable.put(new Integer(maxThreshold), new JLabel(""+maxThreshold));
		setLabelTable(labelTable);
		setPaintLabels(true); 
		
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		
		//Correction for MAC OS
	    LookAndFeel save = UIManager.getLookAndFeel();
	    LookAndFeel laf = new MetalLookAndFeel();
	    try {
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		this.setUI(new MyMetalSliderUI(this));
	    try {
			UIManager.setLookAndFeel(save);
		} catch (UnsupportedLookAndFeelException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}