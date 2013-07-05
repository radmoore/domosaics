package domosaics.ui.views.domainview.components;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.algos.overlaps.OverlapResolver;
import domosaics.localservices.codd.ConditionallyDependentDomainPairMap;
import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;




public class EvalueSliderTool extends JDialog implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	ButtonGroup groupRadio;
	private JRadioButton overlapRadioNone;
	private JRadioButton overlapRadioEvalue;
	private JRadioButton overlapRadioCoverage;
	//overlapRadioQuality;
	
	private JCheckBox coddCkb26, coddCkb27; 
	
	private JLabel threshold;
	
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
		groupRadio = new ButtonGroup();
	    overlapRadioNone = new JRadioButton("None", true);
	    overlapRadioEvalue = new JRadioButton("E-value based");
	    overlapRadioCoverage = new JRadioButton("Max. coverage");
	    groupRadio.add(overlapRadioNone);
	    groupRadio.add(overlapRadioEvalue);
	    groupRadio.add(overlapRadioCoverage);
	    overlapRadioNone.addActionListener(this);
	    overlapRadioCoverage.addActionListener(this);
	    overlapRadioEvalue.addActionListener(this);
	    overlapRadioNone.setActionCommand("None");
	    overlapRadioEvalue.setActionCommand("Evalue");
	    overlapRadioCoverage.setActionCommand("Coverage");
//	     overlapRadioQuality = new JRadioButton("Quality");
//       groupRadio.add(overlapRadioQuality);
//       overlapRadioQuality.setActionCommand("overlapRadioQuality");
		
		coddCkb26 = new JCheckBox("Pfam v26", false);
		coddCkb26.setToolTipText("Context dependent annotation, see [Terrapon et al., Bioinformatics, 2009]");
	    coddCkb26.addItemListener(new ItemListener(){	
			public void itemStateChanged(ItemEvent e) {
				if(coddCkb27.isSelected() && coddCkb26.isSelected())
					coddCkb27.setSelected(false);
				checkView();
				overlapRadioEvalue.setSelected(coddCkb26.isSelected());
				overlapRadioNone.setEnabled(!coddCkb26.isSelected());
				overlapRadioCoverage.setEnabled(!coddCkb26.isSelected());
	            if(coddCkb26.isSelected())
	            {
	             actionCODD("v26.0", false);
	            }else
	            {
	          	 processSlider(slider.getEvalue());
	             resolveOverlaps("Evalue");
	            }
			}
		});
	    coddCkb27 = new JCheckBox("Pfam v27", false);
	    coddCkb27.setToolTipText("Context dependent annotation, see [Terrapon et al., Bioinformatics, 2009]");
	    coddCkb27.addItemListener(new ItemListener(){	
			public void itemStateChanged(ItemEvent e) {
				if(coddCkb27.isSelected() && coddCkb26.isSelected())
					coddCkb26.setSelected(false);
				checkView();
				overlapRadioEvalue.setSelected(coddCkb27.isSelected());
				overlapRadioNone.setEnabled(!coddCkb27.isSelected());
				overlapRadioCoverage.setEnabled(!coddCkb27.isSelected());
	            if(coddCkb27.isSelected())
	            {
	             actionCODD("v27", false);
	            }else
	            {
	          	 processSlider(slider.getEvalue());
	             resolveOverlaps("Evalue");
	            }
			}
		});
	     
	    jbtApply = new JButton("Apply");
	    jbtApply.addActionListener(this);
	     
		slider = new EvalueSlider();
		slider.addChangeListener(this);
		
		Box sliderBox = new Box(BoxLayout.Y_AXIS);
		sliderBox.add(slider);
		sliderBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
		
	     
	    // layout the main panel
	    componentHolder.add(new JXTitledSeparator("Resolve overlaps by "),"growx, span, wrap, gaptop 10");
//	      componentHolder.add(new JLabel(" "), "gap 10");
	    componentHolder.add(overlapRadioNone,"gap 10, wrap");
	    componentHolder.add(overlapRadioEvalue,"gap 10, wrap");
	    componentHolder.add(overlapRadioCoverage,"gap 10, wrap");
//	      componentHolder.add(new JLabel(" "), "gap 10");
//	      componentHolder.add(jbtQuality, "gap 10, wrap");
	    componentHolder.add(new JLabel(" "), "gap 5, wrap");

	    JXTitledSeparator codd = new JXTitledSeparator("Co-Occurring Domain Detection");
	    codd.setToolTipText("Context dependent annotation, see [Terrapon et al., Bioinformatics, 2009]");
	    componentHolder.add(codd,"growx, span, wrap, gaptop 10");
	    componentHolder.add(coddCkb26, "gap 10, split 2");
	    componentHolder.add(coddCkb27, "gap 10, wrap");
	    componentHolder.add(new JLabel(" "), "gap 5, wrap");
		
	    componentHolder.add(new JXTitledSeparator("Adjust evalue "),"growx, span, wrap, gaptop 10");
	    threshold = new JLabel("Threshold: "+slider.getEvalueThreshold());
		componentHolder.add(threshold, "gap 10, gaptop 5, growx, span, wrap");
		componentHolder.add(sliderBox, "growx, span, wrap");
	    componentHolder.add(new JLabel(" "), "gap 255, wrap");
		
		componentHolder.add(new JXTitledSeparator("Apply settings"),"growx, span, wrap, gaptop 10");
//		componentHolder.add(new JLabel(" "), "gap 10");
		componentHolder.add(jbtApply, "gap 90, gaptop 10, wrap");
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
		slider.setValue(slider.getThreshold());
		processSlider(slider.getEvalueThreshold());
		return 0;
	}
	
	
	public void stateChanged(ChangeEvent e) {
		checkView();
		if( slider == (EvalueSlider) e.getSource()) {
			threshold.setText("Threshold: "+slider.getEvalue());
			processSlider(slider.getEvalue());
			if(coddCkb26.isSelected())
			{
				actionCODD("v26.0", slider.getValueIsAdjusting());
			}else
			{
				if(coddCkb27.isSelected())
				{
					actionCODD("v27", slider.getValueIsAdjusting());
				} else
				{
					resolveOverlaps(groupRadio.getSelection().getActionCommand());
				}
			}
		}
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
	public void actionPerformed(ActionEvent e)
	{
		checkView();	
		if(e.getSource() == overlapRadioCoverage)
		{
			processSlider(slider.getEvalue());
			resolveOverlaps("Coverage");
		}else
		{
			if(e.getSource() == overlapRadioEvalue)
			{  
				processSlider(slider.getEvalue());
				resolveOverlaps("Evalue");
			}else
			{
				if(e.getSource() == overlapRadioNone)
				{
					processSlider(slider.getEvalue());
				}else
				{
					if(e.getSource() == jbtApply)
					{
						slider.setThreshold(slider.getValue());
						dispose();
					}
				} 
			} 
		}	
		if(coddCkb26.isSelected())
		{
			actionCODD("v26.0", false);
		} else
		{
			if(coddCkb27.isSelected())
			{
				actionCODD("v27", false);
			}
		}
	}

	private void resolveOverlaps(String method)
    {
	 if(!method.equals("None"))
	 {
      DomainArrangement[] daSet = view.getDaSet();
      List<Domain> toRemove;
      for (DomainArrangement da : daSet)
      {
       if(method.equals("Coverage"))
        toRemove = OverlapResolver.resolveOverlapsByBestCoverage(da);
       else
        toRemove = OverlapResolver.resolveOverlapsByBestEvalue(da);
       for (Domain dom : toRemove)
       {
        da.hideDomain(dom);
        DomainComponent dc = view.getDomainComponentManager().getComponent(dom);
        dc.setVisible(false);
       }
      }
      // in proportional view it has to be a structural change
      view.getDomainLayoutManager().structuralChange();
	 }else
	 {
	  processSlider(slider.getEvalue());  
	 }
    }
    
    private void actionCODD(String version, boolean valueInAdjustment)
    {
    	//view.setDaSet(ConditionallyDependentDomainPairMap.coddProcedure(view.getDaSet()));
    	setAlwaysOnTop(false);
    	List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
    	if(!ConditionallyDependentDomainPairMap.coddProcedure(view.getDaSet(), DoMosaicsUI.getInstance(), version, arrList))
    		if(!valueInAdjustment)
    			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "No putative domains in this data set. Try with higher E-values.");
    	DomainArrangement[] daSet = arrList.toArray(new DomainArrangement[arrList.size()]);
    	setAlwaysOnTop(true);
    	for (DomainArrangement da : daSet)
    	{
    		//System.out.println(da.countDoms()+" "+da.getHiddenDoms().size());
    		for (Domain dom : da.getDomains())
    		{
    			DomainComponent dc = view.getDomainComponentManager().getComponent(dom);
    			dc.setVisible(true);
    		}
    	}
    	// in proportional view it has to be a structural change
    	view.getDomainLayoutManager().structuralChange();
    }
    
    private void checkView() {
		view = ViewHandler.getInstance().getActiveView();
		if(view.getViewInfo().getType()!=ViewType.DOMAINS && view.getViewInfo().getType()!=ViewType.DOMAINTREE)
			dispose();
    }
    
}
