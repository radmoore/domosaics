package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.io.File;



import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;





/**
 * Action for saving arrangements as fasta, if sequence information is available
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class SaveArrFastaFileAction extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		
		if (!view.isSequenceLoaded()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"There are no sequence loaded in this view.");
			return;
		}
		
		File file = FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), "FASTA");
		if (file == null)
			return;
		
		SequenceI[] seqs = view.getSequences();
		new FastaWriter().wrappedWrite(file, seqs, 60);
	}

}
