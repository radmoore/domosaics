package angstd.ui.views.domainview.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import angstd.algos.distance.DistanceMeasureType;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.configuration.Configuration;
import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.manager.DomainSimilarityManager;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.pages.SelectNamePage;



/**
 * The similarity chooser dialog is used in compination with the 
 * {@link DomainSimilarityManager} which processes the dialog changes.
 * The dialog is just to interact with the user.
 * <p>
 * In general a distance measure and a threshold can be changed by the user
 * using this dialog. Using this dialog its also possible to export the 
 * actually uncollapsed arrangements into a new view and of course close
 * the similarity collapsing feature.
 * <p>
 * This class behaves as state change listener for the slider as well as
 * for the combo box triggering the correct methods within 
 * DomainSimilarityManager after a change.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * 
 *
 */
public class SimilarityChooser extends JDialog implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the list of distance measures */
	protected JComboBox distanceList;
	
	/** button to end this feature */
	protected JButton jbtClose;
	
	/** button to export the visible arrangements */
	protected JButton jbtExport;
	
	/** threshold slider */
	protected JSlider slider;
	
	/** the view providing this feature */
	protected DomainViewI view;
	
	/** the box containing the slider */
	protected Box sliderBox;
	
	
	/**
	 * Constructor for a new SimilarityChooser dialog.
	 * 
	 * @param view
	 * 		the view providing this feature
	 */
	public SimilarityChooser(DomainViewI view) {
		this.view = view;
		Container container = getContentPane();
		
		// create components
		distanceList = new JComboBox(DistanceMeasureType.values());
		if(view.getDomainSimilarityManager().getMemorizedSettingDMT()!=null)
			distanceList.setSelectedItem(view.getDomainSimilarityManager().getMemorizedSettingDMT());
		else
			distanceList.setSelectedItem(DistanceMeasureType.JACARD);
		distanceList.addActionListener(this);
		
		jbtClose = new JButton("Close");
		jbtClose.addActionListener(this);
		jbtExport = new JButton("Export");
		jbtExport.addActionListener(this);
		
		// create stroke style box
		Box distanceListBox = new Box(BoxLayout.Y_AXIS);
		distanceListBox.setBorder(BorderFactory.createTitledBorder(
				new LineBorder(Color.black, 1, true),
				"Similarity Measure:" 							
		)); 	
		distanceListBox.add(distanceList);

		sliderBox = new Box(BoxLayout.Y_AXIS);
		
		// create the button box
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(jbtClose);	
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(jbtExport);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		// create mainBox
		Box mainBox = new Box(BoxLayout.Y_AXIS);
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(distanceListBox);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(sliderBox);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(buttonBox);
		mainBox.add(Box.createVerticalGlue());
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		// set up the dialog
		container.add(mainBox);
		setSize(180, 220);
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(false);
		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				close();
			}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		updateDistances();
	}
	
	/**
	 * Create Sliderbox for usage with the jacard index
	 */
	public void createJaccardSliderBox() {
		if(sliderBox!=null && slider!=null)
			sliderBox.remove(slider);
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0"));
		labelTable.put(new Integer(50), new JLabel("50"));
		labelTable.put(new Integer(100), new JLabel("100"));
		
		slider = new JSlider(0, 100, 0);
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true); 
		slider.setUI(new MySliderUI(slider));
		slider.addChangeListener(this);
		
		sliderBox.setBorder(BorderFactory.createTitledBorder(
				new LineBorder(Color.black, 1, true),
				"Percentage collapse:" 							
		)); 	
		
		int tmp =view.getDomainSimilarityManager().getMemorizedSettingTjaccard();
		if( tmp != -1 ) {
			slider.setValue(tmp);
		}
		
		sliderBox.add(slider);
		sliderBox.doLayout();
		sliderBox.repaint();
	}
	
	/**
	 * Create Sliderbox for usage with the domain edit distance
	 */
	public void createDomainDistanceSliderBox() {
		if(sliderBox!=null && slider!=null)
			sliderBox.remove(slider);
		
		int max = view.getDomainSimilarityManager().getMaxDomainDistance();
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0"));
		labelTable.put(new Integer(max), new JLabel(""+max));

		slider = new JSlider(0, max, max);
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true); 
		slider.setUI(new MySliderUI(slider));
		slider.addChangeListener(this);
		
		sliderBox.setBorder(BorderFactory.createTitledBorder(
				new LineBorder(Color.black, 1, true),
				"edit operation collapse:" 							
		)); 	
		int tmp =view.getDomainSimilarityManager().getMemorizedSettingTdd();
		if( tmp != -1 )
			slider.setValue(tmp);
		sliderBox.add(slider);
		sliderBox.doLayout();
		sliderBox.repaint();
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
	
	/**
	 * Checks if the slider or the distance list were changed and triggers
	 * the correct method afterwards.
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(slider)) 
			processSlider();
		else 
			updateDistances();
	}
	
	/**
	 * Calls the setThreshold method within the DomainSimilarityManager
	 * after the threshold was changed.
	 */
	public void processSlider() {
			view.getDomainSimilarityManager().setThreshold(view, slider.getValue());
	}

	/**
	 * Initializes the DomainSimilarityManager with the new 
	 * distance measure.
	 */
	public void updateDistances() {
		DistanceMeasureType type = (DistanceMeasureType) distanceList.getSelectedItem();
		ArrangementComponent dac = view.getArrangementSelectionManager().getSelection().iterator().next();
		view.getDomainSimilarityManager().init(view, dac.getDomainArrangement(), type);
		// change to the correct slider
		switch(type) {
			case JACARD: createJaccardSliderBox(); break;
			case DOMAINDISTANCE: createDomainDistanceSliderBox(); break;
		}
		processSlider();
	}
	
	/**
	 * Exports all visible arrangements to a new view.
	 */
	protected void export() {
		List<DomainArrangement> daSet = new ArrayList<DomainArrangement>();
		List<SequenceI> seqs = new ArrayList<SequenceI>();
		
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();
			
			if (!dac.isVisible())
				continue;
			
			try {
				DomainArrangement da = dac.getDomainArrangement();
				if (da.getSequence() != null)
					seqs.add((Sequence) da.getSequence().clone());
				daSet.add((DomainArrangement)da.clone());
			} 
			catch (CloneNotSupportedException e) {
				Configuration.getLogger().debug(e.toString());
			}
			
		}
		
		if (daSet.size() == 0) {
			MessageUtil.showWarning("No arrangements left");
			return;
		}
		
		// take the active viewName + subset as default name
		String defaultName = view.getViewInfo().getName()+"_subset";
		
		// ask the user to enter a valid name for the view
		String viewName=null;
		String projectName=null;
		
		// get active project 
		ViewElement elem = WorkspaceManager.getInstance().getViewElement(view.getViewInfo());
		ProjectElement project = elem.getProject();
		
		// get user settings
		while(viewName == null) {
			Map m = WizardManager.getInstance().selectNameWizard(defaultName, "domain view", project, true);
			viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
			projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
		}
		
		project = WorkspaceManager.getInstance().getProject(projectName);
		
		/* Never happen
		 * if (viewName == null)
			return; 
		 */

		// and create with the new dataset a new domainview with the selected name
		DomainViewI newView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		newView.setDaSet(daSet.toArray(new DomainArrangement[daSet.size()]));
		
		//if there are sequences loaded clone them as well
		if (view.isSequenceLoaded()) 
			newView.loadSequencesIntoDas(seqs.toArray(new SequenceI[seqs.size()]), newView.getDaSet());
		
		ViewHandler.getInstance().addView(newView, project);
		
		close();
	}
	
	/**
	 * Closes the dialog as well as the manager by triggering the end() method
	 * within the DomainSimilarityManager.
	 */
	public void close() {
		view.getDomainSimilarityManager().end(view);
	}
	
	/**
	 * Handles the button events
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == distanceList)
			updateDistances();
		if(e.getSource() == jbtClose) 
			close();
		if(e.getSource() == jbtExport) 
			export();
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
