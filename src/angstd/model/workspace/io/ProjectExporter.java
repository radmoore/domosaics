package angstd.model.workspace.io;

import java.io.File;
import org.apache.commons.io.FileUtils;
import angstd.model.configuration.Configuration;
import angstd.model.workspace.CategoryElement;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;

public class ProjectExporter {
	
	public static boolean write(ProjectElement project) {
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
        			MessageUtil.showWarning("Could not remove old project directory - will append");
        		}
        	}
        	else {
        		if (!projectDir.mkdir()) {
        			return false;
        		}
        	}
        	
        	// export all categories and the views within
        	for (WorkspaceElement child : project.getChildren()) {
        		CategoryElement cat = (CategoryElement) child;
        		String catDir = projectDirPath+"/"+cat.getTitle();
        		
        		// check if category folder exists else create it
        		if (!new File(catDir).exists()) {
                	System.out.println("created category directory: "+cat.getTitle());
                	if (!new File(catDir).mkdir())
                		return false;
                }
        		
        		// export the views into the category directory
        		for (int i = 0; i < cat.getChildCount(); i++) {
        			ViewElement viewElt = (ViewElement) cat.getChildAt(i);
        			
        			// if view exists ask if it should be overwritten
        			File viewFile = new File(catDir+"/"+viewElt.getTitle());
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
        	return false;
        }
        return true;
    }
	
	public static void write(File file, ProjectElement project, String exportName) {
		
        try {
        	// check for the project within the workspace directory, create it if necessary
        	String fileDir = file.getPath();
        	
        	//String projectDir = fileDir+"/"+project.getTitle();
        	String projectDir = fileDir+"/"+exportName;
        	if (!new File(projectDir).exists()) {
        		System.out.println("created project directory: "+project.getTitle());
        		new File(projectDir).mkdir();
        	}
        	
        	// export all categories and the views within
        	for (WorkspaceElement child : project.getChildren()) {
        		CategoryElement cat = (CategoryElement) child;
        		String catDir = projectDir+"/"+cat.getTitle();
        		
        		// check if category folder exists else create it
        		if (!new File(catDir).exists()) {
                	System.out.println("created category directory: "+cat.getTitle());
                	new File(catDir).mkdir();
                }
        		
        		// export the views into the category directory
        		for (int i = 0; i < cat.getChildCount(); i++) {
        			ViewElement viewElt = (ViewElement) cat.getChildAt(i);
        			
        			// if view exists ask if it should be overwritten
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

