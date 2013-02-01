package domosaics.model.workspace.io;

import java.io.File;

import org.apache.commons.io.FileUtils;

import domosaics.model.configuration.Configuration;
import domosaics.model.workspace.CategoryElement;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.DigestUtil;
import domosaics.ui.util.MessageUtil;




public class ProjectExporter {
	
	public static String PROJECTFILE = ".domosaics_project";
	
	
	public static boolean write(ProjectElement project) {
		String viewFail=""; 
        try {
        	// check for the project within the workspace directory, create it if necessary
        	String workspaceDir = Configuration.getInstance().getWorkspaceDir();
        	
        	String projectDirPath = workspaceDir+"/"+project.getTitle();
        	File projectDir = new File(projectDirPath);
        	
        	// if the project dir already exists
        	if (projectDir.exists()) {
        		try {
        			FileUtils.cleanDirectory(projectDir);
        		}
        		catch (Exception e) {
        			Configuration.getLogger().debug(e.toString());
        			MessageUtil.showWarning("Could not remove old project directory - will append");
        		}
        	}
        	else {
        		if (!projectDir.mkdir())
        			return false;
        	}
        	
			// create digest of project name to avoid empty file
        	DigestUtil.createAndAddDigest(project.getTitle(), projectDir, PROJECTFILE);
        	
        	
        	// export all categories and the views within
        	for (WorkspaceElement child : project.getChildren()) {
        		CategoryElement cat = (CategoryElement) child;
        		String catDir = projectDirPath+"/"+cat.getTitle();
        		
        		// check if category folder exists else create it
        		if (!new File(catDir).exists()) {
                	if (!new File(catDir).mkdir())
                		return false;
                }
        		
        		// export the views into the category directory
        		for (int i = 0; i < cat.getChildCount(); i++) {
        			ViewElement viewElt = (ViewElement) cat.getChildAt(i);
        			
        			// if view exists ask if it should be overwritten
        			File viewFile = new File(catDir+"/"+viewElt.getTitle());
        			viewFail=viewElt.getTitle();
//            		if (viewFile.exists()) 
//                    	if (!MessageUtil.showDialog("View "+viewElt.getTitle()+" already exists. Overwrite it?"))
//                    		continue;
                    
                    // export the view
                    ViewHandler.getInstance().getView(viewElt.getViewInfo()).export(viewFile);
        		}
        	}
 
        } 
        catch (Exception e) {
			e.printStackTrace();
        	Configuration.getLogger().debug("Fail to export view: "+viewFail);
        	Configuration.getLogger().debug(e.toString());
        	return false;
        }
        return true;
    }
	
	public static void write(File file, ProjectElement project, String exportName) {
		
        try {
        	// check for the project within the workspace directory, create it if necessary
        	String fileDir = file.getPath();
        	
        	//String projectDir = fileDir+"/"+project.getTitle();
        	String projectDirName = fileDir+"/"+exportName;
        	File projectDir = new File(projectDirName);
        	if (!projectDir.exists()) {
        		projectDir.mkdir();
        	}

			// create digest of project name to avoid empty file
        	DigestUtil.createAndAddDigest(project.getTitle(), projectDir, PROJECTFILE);
        	
        	// export all categories and the views within
        	for (WorkspaceElement child : project.getChildren()) {
        		CategoryElement cat = (CategoryElement) child;
        		String catDir = projectDir+"/"+cat.getTitle();
        		
        		// check if category folder exists else create it
        		if (!new File(catDir).exists()) {
                	new File(catDir).mkdir();
                }
        		
        		// export the views into the category directory
        		for (int i = 0; i < cat.getChildCount(); i++) {
        			ViewElement viewElt = (ViewElement) cat.getChildAt(i);
        			
        			// if view exists ask if it should be overwritten
        			//System.out.println(catDir+"/"+viewElt.getTitle());
        			File viewFile = new File(catDir+"/"+viewElt.getTitle());
            		if (viewFile.exists()) 
                    	if (!MessageUtil.showDialog("View "+viewElt.getTitle()+" already exists. Overwrite it?"))
                    		continue;
                    
                    // export the view
                    ViewHandler.getInstance().getView(viewElt.getViewInfo()).export(viewFile);
        		}
        	}
 
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}

