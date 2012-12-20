package domosaics.ui.util;

import java.awt.Color;
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
		
		MySliderUI ms = new MySliderUI(this);
		setUI(ms);
		
	}
	
	public String getName() {
		return name;
	}
	
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
    		item.setText(""+slider.getValue());
        
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