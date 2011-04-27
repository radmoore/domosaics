package angstd.ui.views.treeview.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import angstd.model.configuration.Configuration;
import angstd.model.tree.Tree;
import angstd.model.tree.TreeEdge;
import angstd.model.tree.TreeEdgeI;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNode;
import angstd.model.tree.TreeNodeI;
import angstd.ui.ViewHandler;
import angstd.ui.views.ViewType;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.view.io.ViewImporter;

public class TreeViewImporter extends ViewImporter<TreeViewI> {
	private Map<Integer, TreeNodeI> id2node = new HashMap<Integer, TreeNodeI>();
	private Map<TreeNodeI, Boolean> node2collapsed = new HashMap<TreeNodeI, Boolean>();
	private Map<TreeNodeI, Color> node2color = new HashMap<TreeNodeI, Color>();
	private Map<TreeEdgeI, Color> edge2edgecolor = new HashMap<TreeEdgeI, Color>();
	private Map<TreeEdgeI, BasicStroke> edge2edgestroke = new HashMap<TreeEdgeI, BasicStroke>();
	private Map<TreeNodeI, Font> node2font = new HashMap<TreeNodeI, Font>();
	
	public TreeViewI readData(String data) {
		try {
			TreeViewI res = null;
	
			// parse the xml formatted tree
			BufferedReader in = new BufferedReader(new StringReader(data)); 
			String line;
			TreeI tree = new Tree();
			TreeNodeI actNode = null;
			int id = -1;
			
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				if (line.startsWith("#"))			// ignore comments
					continue;
	
				// parse children
				if (line.toUpperCase().contains("<CHILDREN>")) {
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</CHILDREN>")) {
						int childID = str2int(getValue(line));
						float weight = str2float(getValue2(line));
						TreeNodeI child = new TreeNode(childID);
						tree.addNode(child);
						id2node.put(childID, child);
						TreeEdgeI edge = new TreeEdge(actNode, child, weight);
						tree.addEdge(edge);
					}
				}
				
				// if it is not a parameterline, skip it
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

			// create the view based on the parsed tree
			res = ViewHandler.getInstance().createView(ViewType.TREE, viewName);
			res.setTree(tree);
		
			// init component manager
			Iterator<TreeNodeI> iter = tree.getNodeIterator();
			while(iter.hasNext()) 
				res.getNodesComponent(iter.next());
			
			// now set the node attributes
			for (TreeNodeI node : node2color.keySet())
				res.getTreeColorManager().setNodeColor(res.getNodesComponent(node), node2color.get(node));
			
			for (TreeEdgeI edge : edge2edgecolor.keySet())
				res.getTreeColorManager().setEdgeColor(edge, edge2edgecolor.get(edge));
			
			for (TreeEdgeI edge : edge2edgestroke.keySet())
				res.getTreeStrokeManager().setEdgeStroke(edge2edgestroke.get(edge), edge);
			
			for (TreeNodeI node : node2font.keySet()) {
				if (!res.getTreeFontManager().getFont().equals(node2font.get(node))) 
					res.getTreeFontManager().setFont(res.getNodesComponent(node), node2font.get(node));
			}
			
			// run through the tree bottom up and collapse the nodes.
			collapseHelper(res.getTree().getRoot(), res);
				
			for (TreeNodeI node : node2collapsed.keySet())
				res.getNodesComponent(node).setCollapsed(node2collapsed.get(node));
		
			return res;
		
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
			return null;
		}
	}
	
	private void collapseHelper(TreeNodeI node, TreeViewI view) {
		for (TreeNodeI child : node.getChildren()) 
			collapseHelper(child, view);
		
		if (node2collapsed.get(node) != null)
			view.getTreeComponentManager().setNodeCollapsed(view.getNodesComponent(node), node2collapsed.get(node), null);
	}
	
	public void readAttributes(String attributes, TreeViewI view)  {
		try {
			BufferedReader in = new BufferedReader(new StringReader(attributes)); 
			String line;	
			
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;
				
				if (line.startsWith("#"))			// ignore comments
					continue;
				
				// set the importer flag depending on actual entry
				if (line.toUpperCase().contains("<DEFAULTSETTINGS>")) 
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</DEFAULTSETTINGS>")) 
						readDefaultSetting(line, view);
				
				
				if (line.toUpperCase().contains("<LAYOUTSETTINGS>")) 
					while( (line = in.readLine()) != null && !line.toUpperCase().contains("</LAYOUTSETTINGS>")) 
						readLayoutSetting(line, view);
			}
			
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
	}
	
	public static void readDefaultSetting(String line, TreeViewI view) {
		if(idEquals(line, "DEFAULTEDGECOLOR"))
			return; 	// can't be changed so far
		else if(idEquals(line, "DEFAULTNODECOLOR"))
			return; 	// can't be changed so far
		else if(idEquals(line, "DEFAULTEDGELABELCOLOR")) 
			return; 	// can't be changed so far
		else if(idEquals(line, "DEFAULTSELECTIONCOLOR")) 
			view.getTreeColorManager().setSelectionColor(str2color(getValue(line)));
		else if(idEquals(line, "DEFAULTEDGESTROKE")) 
			view.getTreeStrokeManager().setDefaultEdgeStroke(str2stroke(getValue(line)));
		else if(idEquals(line, "DEFAULTFONT"))
			view.getTreeFontManager().setFont(str2font(getValue(line)));
		else if(idEquals(line, "MAXIMUMFONTSIZE")) 
			view.getTreeFontManager().setMaximumFontSize(str2float(getValue(line)));
		else if(idEquals(line, "MINIMUMFONTSIZE")) 
			view.getTreeFontManager().setMinimumFontSize(str2float(getValue(line)));
	}
	
	public static void readLayoutSetting(String line, TreeViewI view) {
		if(idEquals(line, "SHOWEDGELABELS"))
			view.getTreeLayoutManager().setDrawEdgeLabels(str2boolean(getValue(line)));
		else if(idEquals(line, "EXPANDLEAVES"))
			view.getTreeLayoutManager().setExpandLeaves(str2boolean(getValue(line)));
		else if(idEquals(line, "SHOWBOOTSTRAP"))
			view.getTreeLayoutManager().setShowBootstrap(str2boolean(getValue(line)));
		else if(idEquals(line, "SHOWINNERNODES"))
			view.getTreeLayoutManager().setShowInnerNodes(str2boolean(getValue(line)));
		else if(idEquals(line, "SHOWLEGEND"))
			view.getTreeLayoutManager().setShowLegend(str2boolean(getValue(line)));
		else if(idEquals(line, "TREATLABELASBOOTSTRAP"))
			view.getTreeLayoutManager().setTreatLabelAsBootstrap(str2boolean(getValue(line)));
		else if(idEquals(line, "USEDISTANCES"))
			view.getTreeLayoutManager().setUseDistances(str2boolean(getValue(line)));
	}

	@Override
	protected void setLayoutSettings(TreeViewI view) {
		// TODO Auto-generated method stub
		
	}
}
