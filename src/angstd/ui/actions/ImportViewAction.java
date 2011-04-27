package angstd.ui.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.ProjectElement;
import angstd.ui.AngstdUI;
import angstd.ui.WorkspaceManager;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.view.io.ViewImporter;

public class ImportViewAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
    
	public void actionPerformed(ActionEvent e) {
		// determine the view type
		try {
			File viewFile = FileDialogs.showOpenDialog(AngstdUI.getInstance());
			if (viewFile == null)
				return;
			
			BufferedReader in = new BufferedReader(new FileReader(viewFile));
			String firstLine;
			
			ProjectElement project = WorkspaceManager.getInstance().getSelectionManager().getSelectedProject();
			
			if ((firstLine = in.readLine()) != null) {
				in.close();
				if (firstLine.contains("SEQUENCEVIEW"))
					ViewImporter.readSequenceView(viewFile, project); 
				else if (firstLine.contains("DOMAINTREEVIEW"))
					ViewImporter.readDomainTreeView(viewFile, project); 
				else if (firstLine.contains("TREEVIEW"))
					ViewImporter.readTreeView(viewFile, project); 
				else if (firstLine.contains("DOMAINVIEW"))
					ViewImporter.readDomainView(viewFile, project); 
				else {
					MessageUtil.showWarning("Couldn't determine view format.");
					return;
				}
				
			}
		} catch(Exception ex) {
			Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("view import aborted");
		}
	}

}
