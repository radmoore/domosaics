package domosaics.ui.views.domainview.components;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalSliderUI;

public class EvalueSlider extends JSlider{
	private static final long serialVersionUID = 1L;
	
	/** static variable used to memorize the Evalue threshold */
	protected static int oldThreshold = 10;
	
	/** the actual threshold */
	protected int threshold = 1;
	
	protected Hashtable<Integer, Double> threshold2value;
	protected Hashtable<Integer, JLabel> threshold2label;
	
	
	public EvalueSlider () {
		super(0, 37, 14);
		
		initThresholdValues();
		initSliderLabels();
		
		
		setLabelTable(threshold2label);
		setPaintLabels(true); 
		setUI(new MySliderUI(this));
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
	public double getEvalueOldThreshold() {
		return threshold2value.get(oldThreshold);
	}
	
	/**
	 * Give the index of the memorized evalue threshold.
	 */
	public int getOldThreshold() {
		return oldThreshold;
	}
	
	/**
	 * Set the index for the memorized evalue threshold.
	 * 
	 */
	public void setOldThreshold(double evalue)
	{
	 for(int i=0; i<threshold2value.size(); i++)
	 {
	  if(threshold2value.get(i)==evalue)
	  {
	   oldThreshold=i;
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
   
    
    
	/**
	 * Give the slider a nice look and show the current threshold as popup.
	 * 
	 * @author Andreas Held
	 *
	 */
	private class MySliderUI extends MetalSliderUI implements MouseMotionListener, MouseListener {
    	final JPopupMenu pop = new JPopupMenu();
    	JMenuItem item = new JMenuItem();
   
    	public MySliderUI ( JSlider slider ) {
    		super();
    		slider.addMouseMotionListener( this );
    		slider.addMouseListener( this );
    		pop.add( item );
    		pop.setDoubleBuffered( true );
    	}

    	public void showToolTip ( MouseEvent me ) {      
    		item.setText(""+threshold2value.get(slider.getValue()));
        
    		//limit the tooltip location relative to the slider
    		Rectangle b = me.getComponent().getBounds();
    		int x = me.getX();  
    		x = (x < b.x) ? b.x : (x > b.width) ? b.width : x;

        	pop.show( me.getComponent(), x - 5, -30 );
        	item.setArmed( false );
    	}
   
    	public void mouseDragged ( MouseEvent me ) {
    		showToolTip( me );
    	}
    	
      	public void mousePressed ( MouseEvent me ) {
    		showToolTip( me );
    	}
    	
    	public void mouseReleased ( MouseEvent me ) {
    		pop.setVisible( false );
    	}
   
    	public void mouseMoved ( MouseEvent me ) {}

    	public void mouseClicked ( MouseEvent me ) {}
   
    	public void mouseEntered ( MouseEvent me ) {}
   
    	public void mouseExited ( MouseEvent me ) {}
    }

}
