package angstd.model.workspace.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.CategoryElement;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;

public class LastUsedWorkspaceWriter {

	public static void write() {
        try {
        	// check for the project within the workspace directory, create it if necessary
        	String workspaceDir = Configuration.getInstance().getWorkspaceDir();
        	
        	// get the workspace file and parse existing projects
        	File workspaceFile = new File (workspaceDir+"/lastusedworkspace.file");
        	BufferedWriter out = new BufferedWriter(new FileWriter(workspaceFile));
        	writeTag(out, 0, "WORKSPACE", true);
    		
        	// Iterate over workspace and create workspace file
        	for(ProjectElement project : WorkspaceManager.getInstance().getProjects()) {
        		
        		// check if project has at least one category
        		if (project.getChildCount() > 0)
        			writeProject(out, project);
	
        		// check if project has at last one saved view
//        		boolean export = false;
//        		for (WorkspaceElement cat : project.getChildren())
//        			for (WorkspaceElement view : cat.getChildren())
//        				if (!ViewHandler.getInstance().getView(((ViewElement) view).getViewInfo()).isChanged()) {
//        					export = true;
//        					break;
//        				}
//        		if (export)
//        			writeProject(out, project);
        	}
        	
        	writeTag(out, 0, "WORKSPACE", false);
        	
        		
        	out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private static void writeProject(BufferedWriter out, ProjectElement project) throws IOException {
		
		writeTag(out, 1, "PROJECT", true);
		writeParam(out, 2, "name", project.getTitle());
		
		// export all categories and the views within
    	for (WorkspaceElement child : project.getChildren()) {
    		CategoryElement cat = (CategoryElement) child;
    		
//    		// check if project has at last one saved view
//    		boolean export = false;
//    		for (WorkspaceElement view : cat.getChildren())
//    			if (!ViewHandler.getInstance().getView(((ViewElement) view).getViewInfo()).isChanged()) {
//    				export = true;
//    				break;
//    			}
//    		if (!export)
//    			continue;
    			
    		writeTag(out, 2, "CATEGORY", true);
    		writeParam(out, 3, "name", cat.getTitle());
    		
    		// views 
    		for (int i = 0; i < cat.getChildCount(); i++) {
    			ViewElement viewElt = (ViewElement) cat.getChildAt(i);
    			
//    			// check if view is saved
//    			if (ViewHandler.getInstance().getView(viewElt.getViewInfo()).isChanged()) 
//    				continue;
  
    			writeTag(out, 3, "VIEW", true);
        		writeParam(out, 4, "name", viewElt.getTitle());
        		writeTag(out, 3, "VIEW", false);
    		}
    		writeTag(out, 2, "CATEGORY", false);
    	}
    	writeTag(out, 1, "PROJECT", false);
	}
	
	/**
	 * Writes an xml tag with a specified id to the specified output 
	 * stream. (e.g. <DATA>).
	 * 
	 * @param out
	 * 		the output stream used to print
	 * @param tabs
	 * 		the number of tabs used to shift the starting point of the line
	 * @param id
	 * 		the tags id
	 * @param open
	 * 		flag whether or not its a tag opening or a tag closing
	 * @throws IOException
	 * 		the io exception which may occur
	 */
	protected static void writeTag(BufferedWriter out, int tabs, String id, boolean open) throws IOException {
		for (int i = 0; i < tabs; i++)
			out.write("\t");
		if (open)
			out.write("<"+id+">\r\n"); 		// write tag opening
		else {
			out.write("</"+id+">\r\n\r\n"); // write closing tag 	
			out.flush(); 
		}
	}
	
	/**
	 * Writes a parameter line to the specified output stream. <br>
	 * (e.g. <parameter id="VIEWNAME" value="SequenceView 1"/> .
	 * 
	 * @param out
	 * 		the output stream used to print
	 * @param tabs
	 * 		the number of tabs used to shift the starting point of the line
	 * @param id
	 * 		the parameters name
	 * @param param
	 * 		the parameters value
	 * @throws IOException
	 * 		the io exception which may occur
	 */
	protected static void writeParam(BufferedWriter out, int tabs, String id, String param) throws IOException {
		for (int i = 0; i < tabs; i++)
			out.write("\t");							// tab it in
		out.write("<parameter id=\""+id+"\" ");		// write parameter id
		out.write("value=\""+param.toString()+"\"");// write parameter value
		out.write("/>\r\n");						// write line ending
	}
}
