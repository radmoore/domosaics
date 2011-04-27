package angstd.ui.workspace.actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.ProjectElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;

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
				catch (IOException e) {}
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
				} catch (IOException e) {
					continue;
				}
			}


		}
		
	}
}
