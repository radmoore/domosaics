package domosaics.ui.views.sequenceview.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.view.components.ZoomCompatible;




public class SaveFastaFileAction extends AbstractMenuAction implements ZoomCompatible {
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		SequenceView view = (SequenceView) ViewHandler.getInstance().getActiveView();

		File file = FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), "FASTA");
		if (file == null)
			return;

		SequenceI[] seqs = view.getSequences();
		
		new FastaWriter().write(file, seqs);
	}

}
