package domosaics.ui.views.domainview.components;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.actions.context.AddNoteAction;
import domosaics.ui.views.domainview.actions.context.ChangeArrangementAction;
import domosaics.ui.views.domainview.actions.context.DeleteArrangementContextAction;
import domosaics.ui.views.domainview.actions.context.EditSequenceAction;
import domosaics.ui.views.domainview.actions.context.LookupProteinInGoogle;
import domosaics.ui.views.domainview.actions.context.LookupProteinInUniprotAction;
import domosaics.ui.views.domainview.actions.context.RADSScanAction;
import domosaics.ui.views.domainview.actions.context.ShowAllDomainsAction;
import domosaics.ui.views.view.manager.SelectionManager;




/**
 * ArrangementPopupMenu is the context menu which is shown on
 * right click at a domain arrangement.
 * 
 * @author Andreas Held
 *
 */

public class ArrangementPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for a new ArrangementPopupMenu
	 * 
	 * @param view
	 * 		the view displaying the context menu
	 */
	public ArrangementPopupMenu(DomainViewI view) {
		super("Arrangement Menu");

		// add title
		SelectionManager<ArrangementComponent> arSel = view.getArrangementSelectionManager();
		ArrangementComponent ac = arSel.getClickedComp();
		String str = "<html><b>Arrangement for protein <i>"+ac.getLabel()+"</i></b></html>";
		JLabel title = new JLabel(str);
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setVerticalAlignment(JLabel.CENTER);
		add(title);
		add(new JSeparator());
		
		// add menu entries
		add(new AddNoteAction());
		add(new DeleteArrangementContextAction());
		add(new ChangeArrangementAction());
		add(new EditSequenceAction());
		add(new JSeparator());
		add(new ShowAllDomainsAction());
		add(new JSeparator());
		add(new RADSScanAction());
		add(new LookupProteinInGoogle(ac.getLabel()));
		add(new LookupProteinInUniprotAction(ac.getLabel()));
	}
	
}