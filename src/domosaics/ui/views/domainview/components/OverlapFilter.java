package domosaics.ui.views.domainview.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.algos.overlaps.OverlapResolver;
import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.domainview.DomainViewI;




   public class OverlapFilter extends JDialog implements ChangeListener, ActionListener
   {
	protected JButton jbtApply, jbtCoverage, jbtEvalue, jbtCancel;
	//protected JButton  jbtQuality;

	/** the view providing this feature */
	protected DomainViewI view;
	
	public OverlapFilter(DomainViewI view)
	{
	 this.view = view;
	 
	 JPanel componentHolder = new JPanel();
	 componentHolder.setLayout(new MigLayout());
		
	 // create components
     jbtCancel = new JButton("Cancel");
     jbtCancel.addActionListener(this);
     
     jbtApply = new JButton("Apply");
     jbtApply.addActionListener(this);
	
     jbtCoverage = new JButton("Coverage");
     jbtCoverage.addActionListener(this);
	
     jbtEvalue = new JButton("E-Value");
     jbtEvalue.addActionListener(this);
	
//     jbtQuality = new JButton("Quality");
//     jbtQuality.addActionListener(this);
     
     // layout the main panel
     componentHolder.add(new JXTitledSeparator("Resolve overlaps by "),"growx, span, wrap, gaptop 10");
//     componentHolder.add(new JLabel(" "), "gap 10");
     componentHolder.add(jbtCoverage,"gap 1");
     componentHolder.add(new JLabel("or"),"gap 1");
     componentHolder.add(jbtEvalue, "gap 1, wrap");
     componentHolder.add(new JLabel(" "), "gap 10, wrap");
     componentHolder.add(jbtCancel, "gap 100, wrap");
//     componentHolder.add(jbtQuality, "gap 10, wrap");
     
	 componentHolder.add(new JXTitledSeparator("Apply settings"),"growx, span, wrap, gaptop 10");
//	   componentHolder.add(new JLabel(" "), "gap 10");
	 componentHolder.add(jbtApply, "gap 73, wrap");
	
     // set up the dialog
     getContentPane().add(componentHolder);
     pack();
     setResizable(false);
     setAlwaysOnTop(true);
     setModal(false);
     setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
	
	/**
	 * Handles the button events
	 */
    @Override
	public void actionPerformed(ActionEvent e)
    {
     if(e.getSource() == jbtCoverage)
     {
      actionCancel();
      resolveOverlaps("Coverage");
     }else
     {
      if(e.getSource() == jbtEvalue)
      {
       actionCancel();    
       resolveOverlaps("Evalue");
      }else
      {
       if(e.getSource() == jbtCancel)
       {
        actionCancel();
       }else
       {
        if(e.getSource() == jbtApply)
        {
         dispose();
        }
       } 
      } 
     }
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
		return 0;
	}
     
    private void resolveOverlaps(String method)
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
    }
    
    private void actionCancel()
    {
     DomainArrangement[] daSet = view.getDaSet();
     for (DomainArrangement da : daSet)
     {
      da.showAllDomains();
      for (Domain dom : da.getDomains())
      {
       DomainComponent dc = view.getDomainComponentManager().getComponent(dom);
       dc.setVisible(true);
      }
     }
     // in proportional view it has to be a structural change
     view.getDomainLayoutManager().structuralChange();
    }

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
   }
