package domosaics.ui.util;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalSliderUI;

	/**
	 * Give the slider a nice look and show the current threshold as popup.
	 * 
	 * @author Andreas Held, Nicolas Terrapon
	 *
	 */

public class MyMetalSliderUI extends MetalSliderUI implements MouseMotionListener, MouseListener {
	final JPopupMenu pop = new JPopupMenu();
	JMenuItem item = new JMenuItem();

	public MyMetalSliderUI ( JSlider slider ) {
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

	@Override
	public void mouseDragged ( MouseEvent me ) {
		showToolTip( me );
	}
	
  	@Override
	public void mousePressed ( MouseEvent me ) {
		showToolTip( me );
	}
	
	@Override
	public void mouseReleased ( MouseEvent me ) {
		pop.setVisible( false );
	}

	@Override
	public void mouseMoved ( MouseEvent me ) {}

	@Override
	public void mouseClicked ( MouseEvent me ) {}

	@Override
	public void mouseEntered ( MouseEvent me ) {}

	@Override
	public void mouseExited ( MouseEvent me ) {}
}
