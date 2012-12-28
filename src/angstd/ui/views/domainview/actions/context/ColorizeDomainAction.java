package angstd.ui.views.domainview.actions.context;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.util.AngstdColorPicker;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;



/**
 * Action which triggers the opening of a {@link AngstdColorPicker}
 * dialog. The color changing is then handled by the color chooser.
 * For detailed information on this please look into the 
 * {@link AngstdColorPicker} class.
 * 
 * @author Andreas Held
 *
 */
public class ColorizeDomainAction extends AbstractAction{
	private static final long serialVersionUID = 1L;

	public static final int DOMAIN = AngstdColorPicker.DOMAIN;
	
	public ColorizeDomainAction () {
		super();
		putValue(Action.NAME, "Change Color"); 
		putValue(Action.SHORT_DESCRIPTION, "Changes the domain color");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// get the starting color
		DomainComponent selection = view.getDomainSelectionManager().getClickedComp();
		Color oldColor = view.getDomainColorManager().getDomainColor(selection);
		new AngstdColorPicker(AngstdColorPicker.DOMAIN, view, oldColor).show(); 	
	
		view.getDomainSelectionManager().clearSelection();
		view.getParentPane().repaint();
	}
}
