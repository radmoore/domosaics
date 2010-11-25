package angstd.ui.tools.changearrangement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.ui.ViewHandler;
import angstd.ui.tools.Tool;
import angstd.ui.tools.ToolFrameI;
import angstd.ui.tools.changearrangement.components.ChangeArrangementMouseListener;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.actions.FitDomainsToScreenAction;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.renderer.additional.HighlightDomainRenderer;
import angstd.ui.views.view.AbstractView;
import angstd.ui.views.view.View;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;

/**
 * The ChangeArrangementView consists of a panel where changes can be made,
 * a help panel explaining how changes can be made and a small domain
 * view rendering the selected arrangement.
 * <p>
 * Wrapper methods around the backend and local domain view 
 * allow the correct workflow with easy access to the necessary
 * managers.
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementView extends AbstractView implements Tool{
	private static final long serialVersionUID = 1L;
	
	/** the parent tool frame */
	protected ChangeArrangementFrame parentFrame;
	
	/** the panel allowing the changing of domain attributes */
	protected ChangeArrangementPanel changePanel;
	
	/** the arrangement being edited */
	protected DomainArrangement da;
	
	/** the domainview from which the selected arrangement comes from */
	protected DomainViewI backendDomView;
	
	/** the new domain view used to display the selected arrangement */
	protected DomainViewI domView;
	
	/** panel holding all view components, such as the change and help panel as well as the domain view rendering the selected arrangement */
	protected JPanel componentHolder;
	
	protected DomainArrangement backupDA;
	
	
	/**
	 * Constructor for a new ChangeArrangementView initializing the panel
	 * except of the domain view which renders the selected arrangement.
	 * The initialization of this part takes place when setView() is invoked.
	 */
	public ChangeArrangementView() {
		componentHolder = new JPanel(new BorderLayout());
		componentHolder.setBackground(Color.white);
		
		componentHolder.add(changePanel = new ChangeArrangementPanel(this), BorderLayout.CENTER);
		componentHolder.add(new ChangeArrangementHelpPanel(), BorderLayout.EAST);
	}
	
	/**
	 * @see View
	 */
	public void export(File file) {}
	
	/**
	 * Initializes the domain view which renders the selected arrangement.
	 * This method triggers also the embedding into the tool frame.
	 * 
	 * @param view
	 * 		the domainview from which the selected arrangement comes from 
	 * @param da
	 * 		the selected arrangement
	 */
	public void setView(DomainViewI view, ArrangementComponent da) {
		this.backendDomView = view;
		this.da = da.getDomainArrangement();
		
		try {
			this.backupDA = (DomainArrangement) da.getDomainArrangement().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		// create a domain view with just one arrangement
		DomainArrangement[] daSet = new DomainArrangement[1];
		daSet[0] = da.getDomainArrangement();
		SequenceI[] seq = new Sequence[1];
		seq[0] = daSet[0].getSequence();
		
		domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, "");
		domView.setDaSet(daSet);
		
		if (seq[0] != null)
			domView.loadSequencesIntoDas(seq, daSet);
		
		// customize the domain view so it fits to the needs of this view
		
		/// remove the views specific menu
		domView.getParentPane().removeToolbar();
		
		/// remove the mouse listeners and add a special one 
		domView.removeMouseListeners();
		((AbstractView)domView).addMouseListener(new ChangeArrangementMouseListener(domView, this));
		((AbstractView)domView).addMouseMotionListener(new ChangeArrangementMouseListener(domView, this));
		
		/// remove additional renderers but keep the domain selection renderer
		domView.removeAllRenderer();
		domView.addRenderer(new HighlightDomainRenderer(domView));
		
		/// change default style to fit to screen
		domView.getDomainLayoutManager().getActionManager().getAction(FitDomainsToScreenAction.class).setState(true);
		
		/// take the domain colors from the original view
		for (int i = 0; i < da.getDomainArrangement().countDoms(); i++) {
			DomainFamily fam = da.getDomainArrangement().getDomain(i).getFamily();
			Color color = view.getDomainColorManager().getDomainColor(fam);
			domView.getDomainColorManager().setDomainColor(fam, color);
		}
		
		// change the border for this view
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
		domView.getParentPane().setBorder(compound);
			
		// add the view to the views mainpanel and then the main panel to the frame
		componentHolder.add(domView.getParentPane(), BorderLayout.SOUTH);
		parentFrame.addView(this);
		parentFrame.setContent(componentHolder);
	}
	
	public void restore() {
		clearSelection();
		
		// clear the component manager from both views
		domView.getDomainComponentManager().clear();
		
		for (Domain dom : da.getDomains())
			backendDomView.getDomainComponentManager().removeComponent(dom);
		
		// clear the original da and clone the domains from the backup da
		try {
			SequenceI seq = null;
			if (da.getSequence() != null)
				seq = (SequenceI) da.getSequence().clone();
			
			da.clear();
			for (Domain dom : backupDA.getDomains()) {
				da.addDomain((Domain) dom.clone());
			}
			
			if (da.getSequence() != null)
				da.setSequence(seq);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// structural change on both views
		backendDomView.getDomainLayoutManager().structuralChange();
		domView.getDomainLayoutManager().structuralChange();
	}
	
	/**
	 * Wrapper around the ChangePanel to refresh the domain attributes,
	 * e.g. when the user selects a domain.
	 * 
	 * @param dom
	 * 		the selected domain
	 */
	public void refreshDomain(Domain dom) {
		changePanel.refreshDomain(dom);
	}
	
	/**
	 * Returns the arrangement being edited
	 * 
	 * @return
	 * 		arrangement being edited
	 */
	public DomainArrangement getDA() {
		return da;
	}
	
	/**
	 * Returns the currently selected domain or null if no domain
	 * is selected.
	 * 
	 * @return
	 * 		currently selected domain or null
	 */
	public Domain getSelectedDomain() {
		if (domView.getDomainSelectionManager().getClickedComp()==null)
			return null;
		return domView.getDomainSelectionManager().getClickedComp().getDomain();
	}
	
	/**
	 * Refreshs the backend domain view and the local domain view rendering the 
	 * selected arrangement. Therefore changes can be applied to both views.
	 * 
	 * @param changedDomain
	 * 		the changed or added domain
	 */
	public void refreshViews(Domain changedDomain) {
		backendDomView.getDomainLayoutManager().structuralChange();
		
		Color color = backendDomView.getDomainColorManager().getDomainColor(changedDomain);
		domView.getDomainColorManager().setDomainColor(changedDomain.getFamily(), color);
		domView.getDomainLayoutManager().structuralChange();
		
		clearSelection();
	}
	
	/**
	 * Clears the domain selection
	 */
	public void clearSelection() {
		domView.getDomainSelectionManager().setClickedComp(null);
		domView.getDomainSelectionManager().setMouseOverComp(null);
	}
	
	/**
	 * @see AbstractView
	 */
	public void registerMouseListeners() { }

	/**
	 * @see AbstractView
	 */
	public void renderView(Graphics2D g) { }

	/**
	 * @see Tool
	 */
	public ToolFrameI getToolFrame() {
		return parentFrame;
	}

	/**
	 * @see Tool
	 */
	public void setToolFrame(ToolFrameI frame) {
		parentFrame = (ChangeArrangementFrame) frame;
	}

	/**
	 * @see ViewI
	 */
	public void setViewLayout(ViewLayout layout) { }

	
	public void setViewRenderer(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

}
