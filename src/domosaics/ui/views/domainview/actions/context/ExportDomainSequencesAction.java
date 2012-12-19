package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.sequence.Sequence;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;


/**
 * Action which exports all sequences for the specified domain.
 * 
 * @author Andreas Held
 *
 */
public class ExportDomainSequencesAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ExportDomainSequencesAction () {
		super();
		putValue(Action.NAME, "Export all sequences");
		putValue(Action.SHORT_DESCRIPTION, "Selects all domains of this family and exports the sequences");
	}

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		// from what domain shall be the sequences exported?
		DomainComponent toSearchDomain = view.getDomainSelectionManager().getClickedComp();

		File file = FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), "fasta");
		if (file == null)
			return;
	
		// now clean up the selected domain
		view.getDomainSelectionManager().clearSelection();
	
		// and find all domains which were requested by the user
		Iterator<DomainComponent> iter = view.getArrangementComponentManager().getDomainComponentsIterator();
		while(iter.hasNext()) {
			DomainComponent dc = iter.next();
			
			if(!dc.isVisible())
				continue;
			
			if (dc.getLabel().toUpperCase().equals(toSearchDomain.getLabel().toUpperCase())) 
				view.getDomainSelectionManager().addToSelection(dc);
		}
		
		// extract the sequences from those domains
		iter = view.getDomainSelectionManager().getSelectionIterator();
		SequenceI[] domSeqs = new Sequence[view.getDomainSelectionManager().getSelection().size()];
		int i = 0;
		while(iter.hasNext()) {
			DomainComponent dc = iter.next();
			// create the new label protein_domain_from_to
			String seqName = dc.getDomain().getArrangement().getName()+"_"+dc.getLabel()+"_"+dc.getDomain().getFrom()+"-"+dc.getDomain().getTo();
			domSeqs[i] = new Sequence(seqName, dc.getDomain().getSequence().getSeq(false));
			i++;
		}
		
		
		
		// cancel the selection so we don't run in painting bugs
		view.getDomainSelectionManager().clearSelection();
		new FastaWriter().write(file, domSeqs);
		
		view.getParentPane().repaint();
	}
}
