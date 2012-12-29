package domosaics.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import domosaics.model.workspace.io.ProjectImporter;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.FileDialogs;




public class LoadProjectAction  extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
    
	public void actionPerformed(ActionEvent e) {
		File file = FileDialogs.openChooseDirectoryDialog(DoMosaicsUI.getInstance());
		if (file == null)
			return;
		
		ProjectImporter.read(file);
	}
}
