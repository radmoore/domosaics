package angstd.ui.views.sequenceview.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaWriter;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.FileDialogs;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.views.view.components.ZoomCompatible;

public class SaveFastaFileAction extends AbstractMenuAction implements ZoomCompatible {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		SequenceView view = (SequenceView) ViewHandler.getInstance().getActiveView();

		File file = FileDialogs.showSaveDialog(view.getParentPane(), "FASTA");
		if (file == null)
			return;

		SequenceI[] seqs = view.getSequences();
		
		new FastaWriter().write(file, seqs);
	}

}
