package angstd.ui.views.domainview.components;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

import angstd.algos.overlaps.OverlapResolver;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.DomainViewI;

public class EvalueSliderTool extends JDialog implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	protected JButton jbtApply;
	
	/** threshold slider */
	protected EvalueSlider slider;
	
	/** the view providing this feature */
	protected DomainViewI view;
	
	/** the box containing the slider */
	protected Box sliderBox;
	
	public EvalueSliderTool(DomainViewI view) {
		this.view = view;
		JPanel componentHolder = new JPanel();
		componentHolder.setLayout(new MigLayout());

		 // create components
	 //    jbtCancel = new JButton("Cancel");
	 //    jbtCancel.addActionListener(this);
	    jbtApply = new JButton("Apply");
	    jbtApply.addActionListener(this);
	     
		slider = new EvalueSlider();
		slider.addChangeListener(this);
		
		Box sliderBox = new Box(BoxLayout.Y_AXIS);
		sliderBox.add(slider);
		sliderBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
		
		
		componentHolder.add(new JXTitledSeparator("Adjust evalue "),"growx, span, wrap, gaptop 10");
		componentHolder.add(sliderBox, "gap 10, growx, span, wrap");
		
		componentHolder.add(new JXTitledSeparator("Apply settings"),"growx, span, wrap, gaptop 10");
//		componentHolder.add(new JLabel(" "), "gap 10");
		componentHolder.add(jbtApply, "gap 73, wrap");
//		componentHolder.add(new JLabel(" "), "gap 10, wrap");
//		componentHolder.add(jbtCancel, "gap 10, growx, wrap");

		// set up the dialog
		getContentPane().add(componentHolder);
		pack();
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
		setLocation(10, getLocation().y);
		this.setVisible(true);
		slider.setValue(slider.getOldThreshold());
		processSlider(slider.getEvalueOldThreshold());
		return 0;
	}
	
	
	public void stateChanged(ChangeEvent e) {
		EvalueSlider slider = (EvalueSlider) e.getSource();
		processSlider(slider.getEvalue());
	}
	
	protected void processSlider(double threshold) {
		// hide all domains higher than the threshold and show all domains below or equal the threshold
		DomainArrangement[] daSet = view.getDaSet();
		for (DomainArrangement da : daSet) {
			
			// first step show all domains
			for (Domain dom : da.getHiddenDoms())
				view.getDomainComponentManager().getComponent(dom).setVisible(true);
			da.showAllDomains();
			
			// second step hide all domains higher than the threshold
			Domain[] doms = da.getDomains().toArray(new Domain[da.getDomains().size()]);
			for (Domain dom : doms) 
				if (dom.getEvalue() > threshold) {
					da.hideDomain(dom);
					DomainComponent dc = view.getDomainComponentManager().getComponent(dom);
					dc.setVisible(false);
				}
		}
		
		// in proportional view it has to be a structural change
		view.getDomainLayoutManager().structuralChange(); 
	}

	
	/**
	 * Handles the button events
	 */
	public void actionPerformed(ActionEvent e) {
				if(e.getSource() == jbtApply) {
					slider.setOldThreshold(slider.getEvalue());
					dispose();
				}
	}

}
