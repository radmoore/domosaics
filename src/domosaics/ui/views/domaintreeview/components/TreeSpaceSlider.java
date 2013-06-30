package domosaics.ui.views.domaintreeview.components;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.model.configuration.Configuration;
import domosaics.ui.util.MyMetalSliderUI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.treeview.TreeViewI;

public class TreeSpaceSlider extends JDialog implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;

	protected JButton jbtApply, jbtCancel;

	protected JSlider spaceSlider;
	
	/** the view providing this feature */
	protected TreeViewI view;
	
	protected int actThres;
	protected int oldSpace;
	
	protected JPanel componentHolder;
	
	public static TreeSpaceSlider instance;
	
	
	public TreeSpaceSlider(TreeViewI view) {
		this.view = view;
		if(view instanceof DomainTreeViewI)
			oldSpace = (int) Math.round(100 / ( (DomainTreeViewI) view).getDomainTreeLayoutManager().getTreeSpace());
		else
			oldSpace = (int) Math.round(100 / view.getTreeLayoutManager().getTreeSpace());
		actThres = oldSpace;
		instance = this;
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
		spaceSlider.setUI(new MyMetalSliderUI(spaceSlider));
	    try {
			UIManager.setLookAndFeel(save);
		} 
	    catch (UnsupportedLookAndFeelException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
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
		this.addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) {
        		instance=null;
        	}
        	
        	public void windowActivated(WindowEvent e) { }
        	
        	public void windowClosed(WindowEvent e) { 
        		instance=null;
        	}

        	public void windowDeactivated(WindowEvent e) { }

        	public void windowDeiconified(WindowEvent e) { }

        	public void windowIconified(WindowEvent e) { }
        	
        	public void windowOpened(WindowEvent e) { }
		});
	}
	
	public TreeViewI getView() {
		return view;
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
		if(view instanceof DomainTreeViewI)
			((DomainTreeViewI)view).getDomainTreeLayoutManager().setTreeSpace(actThres);
		else
			view.getTreeLayoutManager().setTreeSpace(actThres);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jbtApply) {
			this.dispose();
			return;
		}
		
		if(e.getSource() == jbtCancel) {
			if(view instanceof DomainTreeViewI)
				((DomainTreeViewI)view).getDomainTreeLayoutManager().setTreeSpace(oldSpace);
			else
				view.getTreeLayoutManager().setTreeSpace(oldSpace);
			this.dispose();
			return;
		}
	}

}
