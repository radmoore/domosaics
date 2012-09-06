package angstd.ui.tools.RADSTool;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.configuration.Configuration;
import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.ui.ViewHandler;
import angstd.ui.tools.ToolFrame;
import angstd.ui.tools.changearrangement.components.ChangeArrangementMouseListener;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.actions.FitDomainsToScreenAction;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.renderer.additional.HighlightDomainRenderer;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.View;
import angstd.webservices.RADS.ui.RADSScanPanel;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSScanToolFrame extends ToolFrame{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RADSScanView radsScanView;
	private JPanel componentHolder;
	private ArrangementComponent arrComp;

	public RADSScanToolFrame() {
		super();
		componentHolder = new JPanel(new BorderLayout());
		componentHolder.setSize(583, 774);
	}
    
    public void addView(View view) {
    	this.radsScanView = (RADSScanView) view;
    	this.arrComp = radsScanView.getArrangementComponent();
    	setTitle(view.getViewInfo().getName());
    }
    
    public void setContent(JPanel content) {
    	RADSScanPanel scanPanel = (RADSScanPanel) content;
    	scanPanel.setParentFrame(this);
    	scanPanel.setRADSScanToolMode();
    	scanPanel.setQueryArrangement(arrComp.getDomainArrangement());
    	createQueryView();
    	componentHolder.add(content, BorderLayout.SOUTH);
    	this.setContentPane(componentHolder);
    	this.setResizable(false);
		setAlwaysOnTop(true);
    	this.pack();
    }
    
	public void createQueryView() {
		
		// TODO this does not work (see below)
		DomainViewI domView = (DomainViewI) ViewHandler.getInstance().getActiveView();

		System.out.println("This is the active view: "+domView.getViewComponent().getName());
		DomainArrangement[] daSet = new DomainArrangement[1];
		daSet[0] = arrComp.getDomainArrangement();
		SequenceI[] seq = new Sequence[1];
		seq[0] = daSet[0].getSequence();
		
		domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, "");
		domView.setDaSet(daSet);
		
		if (seq[0] != null)
			domView.loadSequencesIntoDas(seq, daSet);
		
		// customize the domain view so it fits to the needs of this view
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
