package angstd.model.workspace.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.SwingUtilities;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.ProjectElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.views.view.io.ViewImporter;

public class LastUsedWorkspaceImporter {
	
	protected static ProjectElement project = null;
	
	public static void initWorkspace(File workspaceFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(workspaceFile)); 
			String line;
			
			File workspaceDir = new File(Configuration.getInstance().getWorkspaceDir());
			String projectDir = null;
			String catDir = null;
			File viewFile = null;
			int viewTypeFlag = -1;

			// parse the file and create the elements
			while((line = in.readLine()) != null) {
				
				// set the actual project
				if(line.contains("<PROJECT>")) {
					line = in.readLine(); // fetch name line manually
					
					projectDir = workspaceDir.getAbsolutePath()+"/"+getValue(line);
		    		
					// check if project directory exists else skip this entry
//					if (!new File(projectDir).exists() || !new File(projectDir).isDirectory()) {
//						System.out.println("Last used workspace and workspace directory are not synchronous");
//		    			// run through the end of the project
//		    			while((line = in.readLine()) != null && !line.toUpperCase().contains("</PROJECT>"))
//		    				;
//		    			continue;
//		    		}
					
					// add project folder to the workspace
					if (!getValue(line).toUpperCase().equals("DEFAULT PROJECT"))
						project = WorkspaceManager.getInstance().addProject(getValue(line), false);
				}
					
				// set the actual category
				if(line.contains("<CATEGORY>")) {
					line = in.readLine(); // fetch name line manually
					catDir = projectDir+"/"+getValue(line);
		    		
					// check if project directory exists else skip this entry
					if (!new File(catDir).exists() || !new File(catDir).isDirectory()) {
		    			System.out.println("Last used workspace and workspace directory are not synchronous");
		    			// run through the end of the project
		    			while((line = in.readLine()) != null && !line.toUpperCase().contains("</CATEGORY>"))
		    				;
		    			continue;
		    		}
					
					// set the viewType flag depending on the category
	    			if (getValue(line).equals("Arrangements"))
	    				viewTypeFlag = 1;
	    			else if(getValue(line).equals("Trees"))
	    				viewTypeFlag = 2;
	    			else if (getValue(line).equals("Sequences"))
	    				viewTypeFlag = 3;
	    			else if (getValue(line).equals("Domain Trees"))
	    				viewTypeFlag = 4;
				}
				
				// set the actual view
				if(line.contains("<VIEW>")) {
					line = in.readLine(); // fetch name line manually
					viewFile = new File(catDir+"/"+getValue(line));
		    		
					// check if project directory exists else skip this entry
					if (!viewFile.exists()) {
		    			System.out.println("Last used workspace and workspace directory are not synchronous");
		    			// run through the end of the project
		    			while((line = in.readLine()) != null && !line.toUpperCase().contains("</VIEW>"))
		    				;
		    			continue;
		    		}
					
					// import view based on the set viewType  flag
	        		switch(viewTypeFlag) {
	        			case 1: ViewImporter.readDomainView(viewFile, project); break;
	        			case 2: ViewImporter.readTreeView(viewFile, project); break;
	        			case 3: ViewImporter.readSequenceView(viewFile, project); break;
	        			case 4: ViewImporter.readDomainTreeView(viewFile, project); break;
	        		}
	    			
				}
			}

			in.close();
			
			// refresh the workspace
	    	SwingUtilities.invokeLater(new Runnable() {						
				public void run() {	
					WorkspaceManager.getInstance().getWorkspaceView().refresh();
				}
	    	});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private static void readSequenceView(File viewFile) {
//		SequenceView view = new SequenceViewImporter().read(viewFile);
//		ViewHandler.getInstance().addView(view, project, false);
//	}
//	
//	private static void readTreeView(File viewFile) {
//		TreeViewI view = new TreeViewImporter().read(viewFile);
//		ViewHandler.getInstance().addView(view, project, false);
//	}
//	
//	private static void readDomainView(File viewFile) {
//		DomainViewI view = new DomainViewImporter().read(viewFile);
//		ViewHandler.getInstance().addView(view, project, false);
//	}
//	
//	private static void readDomainTreeView(File viewFile) {
//		DomainViewI view = new DomainTreeViewImporter().read(viewFile);
//		ViewHandler.getInstance().addView(view, project, false);
//	}
//	
//	private static String getID(String str) {
//		int startPos = str.indexOf("id=\"")+4;
//		int endPos = str.indexOf("\"", startPos);
//		return str.substring(startPos, endPos);
//	}
	
	private static String getValue(String str) {
		int startPos = str.indexOf("value=\"")+7;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
}
