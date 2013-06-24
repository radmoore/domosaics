package domosaics.model.workspace.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.SwingUtilities;

import domosaics.model.configuration.Configuration;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domaintreeview.DomainTreeView;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.treeview.TreeView;
import domosaics.ui.views.view.io.ViewImporter;




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
					
					projectDir = workspaceDir.getAbsolutePath()+File.separator+getValue(line);
		    		
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
					catDir = projectDir+File.separator+getValue(line);
		    		
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
					viewFile = new File(catDir+File.separator+getValue(line));
		    		
					// check if project directory exists else skip this entry
					if (!viewFile.exists()) {
		    			System.out.println("Last used workspace and workspace directory are not synchronous");
		    			// run through the end of the project
		    			while((line = in.readLine()) != null && !line.toUpperCase().contains("</VIEW>"));
		    			continue;
		    		}
					
					// import view based on the set viewType  flag
	        		switch(viewTypeFlag) {
        				// Nic import
	        			case 1:
	        				DomainView domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, getValue(line));
	        				domView.importXML(viewFile);
	        				ViewHandler.getInstance().addView(domView, project, false);
	        				break;
	        			case 2:
	        				TreeView treeView = ViewHandler.getInstance().createView(ViewType.TREE, getValue(line));
	        				treeView.importXML(viewFile);
	        				ViewHandler.getInstance().addView(treeView, project, false);
	        				break;
	        			case 3:
	        				SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, getValue(line));
	        				seqView.importXML(viewFile);
	        				ViewHandler.getInstance().addView(seqView, project, false);
	        				break;
	        			case 4:
	        				DomainTreeView domTreeView = ViewHandler.getInstance().createView(ViewType.DOMAINTREE, getValue(line));
	        				domTreeView.importXML(viewFile);
	        				ViewHandler.getInstance().addView(domTreeView, project, false);
	        				break;
        				//case 1: ViewImporter.readDomainView(viewFile, project); break;
	        			//case 2: ViewImporter.readTreeView(viewFile, project); break;
	        			//case 3: ViewImporter.readSequenceView(viewFile, project); break;
	        			//case 4: ViewImporter.readDomainTreeView(viewFile, project); break;
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
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
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
