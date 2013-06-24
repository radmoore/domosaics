package domosaics.ui.views.domainview.components;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
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
	protected String[] tooltips;

	
	public EvalueSlider () {
		super(0, 37, 37);
		
		initThresholdValues();
		initSliderLabels();
		
		
		setLabelTable(threshold2label);
		setPaintLabels(true); 
	    

		//Correction for MAC OS
	    /*LookAndFeel save = UIManager.getLookAndFeel();
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
		}*/
	    
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
	public void setThreshold(int t)
	{
	   threshold=t;
	}
	
	private void initThresholdValues() {
		tooltips = new String[38];
		tooltips[0]="0";
		tooltips[1]="1E-100";
		tooltips[2]="1E-90";
		tooltips[3]="1E-80";
		tooltips[4]="1E-70";
		tooltips[5]="1E-60";
		tooltips[6]="1E-50";
		tooltips[7]="1E-40";
		tooltips[8]="1E-30";
		tooltips[9]="1E-20";
		tooltips[10]="1E-10";
		tooltips[11]="1E-9";
		tooltips[12]="1E-8";
		tooltips[13]="1E-7";
		tooltips[14]="1E-6";
		tooltips[15]="1E-5";
		tooltips[16]="1E-4";
		tooltips[17]="1E-3";
		tooltips[18]="1E-2";
		tooltips[19]="1E-1";
		tooltips[20]="2E-1";
		tooltips[21]="3E-1";
		tooltips[22]="4E-1";
		tooltips[23]="5E-1";
		tooltips[24]="6E-1";
		tooltips[25]="7E-1";
		tooltips[26]="8E-1";
		tooltips[27]="9E-1";
		tooltips[28]="1";
		tooltips[29]="2";
		tooltips[30]="3";
		tooltips[31]="4";
		tooltips[32]="5";
		tooltips[33]="6";
		tooltips[34]="7";
		tooltips[35]="8";
		tooltips[36]="9";
		tooltips[37]="10";
		
		
		threshold2value = new Hashtable<Integer, Double>();
	
		for(int i=0; i<38; i++)
		{
			threshold2value.put(new Integer(i), new Double(tooltips[i]));
		}
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
