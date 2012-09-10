package angstd.ui.tools.RADSTool;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.ui.ViewHandler;
import angstd.ui.tools.ToolFrame;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.actions.FitDomainsToScreenAction;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.view.View;
import angstd.webservices.RADS.ui.RADSScanPanel;

/**
 * RADSScanToolFrame is the parent frame of a RADSScanPanel when
 * called as a tool (from context menu of an arrangement).
 * 
 * See {@link RADSScanPanel}, {@link ToolFrame}
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSScanToolFrame extends ToolFrame{

	private static final long serialVersionUID = 1L;
	
	private RADSScanView radsScanView;
	private JPanel componentHolder;
	private ArrangementComponent arrComp;

	/**
	 * Constructs a new ToolFrame
	 */
	public RADSScanToolFrame() {
		super();
		componentHolder = new JPanel(new BorderLayout());
		componentHolder.setSize(533, 775);
	}
    
	
	/**
	 * Adds the view to this frame (see {@RADSScanView#setView})
	 * 
	 * @param view - an RADSScanView instance
	 */
    public void addView(View view) {
    	this.radsScanView = (RADSScanView) view;
    	this.arrComp = radsScanView.getArrangementComponent();
    	setTitle(view.getViewInfo().getName());
    }
    
    /**
     * Sets the Panel which is embedded in this ToolFrame.
     * 
     * @param content - instance of RADSScanPanel
     */
    public void setContent(JPanel content) {
    	RADSScanPanel scanPanel = (RADSScanPanel) content;
    	scanPanel.setParentFrame(this);
    	scanPanel.setRADSScanToolMode();
    	scanPanel.setQueryArrangement(arrComp.getDomainArrangement());
    	createQueryView();
    	componentHolder.add(content, BorderLayout.SOUTH);
    	componentHolder.add(content);
    	this.setContentPane(componentHolder);
    	this.setResizable(false);
		setAlwaysOnTop(true);
    	this.pack();
    }
    
    /**
     * Creates a small arrangement view to display the query arrangement
     * in the RADSScanPanel. The clicked arrangement is set in the constructor via 
     * {@link RADSScanView#getArrangementComponent()}
     */
	public void createQueryView() {
		
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();
		DomainArrangement[] daSet = new DomainArrangement[1];
		daSet[0] = arrComp.getDomainArrangement();
		SequenceI[] seq = new Sequence[1];
		seq[0] = daSet[0].getSequence();
		
		domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, "");
		domView.setDaSet(daSet);
		
		if (seq[0] != null)
			domView.loadSequencesIntoDas(seq, daSet);
		
		domView.getParentPane().removeToolbar();
		domView.removeMouseListeners();
		domView.getDomainLayoutManager().getActionManager().getAction(FitDomainsToScreenAction.class).setState(true);

		for (int i = 0; i < arrComp.getDomainArrangement().countDoms(); i++) {
			DomainFamily fam = arrComp.getDomainArrangement().getDomain(i).getFamily();			
			Color color = domView.getDomainColorManager().getDomainColor(fam);
			domView.getDomainColorManager().setDomainColor(fam, color);
		}
		
		componentHolder.add(domView.getParentPane(), BorderLayout.NORTH);
	}

}
