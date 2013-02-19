package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.NoteEditor;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;




public class AddNoteAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public AddNoteAction (){
		super();
		putValue(Action.NAME, "Add / Change note");
		putValue(Action.SHORT_DESCRIPTION, "Allows the association of a note");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// open note window
		ArrangementComponent selectedDA = view.getArrangementSelectionManager().getClickedComp();
		
		new NoteEditor(view, selectedDA.getDomainArrangement()).showDialog(DoMosaicsUI.getInstance(), "Note editor");

		view.getDomainLayoutManager().visualChange();
	}

}
