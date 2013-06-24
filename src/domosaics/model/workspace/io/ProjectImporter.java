package domosaics.model.workspace.io;

import java.io.File;

import javax.swing.SwingUtilities;

import domosaics.model.workspace.ProjectElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domaintreeview.DomainTreeView;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.treeview.TreeView;
import domosaics.ui.views.view.io.ViewImporter;
import domosaics.ui.wizards.WizardManager;




public class ProjectImporter {
	protected static ProjectElement project = null;
	
//	public static void initWorkspace() {
//		// check for projects within the workspace directory
//    	File workspaceDir = new File(Configuration.getInstance().getWorkspaceDir());
//    	String[] projects = workspaceDir.list();
//    	
//    	// create projects and restore data
//    	for (String p : projects) {
//    		String projectDir = workspaceDir.getAbsolutePath()+"/"+p;
//    		if (!new File(projectDir).isDirectory())
//    			continue;
//
//    		if (!p.equals("Default Project"))
//    			project = WorkspaceManager.getInstance().addProject(p, false);
//    		
//    		
//    		// create categorys and load views
//    		String[] categories = new File(projectDir).list();
//    		for (String c : categories) {
//    			String catDir = projectDir+"/"+c;
//    			if (!new File(catDir).isDirectory())
//        			continue;
//    			
//    			int flag = 0;
//    			if (c.equals("Arrangements"))
//    				flag = 1;
//    			else if(c.equals("Trees"))
//    				flag = 2;
//    			else if (c.equals("Sequences"))
//    				flag = 3;
//    			else if (c.equals("Domain Trees"))
//    				flag = 4;
//    			
//    			String[] views = new File(catDir).list();
//    			for (String v : views) {
//        			File viewFile = new File(catDir+"/"+v);
//        			switch(flag) {
//        				case 1: readDomainView(viewFile); break;
//        				case 2: readTreeView(viewFile); break;
//        				case 3: readSequenceView(viewFile); break;
//        				case 4: readDomainTreeView(viewFile); break;
//        			}
//    			}
//    		}
//    	}
//    	
//    	// refresh the workspace
//    	SwingUtilities.invokeLater(new Runnable() {						
//			public void run() {	
//				WorkspaceManager.getInstance().getWorkspaceView().refresh();
//			}
//    	});
//	}
	
	public static void read(File file) {
    	String projectDirPath = file.getAbsolutePath();
    	File projectDir = new File(projectDirPath);
    	boolean validProject = false;
    	String projectName = file.getName();
    	
		if (! projectDir.isDirectory())
			return;

		// ensure that no 'Default Project' is being imported
//		if (projectName.equals("Default Project")) {
//			MessageUtil.showInformation("The name >Default Project< is disallowed. Please choose a different name.");
//			
//			projectName = WizardManager.getInstance().selectRenameWizard("Default Project", "project", null);
//			if (projectName == null)
//				return;
//		}
		
		//check if this is a valid project
		String[] projectFiles = projectDir.list();
		for (String elem : projectFiles) {
			if (elem.equals(ProjectExporter.PROJECTFILE)) {
				validProject = true;
				break;
			}
		}

		if (!validProject)
			if (!MessageUtil.showDialog(DoMosaicsUI.getInstance(),file.getName() + " does not appear to be a valid project. Do you want to import anyways?"))
				return;
			
		
		project = WizardManager.getInstance().showCreateProjectWizard(projectName);
		//project = WorkspaceManager.getInstance().addProject(projectName, false);
		
		// create categorys and load views
		String[] categories = new File(projectDirPath).list();
		for (String c : categories) {
			String catDir = projectDirPath+File.separator+c;
			if (!new File(catDir).isDirectory())
    			continue;
			
			int flag = 0;
			if (c.equals("Arrangements"))
				flag = 1;
			else if(c.equals("Trees"))
				flag = 2;
			else if (c.equals("Sequences"))
				flag = 3;
			else if (c.equals("Domain Trees"))
				flag = 4;
			
			String[] views = new File(catDir).list();
			for (String v : views) {
    			File viewFile = new File(catDir+File.separator+v);
    			switch(flag) {
				// Nic import
    			case 1:
    				DomainView domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, v);
    				domView.importXML(viewFile);
    				ViewHandler.getInstance().addView(domView, project, false);
    				break;
    			case 2:
    				TreeView treeView = ViewHandler.getInstance().createView(ViewType.TREE, v);
    				treeView.importXML(viewFile);
    				ViewHandler.getInstance().addView(treeView, project, false);
    				break;
    			case 3:
    				SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, v);
    				seqView.importXML(viewFile);
    				ViewHandler.getInstance().addView(seqView, project, false);
    				break;
    			case 4:
    				DomainTreeView domTreeView = ViewHandler.getInstance().createView(ViewType.DOMAINTREE, v);
    				domTreeView.importXML(viewFile);
    				ViewHandler.getInstance().addView(domTreeView, project, false);
    				break;
    			//	case 1: ViewImporter.readDomainView(viewFile, project); break;
    			//	case 2: ViewImporter.readTreeView(viewFile, project); break;
    			//	case 3: ViewImporter.readSequenceView(viewFile, project); break;
    			//	case 4: ViewImporter.readDomainTreeView(viewFile, project); break;
    			}
			}
		}
		
    	// refresh the workspace
    	SwingUtilities.invokeLater(new Runnable() {						
			public void run() {	
				WorkspaceManager.getInstance().getWorkspaceView().refresh();
			}
    	});
	}
	
	// returns true, if the import action should be canceled
	@SuppressWarnings("unused")
	private static boolean createProject(String projectName) {
		project = WorkspaceManager.getInstance().getProject(projectName);
		if (project != null) { // project already exists
			Object[] options = {"Overwrite", "Rename", "Cancel"};
			int choice = MessageUtil.show3ChoiceDialog(DoMosaicsUI.getInstance(),"Project already exists. Overwrite or rename?", options);
			
			switch (choice) {
				case 0: 
					WorkspaceManager.getInstance().removeFromDomosaics(project); 
					project = WorkspaceManager.getInstance().addProject(project.getTitle(), true);
					break;  	// overwrite;
				case 1: WizardManager.getInstance().showCreateProjectWizard(projectName);	break; 	// rename
				case 2: return true; 	// cancel
			}
			project = WorkspaceManager.getInstance().getSelectionManager().getSelectedProject();
		} else {
			project = WorkspaceManager.getInstance().addProject(projectName, true);
		}
		return false;
	}

//	public static void read()  {
//    	if (projects == null)
//    		return;
//    	
//    	String projectDir = workspaceDir+"/"+project.getTitle();
//    	if (!new File(projectDir).exists()) {
//    		System.out.println("created project directory: "+project.getTitle());
//    		new File(projectDir).mkdir();
//    	}
//    	
//    	// export all categories and the views within
//    	for (WorkspaceElement child : project.getChildren()) {
//    		CategoryElement cat = (CategoryElement) child;
//    		String catDir = projectDir+"/"+cat.getTitle();
//    		
//    		// check if category folder exists else create it
//    		if (!new File(catDir).exists()) {
//            	System.out.println("created category directory: "+cat.getTitle());
//            	new File(catDir).mkdir();
//            }
//    		
//    		// export the views into the category directory
//    		for (int i = 0; i < cat.getChildCount(); i++) {
//    			ViewElement viewElt = (ViewElement) cat.getChildAt(i);
//    			
//    			// if view exists ask if it should be overwritten
//    			File viewFile = new File(catDir+"/"+viewElt.getTitle());
//        		if (viewFile.exists()) 
//                	if (!MessageUtil.showDialog("View "+viewElt.getTitle()+" already exists. Overwrite it?"))
//                		continue;
//                
//                // export the view
//                ViewHandler.getInstance().getView(viewElt.getViewInfo()).export(viewFile);
//    		}
//    	}
//		
//		
//		
//		
//		
//		
//		
//		// read the project main file and store to be imported view files in lists
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(file)); 
//			String line;
//			boolean cancelFlag = false; 		
//			boolean readViewFlag = false;
//			StringBuffer viewString = new StringBuffer();
//			
//			while((line = in.readLine()) != null) {
//				if (cancelFlag)
//					break;
//				
//				if (line.isEmpty())					// ignore empty lines
//					continue;
//				
//				if (line.startsWith("#"))			// ignore comments
//					continue;
//				
//				// read project name and create project
//				if(line.contains("parameter") && idEquals(line, "PROJECTNAME")) {
//					cancelFlag = createProject(getValue(line));
//					continue;
//				}
//				
//				// start reading view as xml
//				if (line.contains("<SEQUENCEVIEW>") ||
//					line.contains("<TREEVIEW>") ||	
//					line.contains("<DOMAINVIEW>") ||
//					line.contains("<DOMAINTREEVIEW>")) {
//						readViewFlag = true;
//						viewString = new StringBuffer();
//						continue;
//				}
//	
//				// read the view from the xml string
//				if (line.contains("</SEQUENCEVIEW>")) {
//					readSequenceView(viewString.toString());
//					readViewFlag = false;
//					continue;
//				}
//				
//				if (line.contains("</TREEVIEW>")) {
//					readTreeView(viewString.toString());
//					readViewFlag = false;
//					continue;
//				}
//				
//				if (line.contains("</DOMAINVIEW>")) {
//					readDomainView(viewString.toString());
//					readViewFlag = false;
//					continue;
//				}
//				
//				if (line.contains("</DOMAINTREEVIEW>")) {
//					readDomainTreeView(viewString.toString());
//					readViewFlag = false;
//					continue;
//				}
//				
//				if(readViewFlag) {
//					viewString.append(line+"\r\n");
//					continue;
//				}
//				
//			}
//		} catch (Exception e) {
//			System.out.println("Error occured during project import - reading project file");
//			e.printStackTrace();
//		}
//	}
	

	
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(viewFile));
//			String line;
//			
//			String seqViewName = null;
//			SequenceView seqView = null;
//			
//			boolean readDataFlag = false;
//			boolean readAttributesFlag = false;
//			StringBuffer data = new StringBuffer();
//			StringBuffer attributes = new StringBuffer();
//			
//			while((line = in.readLine()) != null) {
//				if(line.contains("parameter") && idEquals(line, "VIEWNAME")) {
//					seqViewName = getValue(line); 
//					continue;
//				}
//				
//				if(line.contains("<DATA>")) {
//					readDataFlag = true;
//					continue;
//				}
//				
//				if(line.contains("<ATTRIBUTES>")) {
//					readAttributesFlag = true;
//					continue;
//				}
//				
//				if(line.contains("</DATA>")) {
//					readDataFlag = false;
//					SequenceI[] seqs = (SequenceI[]) new FastaReader().getDataFromString(data.toString());
//					if (seqs == null)
//						return;
//					seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, seqViewName);
//					seqView.setSeqs(seqs);
//					ViewHandler.getInstance().addView(seqView, project, false);
//					continue;
//				}
//				
//				if(line.contains("</ATTRIBUTES>")) {
//					readAttributesFlag = false;
//					SequenceViewImporter.read(attributes.toString(), seqView);
//					continue;
//				}
//				
//				if (readDataFlag) 
//					data.append(line+"\r\n");
//	
//				if (readAttributesFlag) 
//					attributes.append(line+"\r\n");
//			}
//			in.close();
//		} catch(Exception e) {
//			System.out.println("Problem during SequenceView import.");
//			e.printStackTrace();
//		}
//	}
	
//	private static void readDomainView(File viewFile) {
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(viewFile));
//			String line;
//			
//			String domViewName = null;
//			SequenceI[] seqs = null;	// associated sequences
//			DomainViewI domView = null;
//			
//			boolean readSeqDataFlag = false;
//			boolean readDataFlag = false;
//			boolean readAttributesFlag = false;
//			
//			StringBuffer seqData = new StringBuffer();
//			StringBuffer data = new StringBuffer();
//			StringBuffer attributes = new StringBuffer();
//			
//			while((line = in.readLine()) != null) {
//				if(line.contains("parameter") && idEquals(line, "VIEWNAME")) {
//					domViewName = getValue(line);
//					continue;
//				}
//				
//				if(line.contains("<SEQUENCEDATA>")) {
//					readSeqDataFlag = true;
//					continue;
//				}
//				
//				if(line.contains("<DATA>")) {
//					readDataFlag = true;
//					continue;
//				}
//				
//				if(line.contains("<ATTRIBUTES>")) {
//					readAttributesFlag = true;
//					continue;
//				}
//				
//				if(line.contains("</SEQUENCEDATA>")) {
//					readSeqDataFlag = false;
//					seqs = (SequenceI[]) new FastaReader().getDataFromString(seqData.toString());
//					continue;
//				}
//				
//				if(line.contains("</DATA>")) {
//					readDataFlag = false;
//					DomainArrangement[] proteins = (DomainArrangement[]) new XdomReader().getDataFromString(data.toString());
//					if (proteins == null)
//						return;
//
//					domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, domViewName);
//					domView.setDaSet(proteins);
//					
//					if (seqs != null) 
//						domView.loadSequencesIntoDas(seqs, proteins);
//					
//					ViewHandler.getInstance().addView(domView, project, false);
//					continue;
//				}
//				
//				if(line.contains("</ATTRIBUTES>")) {
//					readAttributesFlag = false;
//					DomainViewImporter.read(attributes.toString(), domView);
//					continue;
//				}
//				
//				if (readSeqDataFlag) 
//					seqData.append(line+"\r\n");
//				
//				if (readDataFlag) 
//					data.append(line+"\r\n");
//	
//				if (readAttributesFlag) 
//					attributes.append(line+"\r\n");
//			}
//			in.close();
//		} catch(Exception e) {
//			System.out.println("Problem during DomainView import.");
//			e.printStackTrace();
//		}
//	}
	
//	private static void readTreeView(File viewFile) {
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(viewFile));
//			String line;
//			
//			String treeViewName = null;
//			TreeViewI treeView = null;
//			
//			boolean readDataFlag = false;
//			boolean readAttributesFlag = false;
//			StringBuffer data = new StringBuffer();
//			StringBuffer attributes = new StringBuffer();
//			
//			while((line = in.readLine()) != null) {
//				if(line.contains("parameter") && idEquals(line, "VIEWNAME")) {
//					treeViewName = getValue(line); 
//					continue;
//				}
//				
//				if(line.contains("<DATA>")) {
//					readDataFlag = true;
//					continue;
//				}
//				
//				if(line.contains("<ATTRIBUTES>")) {
//					readAttributesFlag = true;
//					continue;
//				}
//				
//				if(line.contains("</DATA>")) {
//					readDataFlag = false;
//					TreeI tree = new NewickTreeReader().getTreeFromString(data.toString());
//					if (tree == null)
//						return;
//
//					treeView = ViewHandler.getInstance().createView(ViewType.TREE, treeViewName);
//					treeView.setTree(tree);
//					ViewHandler.getInstance().addView(treeView, project, false);
//				}
//				
//				if(line.contains("</ATTRIBUTES>")) {
//					readAttributesFlag = false;
//					TreeViewImporter.read(attributes.toString(), treeView);
//					
//					
//					// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//					
//					/* FIXME when layout is on use distances and the font 
//					 * was changed a new node with id 0 occured... WHY?!
//					 * Before TreeAttributeReader everything is fine.
//					 * It happens within setFont(node, Font) after
//					 * if(idEquals(line, "FONT"))	
//					 * the following lines are testcode to check 
//					 * whether or not the id zero node is present
//					 */
////					Iterator<NodeComponent> ncIter = treeView.getTreeComponentManager().getComponentsIterator();
////					while(ncIter.hasNext()) {
////						NodeComponent nc = ncIter.next();
////						System.out.println(nc.getNode().getID()+"-"+nc.getNode().getLabel());
////					}
//					
//					
//					// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//					continue;
//				}
//				
//				if (readDataFlag) 
//					data.append(line+"\r\n");
//	
//				if (readAttributesFlag) 
//					attributes.append(line+"\r\n");
//			}
//			in.close();
//		} catch(Exception e) {
//			System.out.println("Problem during TreeView import.");
//			e.printStackTrace();
//		}
//	}
	
//	private static void readDomainTreeView(File viewFile) {
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(viewFile));
//			String line;
//			
//			String viewName = null;
//			String treeViewName = null;
//			String domViewName = null;
//			
//			boolean readAttributesFlag = false;
//			StringBuffer attributes = new StringBuffer();
//			
//			while((line = in.readLine()) != null) {
//				if(line.contains("parameter") && idEquals(line, "VIEWNAME")) {
//					viewName = getValue(line); 
//					continue;
//				}
//				
//				if(line.contains("parameter") && idEquals(line, "TREEVIEWNAME")) {
//					treeViewName = getValue(line); 
//					continue;
//				}
//				
//				if(line.contains("parameter") && idEquals(line, "DOMAINVIEWNAME")) {
//					domViewName = getValue(line); 
//					continue;
//				}
//				
//				if(line.contains("<ATTRIBUTES>")) {
//					readAttributesFlag = true;
//					continue;
//				}
//				
//				
//				if(line.contains("</ATTRIBUTES>")) {
//					readAttributesFlag = false;
//					
//					TreeViewI treeView = WorkspaceManager.getInstance().getWorkspace().getTreeViewByName(project, treeViewName);
//					DomainViewI domView = WorkspaceManager.getInstance().getWorkspace().getDomainViewByName(project, domViewName);
//					
//					DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, viewName);
//					domTreeView.setBackendViews(treeView, domView);
//					ViewHandler.getInstance().addView(domTreeView, project);
//					
//					DomainTreeAttributeReader.read(attributes.toString(), domTreeView);
//					continue;
//				}
//	
//				if (readAttributesFlag) 
//					attributes.append(line+"\r\n");
//			}
//			in.close();
//		} catch(Exception e) {
//			System.out.println("Problem during DomainTreeView import.");
//			e.printStackTrace();
//		}
//	}

	
//	protected static String getID(String str) {
//		int startPos = str.indexOf("id=\"")+4;
//		int endPos = str.indexOf("\"", startPos);
//		return str.substring(startPos, endPos);
//	}
//	
//	protected static String getValue(String str) {
//		int startPos = str.indexOf("value=\"")+7;
//		int endPos = str.indexOf("\"", startPos);
//		return str.substring(startPos, endPos);
//	}
//	
//	protected static boolean idEquals(String line, String id) {
//		return getID(line).toUpperCase().equals(id);
//	}
	

}
