package angstd.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.io.XdomWriter;
import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.FileDialogs;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;

/**
 * Action which displays a save dialog and saves the 
 * visible arrangements as xdom.
 * 
 * @author Andreas Held
 *
 */
public class SaveXdomFileAction extends AbstractMenuAction { //implements ZoomCompatible {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		ViewType type = view.getViewInfo().getType();
		
		File file = FileDialogs.showSaveDialog(AngstdUI.getInstance(), type.getFileExtension());
		if (file == null)
			return;
		
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		List<DomainArrangement> visibleDAs = new ArrayList<DomainArrangement>();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();
			if (dac.isVisible())
				visibleDAs.add(dac.getDomainArrangement());
		}
		
		new XdomWriter().write(file, visibleDAs.toArray(new DomainArrangement[visibleDAs.size()]));
	}

}
