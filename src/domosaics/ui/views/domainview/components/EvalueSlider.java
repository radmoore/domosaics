package domosaics.ui.views.domainview.components;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import domosaics.model.configuration.Configuration;
import domosaics.ui.util.MyMetalSliderUI;

public class EvalueSlider extends JSlider{
	private static final long serialVersionUID = 1L;
	
	/** static variable used to memorize the indice (Hash key) of the default Evalue threshold */
	protected static int threshold = 37;
	
	
	protected Hashtable<Integer, Double> threshold2value;
	protected Hashtable<Integer, JLabel> threshold2label;
	
	
	public EvalueSlider () {
		super(0, 37, 14);
		
		initThresholdValues();
		initSliderLabels();
		
		
		setLabelTable(threshold2label);
		setPaintLabels(true);  

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
		this.setUI(new MyMetalSliderUI(this));
	    try {
			UIManager.setLookAndFeel(save);
		} 
	    catch (UnsupportedLookAndFeelException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
	    
	}

	/**
	 * Give the index of the current evalue threshold.
	 */
	public double getEvalue() {
		return threshold2value.get(getValue());
	}
	
	/**
	 * Give the index of the memorized evalue threshold.
	 */
	public double getEvalueThreshold() {
		return threshold2value.get(threshold);
	}
	
	/**
	 * Give the index of the memorized evalue threshold.
	 */
	public int getThreshold() {
		return threshold;
	}
	
	/**
	 * Set the index for the memorized evalue threshold.
	 * 
	 */
	public void setThreshold(double evalue)
	{
	 for(int i=0; i<threshold2value.size(); i++)
	 {
	  if(threshold2value.get(i)==evalue)
	  {
	   threshold=i;
	   break;
	  }
	 }
	}
	
	private void initThresholdValues() {
		threshold2value = new Hashtable<Integer, Double>();
	
		threshold2value.put(new Integer(0), new Double("0"));
		
		threshold2value.put(new Integer(1), new Double("1E-100"));
		threshold2value.put(new Integer(2), new Double("1E-90"));
		threshold2value.put(new Integer(3), new Double("1E-80"));
		threshold2value.put(new Integer(4), new Double("1E-70"));
		threshold2value.put(new Integer(5), new Double("1E-60"));
		threshold2value.put(new Integer(6), new Double("1E-50"));
		threshold2value.put(new Integer(7), new Double("1E-40"));
		threshold2value.put(new Integer(8), new Double("1E-30"));
		threshold2value.put(new Integer(9), new Double("1E-20"));
		threshold2value.put(new Integer(10), new Double("1E-10"));
		
		threshold2value.put(new Integer(11), new Double("1E-9"));
		threshold2value.put(new Integer(12), new Double("1E-8"));
		threshold2value.put(new Integer(13), new Double("1E-7"));
		threshold2value.put(new Integer(14), new Double("1E-6"));
		threshold2value.put(new Integer(15), new Double("1E-5"));
		threshold2value.put(new Integer(16), new Double("1E-4"));
		threshold2value.put(new Integer(17), new Double("1E-3"));
		threshold2value.put(new Integer(18), new Double("1E-2"));
		
		threshold2value.put(new Integer(19), new Double("0.1"));
		threshold2value.put(new Integer(20), new Double("0.2"));
		threshold2value.put(new Integer(21), new Double("0.3"));
		threshold2value.put(new Integer(22), new Double("0.4"));
		threshold2value.put(new Integer(23), new Double("0.5"));
		threshold2value.put(new Integer(24), new Double("0.6"));
		threshold2value.put(new Integer(25), new Double("0.7"));
		threshold2value.put(new Integer(26), new Double("0.8"));
		threshold2value.put(new Integer(27), new Double("0.9"));
		
		threshold2value.put(new Integer(28), new Double("1"));
		threshold2value.put(new Integer(29), new Double("2"));
		threshold2value.put(new Integer(30), new Double("3"));
		threshold2value.put(new Integer(31), new Double("4"));
		threshold2value.put(new Integer(32), new Double("5"));
		threshold2value.put(new Integer(33), new Double("6"));
		threshold2value.put(new Integer(34), new Double("7"));
		threshold2value.put(new Integer(35), new Double("8"));
		threshold2value.put(new Integer(36), new Double("9"));
		threshold2value.put(new Integer(37), new Double("10"));
	}

	private void initSliderLabels() {
		threshold2label = new Hashtable<Integer, JLabel>();
		threshold2label.put(new Integer(0), new JLabel("0"));
		threshold2label.put(new Integer(10), new JLabel("1E-10"));
		threshold2label.put(new Integer(19), new JLabel("0.1"));
		threshold2label.put(new Integer(28), new JLabel("1"));
		threshold2label.put(new Integer(32), new JLabel("5"));
		threshold2label.put(new Integer(37), new JLabel("10"));
	}

}
