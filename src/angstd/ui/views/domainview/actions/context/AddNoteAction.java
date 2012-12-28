package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.util.NoteEditor;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;



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

		view.getArrangementSelectionManager().getSelection().clear();
		view.getArrangementSelectionManager().getSelection().add(selectedDA);
		
		new NoteEditor(view, selectedDA.getDomainArrangement()).showDialog(AngstdUI.getInstance(), "Note editor");

		view.getDomainLayoutManager().visualChange();
	}

}
