package domosaics.ui.views.domainview.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.io.XdomWriter;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;




/**
 * Action which displays a save dialog and saves the 
 * visible arrangements as xdom.
 * 
 * @author Andreas Held
 *
 */
public class SaveXdomFileAction extends AbstractMenuAction { //implements ZoomCompatible {
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = ViewHandler.getInstance().getActiveView();
		ViewType type = view.getViewInfo().getType();
		
		String extension = type.getFileExtension();
		if ( extension == "DOMTREE")
			extension = "XDOM";
		
		File file = FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), type.getFileExtension());
		if (file == null)
			return;
		
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		List<DomainArrangement> visibleDAs = new ArrayList<DomainArrangement>();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();
			if (dac.isVisible())
				visibleDAs.add(dac.getDomainArrangement());
		}
		
		new XdomWriter().writeSimple(file, visibleDAs.toArray(new DomainArrangement[visibleDAs.size()]));
	}

}
