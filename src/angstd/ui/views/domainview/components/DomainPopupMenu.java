package angstd.ui.views.domainview.components;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import angstd.ui.views.domaintreeview.DomainTreeView;
import angstd.ui.views.domaintreeview.actions.TraceDomainAction;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.actions.context.ChangeShapeAction;
import angstd.ui.views.domainview.actions.context.ColorizeDomainAction;
import angstd.ui.views.domainview.actions.context.ConfirmPutativeDomain;
import angstd.ui.views.domainview.actions.context.ExportDomainSequencesAction;
import angstd.ui.views.domainview.actions.context.HideDomainAction;
import angstd.ui.views.domainview.actions.context.LookupDomainInGoogle;
import angstd.ui.views.domainview.actions.context.LookupDomainInPfamAction;
import angstd.ui.views.domainview.actions.context.LookupDomainInSourceDBAction;
import angstd.ui.views.domainview.actions.context.LookupDomainInUniprot;
import angstd.ui.views.domainview.actions.context.SearchOrthologousAction;
import angstd.ui.views.domainview.actions.context.SelectArrangementsContainingDomAction;



/**
 * DomainPopupMenu is the context menu which is shown on
 * right click at a domain component.
 * 
 * @author Andreas Held
 *
 */
public class DomainPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	

	/**
	 * Constructor for a new DomainPopupMenu
	 * 
	 * @param view
	 * 		the view displaying the context menu
	 */
	public DomainPopupMenu(DomainViewI view) {
		super("Domain Menu");

		// add title
		DomainComponent dc = view.getDomainSelectionManager().getClickedComp();
		JLabel title = new JLabel("<html><b>Domain <i>"+dc.getLabel()+"</i></b></html>");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setVerticalAlignment(JLabel.CENTER);
		add(title);
		add(new JSeparator());
			
		// add actions
		add(new HideDomainAction());
		add(new ChangeShapeAction());
		add(new ColorizeDomainAction());
		add(new JSeparator());
		
		if (view instanceof DomainTreeView)
			add(new TraceDomainAction());
		
		add(new SelectArrangementsContainingDomAction());
		add(new ExportDomainSequencesAction());
		
		add(new JSeparator());
		add(new SearchOrthologousAction());
		add(new JSeparator());
		add(new LookupDomainInGoogle(dc.getLabel()));
		add(new LookupDomainInSourceDBAction(dc.getLabel(),dc.getDomain().getFamily().getDomainType().getName()));
		add(new LookupDomainInUniprot(dc.getLabel()));
		//add(new JSeparator());
		//add(new ConfirmPutativeDomain());
	}

}