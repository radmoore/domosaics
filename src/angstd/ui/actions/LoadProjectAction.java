package angstd.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import angstd.model.workspace.io.ProjectImporter;
import angstd.ui.AngstdUI;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.FileDialogs;

public class LoadProjectAction  extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
    
	public void actionPerformed(ActionEvent e) {
		File file = FileDialogs.openChooseDirectoryDialog(AngstdUI.getInstance());
		if (file == null)
			return;
		
		ProjectImporter.read(file);
	}
}
