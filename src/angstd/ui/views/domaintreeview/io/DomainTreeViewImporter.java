package angstd.ui.views.domaintreeview.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.io.XdomReader;
import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.model.tree.Tree;
import angstd.model.tree.TreeEdge;
import angstd.model.tree.TreeEdgeI;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNode;
import angstd.model.tree.TreeNodeI;
import angstd.ui.ViewHandler;
import angstd.ui.views.ViewType;
import angstd.ui.views.domaintreeview.DomainTreeView;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.actions.CollapseSameArrangementsAtNodeAction;
import angstd.ui.views.domaintreeview.layout.CSAModeDomainTreeLayout;
import angstd.ui.views.domaintreeview.layout.DefaultDomainTreeLayout;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.layout.MSALayout;
import angstd.ui.views.domainview.layout.ProportionalLayout;
import angstd.ui.views.domainview.layout.UnproportionalLayout;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import angstd.ui.views.domainview.renderer.arrangement.MsaArrangementRenderer;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.io.TreeViewImporter;
import angstd.ui.views.view.io.ViewImporter;
import angstd.ui.views.view.layout.ViewLayout;



public class DomainTreeViewImporter extends ViewImporter<DomainTreeViewI>  {
	
	private Map<Integer, TreeNodeI> id2node = new HashMap<Integer, TreeNodeI>();
	private Map<TreeNodeI, Boolean> node2collapsed = new HashMap<TreeNodeI, Boolean>();
	private Map<TreeNodeI, Color> node2color = new HashMap<TreeNodeI, Color>();
	private Map<TreeEdgeI, Color> edge2edgecolor = new HashMap<TreeEdgeI, Color>();
	private Map<TreeEdgeI, BasicStroke> edge2edgestroke = new HashMap<TreeEdgeI, BasicStroke>();
	private Map<TreeNodeI, Font> node2font = new HashMap<TreeNodeI, Font>();
	private Map<TreeNodeI, Boolean> node2CsaCollapsed = new HashMap<TreeNodeI, Boolean>();
	
	// settings for the actual domain family
	private static DomainFamily fam = null;
	private static ViewLayout domLayout = null;
	

	protected void setLayoutSettings(DomainTreeViewI view) {
		if (domLayout == null)
			return;
		
		// keep the domain layout manager up to date if the layout was set while importing a view
		if (domLayout instanceof ProportionalLayout && !view.getDomainLayoutManager().isProportionalView()) {
			view.setViewLayout(new ProportionalLayout());
			view.getDomainLayoutManager().setToProportionalView();
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.registerMouseListeners();
		}
		if (domLayout instanceof UnproportionalLayout && !view.getDomainLayoutManager().isUnproportionalView()) {
			view.setViewLayout(new UnproportionalLayout());
			view.getDomainLayoutManager().setToUnproportionalView();
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.registerMouseListeners();
		}
			
		if (domLayout instanceof MSALayout && !view.getDomainLayoutManager().isMsaView()) {
			view.setViewLayout(new MSALayout());
			view.getDomainViewRenderer().setArrangementRenderer(new MsaArrangementRenderer());		
			view.getDomainLayoutManager().setToMsaView();
			view.registerMouseListeners();
		}
		
//		if (view.getCSAInSubtreeManager().isActive()) {
//			DefaultDomainTreeLayout newLayout = new CSAModeDomainTreeLayout();
//			newLayout.setDomainLayout(view.getDomainLayout());
//			view.setViewLayout(newLayout);
//			view.registerMouseListeners();
//		}
		
	}
	
	public DomainTreeViewI readData(String data) {
		try {
			TreeViewI treeView = null;
			DomainViewI domView = null;
			
			BufferedReader in = new BufferedReader(new StringReader(data)); 
			String line;
			
			TreeI tree = new Tree();
			TreeNodeI actNode = null;
			int id = -1;
			SequenceI[] seqs = null;		// associated sequences
			StringBuffer seqData = new StringBuffer();
			StringBuffer domainData = new StringBuffer();
			
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				if (line.startsWith("#"))			// ignore comments
					continue;
				
				// READ THE SEQUENCES IF PRESENT
				if (line.toUpperCase().contains("<SEQUENCEDATA>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</SEQUENCEDATA>")) 
						seqData.append(line+"\r\n");
					seqs = (SequenceI[]) new FastaReader().getDataFromString(seqData.toString());
				}
			
				// READ THE TREE
				if (line.toUpperCase().contains("<TREEDATA>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</TREEDATA>")) {
						
						// parse children
						if (line.toUpperCase().contains("<CHILDREN>")) {
							while( (line = in.readLine()) != null && !line.toUpperCase().contains("</CHILDREN>")) {
								int childID = str2int(getValue(line));
								float weight = str2float(getValue2(line));
								TreeNodeI child = new TreeNode(childID);
								id2node.put(childID, child);
								TreeEdgeI edge = new TreeEdge(actNode, child, weight);
								tree.addEdge(edge);
								tree.addNode(child);
							}
						}
						
						// if it is not a parameter line, skip it
						if (!line.contains("parameter"))	
							continue;
						
						// store node attributes
						if(idEquals(line, "ID")) {
							id = str2int(getValue(line));
							if (id2node.get(id) == null) {
								actNode = new TreeNode(id);
								tree.addNode(actNode);
								id2node.put(id, actNode);
							} else
								actNode = id2node.get(id);
							if (tree.getRoot() == null)
								tree.setRoot(actNode);
						} else if(idEquals(line, "NAME") && !getValue(line).equals("null")) 
							actNode.setLabel(getValue(line));
						else if(idEquals(line, "COLLAPSED"))
							node2collapsed.put(actNode, str2boolean(getValue(line)));
						else if(idEquals(line, "CSACOLLAPSED"))
							node2CsaCollapsed.put(actNode, str2boolean(getValue(line)));
						else if(idEquals(line, "NODECOLOR"))
							node2color.put(actNode, str2color(getValue(line)));
						else if(idEquals(line, "EDGECOLOR")) 
							edge2edgecolor.put(actNode.getEdgeToParent(), str2color(getValue(line)));
						else if(idEquals(line, "EDGESTROKE"))
							edge2edgestroke.put(actNode.getEdgeToParent(), str2stroke(getValue(line)));
						else if(idEquals(line, "FONT"))	
							node2font.put(actNode, str2font(getValue(line)));	
					}
					if (tree == null)
						return null;
					
					// create the tree view based on the parsed tree
					treeView = ViewHandler.getInstance().createView(ViewType.TREE, viewName+"treeDummy");
					treeView.setTree(tree);
					
					// init component manager
					Iterator<TreeNodeI> iter = tree.getNodeIterator();
					while(iter.hasNext()) 
						treeView.getNodesComponent(iter.next());
					
					// now set the node attributes
					for (TreeNodeI node : node2color.keySet())
						treeView.getTreeColorManager().setNodeColor(treeView.getNodesComponent(node), node2color.get(node));
					
					for (TreeEdgeI edge : edge2edgecolor.keySet())
						treeView.getTreeColorManager().setEdgeColor(edge, edge2edgecolor.get(edge));
					
					for (TreeEdgeI edge : edge2edgestroke.keySet())
						treeView.getTreeStrokeManager().setEdgeStroke(edge2edgestroke.get(edge), edge);
					
					for (TreeNodeI node : node2font.keySet()) {
						if (!treeView.getTreeFontManager().getFont().equals(node2font.get(node))) 
							treeView.getTreeFontManager().setFont(treeView.getNodesComponent(node), node2font.get(node));
					}

					for (TreeNodeI node : node2collapsed.keySet())
						treeView.getNodesComponent(node).setCollapsed(node2collapsed.get(node));
				
				}
				
				// READ DOMAINS
				if(line.contains("<DOMAINDATA>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</DOMAINDATA>")) 
						domainData.append(line+"\r\n");
					
					DomainArrangement[] proteins = (DomainArrangement[]) new XdomReader().getDataFromString(domainData.toString());
					if (proteins == null)
						return null;
					
					domView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName+"domDummy");
					domView.setDaSet(proteins);
					
					if (seqs != null) 
						domView.loadSequencesIntoDas(seqs, proteins);
				}
			}
			
			// domainview and treeview should be created now, time to set up the domain tree view
			if (treeView == null || domView == null)
				return null;
			
			DomainTreeViewI domTreeView =  ViewHandler.getInstance().createView(ViewType.DOMAINTREE, viewName);
			domTreeView.setBackendViews(treeView, domView);
			
			// run through the tree bottom up and collapse the nodes.
			collapseHelper(treeView.getTree().getRoot(), domTreeView);

			return domTreeView;
		} catch (Exception e) {
			Configuration.getLogger().debug(e.toString());	
			return null;
		}
	}
	
	
	public void readAttributes(String attributes, DomainTreeViewI view)  {
		try {
			BufferedReader in = new BufferedReader(new StringReader(attributes)); 
			String line;	
			
			ArrangementManager manager = new ArrangementManager();
			manager.add(view.getDaSet());
			
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				
				if (line.startsWith("#"))			// ignore comments
					continue;
			
				if (line.toUpperCase().contains("<TREEDEFAULTSETTINGS>")) 
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</TREEDEFAULTSETTINGS>")) 
						TreeViewImporter.readDefaultSetting(line, view);
				
				
				if (line.toUpperCase().contains("<TREELAYOUTSETTINGS>")) 
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</TREELAYOUTSETTINGS>")) 
						TreeViewImporter.readLayoutSetting(line, view);
				
				if (line.toUpperCase().contains("<DOMAINLAYOUTSETTINGS>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</DOMAINLAYOUTSETTINGS>")) 
						readDomainLayoutSetting(line, view); 
				}
				
				if (line.toUpperCase().contains("<DOMAINSETTINGS>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</DOMAINSETTINGS>")) {
						
						// if it is not a parameterline, skip it
						if (!line.contains("parameter"))	
							continue;

						readFamilySetting(line, view, manager);
					}	
				}
			}
			
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		
	}
	
	private void collapseHelper(TreeNodeI node, DomainTreeViewI view) {
		for (TreeNodeI child : node.getChildren()) 
			collapseHelper(child, view);
		
		if (node2CsaCollapsed.get(node) != null && node2CsaCollapsed.get(node)) {
			CollapseSameArrangementsAtNodeAction.collapse(view, view.getNodesComponent(node));
			return;
		}
		
		if (node2collapsed.get(node) != null)
			view.getTreeComponentManager().setNodeCollapsed(view.getNodesComponent(node), node2collapsed.get(node), view);
	}
	
	private static void readFamilySetting(String line, DomainViewI view, ArrangementManager manager) {
		if(idEquals(line, "ACC"))
			fam = manager.getFamily(getValue(line));
		else if(idEquals(line, "COLOR"))
			view.getDomainColorManager().setDomainColor(fam, str2color(getValue(line)));
		else if(idEquals(line, "SHAPE")) 
			view.getDomainShapeManager().setDomainShape(fam, str2int(getValue(line)));
	}
	
	public static void readDomainLayoutSetting(String line, DomainViewI view) {
		if(idEquals(line, "VIEWLAYOUT")) {
			if (getValue(line).equals("PROPORTIONAL"))
				domLayout = new ProportionalLayout();
			else if(getValue(line).equals("UNPROPORTIONAL")) 
				domLayout = new UnproportionalLayout();
			else if(getValue(line).equals("MSA"))
				domLayout = new MSALayout();
			else 
				domLayout = new ProportionalLayout();
				
		}
		if(idEquals(line, "FITTOSCREEN"))
			view.getDomainLayoutManager().setFitDomainsToScreen(str2boolean(getValue(line)));
		else if(idEquals(line, "SHOWSHAPES"))
			view.getDomainLayoutManager().setShowShapes(str2boolean(getValue(line)));
//		else if(idEquals(line, "SHOWLINEAL"))
//			view.getDomainLayoutManager().setShowLineal(str2boolean(getValue(line)));
	}

}
