package domosaics.ui.workspace.actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import domosaics.model.configuration.Configuration;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;




public class SweepWorkspaceAction {

	private static String BCKDIR = "workspace_bck";
	
	public SweepWorkspaceAction() {
		boolean sweep = MessageUtil.showDialog("This will remove all projects from your \nworkspace FOLDER (not active projects). Do you want to continue?");
		
		if (sweep) {
			// get workspace dir
			String workspaceDirPath = Configuration.getInstance().getWorkspaceDir();
			String bckDirPath = workspaceDirPath+"/"+BCKDIR;
			File bckDir = new File(bckDirPath);

			// check if bckdir exists.
			// if no, create
			if (!bckDir.exists()) {
				if (!bckDir.mkdir()) {
					MessageUtil.showWarning("Failed to back up workspace");
					return;
				}	
			}
			// If yes, clean
			else {
				try {
					FileUtils.cleanDirectory(bckDir);
				} 
				catch (IOException e) {
					if (Configuration.getReportExceptionsMode(true))
						Configuration.getInstance().getExceptionComunicator().reportBug(e);
					else			
						Configuration.getLogger().debug(e.toString());
				}
			}
			// copy all non-active projects into bckdir
			// and delete if successfull
			for (ProjectElement project: WorkspaceManager.getInstance().getProjects()) {
				File projectDir = new File(workspaceDirPath+"/"+project.getTitle()); 
				if (projectDir.exists())
					continue;
				
				try {
					FileUtils.copyDirectoryToDirectory(projectDir, bckDir);
					FileUtils.deleteDirectory(projectDir);
				} 
				catch (IOException e) {
					if (Configuration.getReportExceptionsMode(true))
						Configuration.getInstance().getExceptionComunicator().reportBug(e);
					else			
						Configuration.getLogger().debug(e.toString());
					continue;
				}
			}


		}
		
	}
}
