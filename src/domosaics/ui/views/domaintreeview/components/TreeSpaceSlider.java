package domosaics.ui.views.domaintreeview.components;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.ui.views.domaintreeview.DomainTreeViewI;


public class TreeSpaceSlider extends JDialog implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;

	protected JButton jbtApply, jbtCancel;

	protected JSlider spaceSlider;
	
	/** the view providing this feature */
	protected DomainTreeViewI view;
	
	protected int actThres;
	protected int oldSpace;
	
	protected JPanel componentHolder;
	
	
	public TreeSpaceSlider(DomainTreeViewI view) {
		this.view = view;
		oldSpace = (int) Math.round(100 / view.getDomainTreeLayoutManager().getTreeSpace());
		actThres = oldSpace;

		// create components
		jbtCancel = new JButton("Cancel");
		jbtCancel.addActionListener(this);
		
		jbtApply = new JButton("Apply");
		jbtApply.addActionListener(this);
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(1), new JLabel("1"));
		labelTable.put(new Integer(25), new JLabel("25"));
		labelTable.put(new Integer(50), new JLabel("50"));
		labelTable.put(new Integer(75), new JLabel("75"));
		labelTable.put(new Integer(100), new JLabel("100"));
		
		spaceSlider = new JSlider(1, 100, actThres);
		spaceSlider.setLabelTable(labelTable);
		spaceSlider.setPaintLabels(true); 
		spaceSlider.setUI(new MySliderUI(spaceSlider));
		spaceSlider.addChangeListener(this);
		
		componentHolder = new JPanel(new MigLayout());

		// layout the panel depending on the chosen algorithm
		componentHolder.add(new JXTitledSeparator("Change tree space "),"growx, span, wrap, gaptop 10");
		componentHolder.add(spaceSlider, "gap 10, gapright10, growx, span, wrap");
		
		componentHolder.add(new JXTitledSeparator("Apply settings"),"growx, span, wrap, gaptop 10");
		//componentHolder.add(new JLabel(" "), "gap 10");
		componentHolder.add(jbtApply, "w 90!, gap 20");
		componentHolder.add(jbtCancel, "gap 10");
		componentHolder.add(new JLabel(" "), "gap 10, wrap");
		
		getContentPane().add(componentHolder);
		pack();
		
		// set up the dialog
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		setLocation(50, getLocation().y);
		this.setVisible(true);
		return 0;
	}
	
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();

		if (actThres == slider.getValue())
			return;
		actThres = slider.getValue();
		
		view.getDomainTreeLayoutManager().setTreeSpace(actThres);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jbtApply) {
			this.dispose();
			return;
		}
		
		if(e.getSource() == jbtCancel) {
			view.getDomainTreeLayoutManager().setTreeSpace(oldSpace);
			this.dispose();
			return;
		}
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
    		item.setText(slider.getValue()+"%");
        
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
