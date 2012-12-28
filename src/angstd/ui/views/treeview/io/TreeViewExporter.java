package angstd.ui.views.treeview.io;

import java.awt.BasicStroke;
import java.io.BufferedWriter;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Element;

import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.view.io.ViewExporter;



public class TreeViewExporter extends ViewExporter<TreeViewI>{

	public void write(BufferedWriter out, TreeViewI view) {
        try {
        	writeTag(out, 1, "TREEVIEW", true);
			writeParam(out, 2, "VIEWNAME", view.getViewInfo().getName());
			
			writeTag(out, 2, "DATA", true);
			writeTag(out, 3, "TREE", true);
			
			traverseTreeExport(out, view.getTree().getRoot(), view);
			
			writeTag(out, 3, "TREE", false);
			writeTag(out, 2, "DATA", false);
			
			writeTag(out, 2, "ATTRIBUTES", true);
			
			// write default settings
        	writeTag(out, 3, "DEFAULTSETTINGS", true);
        	writeParam(out, 4, "DEFAULTEDGECOLOR", 		color2str(view.getTreeColorManager().getDefaultEdgeColor()));
        	writeParam(out, 4, "DEFAULTNODECOLOR", 		color2str(view.getTreeColorManager().getDefaultNodeColor()));
        	writeParam(out, 4, "DEFAULTSELECTIONCOLOR", color2str(view.getTreeColorManager().getDefaultSelectionColor()));
        	writeParam(out, 4, "DEFAULTEDGELABELCOLOR", color2str(view.getTreeColorManager().getEdgeLabelColor()));
        	writeParam(out, 4, "DEFAULTEDGESTROKE", 	stroke2str((BasicStroke)view.getTreeStrokeManager().getDefaultEdgeStroke()));
        	writeParam(out, 4, "DEFAULTFONT", 			font2str(view.getTreeFontManager().getFont()));
        	writeParam(out, 4, "MAXIMUMFONTSIZE", ""+	view.getTreeFontManager().getMaximumFontSize());
        	writeParam(out, 4, "MINIMUMFONTSIZE", ""+	view.getTreeFontManager().getMinimumFontSize());
        	writeTag(out, 3, "DEFAULTSETTINGS", false);
    		
    		// layout options
    		writeTag(out, 3, "LAYOUTSETTINGS", true);
    		writeParam(out, 4, "SHOWEDGELABELS", 		""+view.getTreeLayoutManager().isDrawEdgeWeights());
    		writeParam(out, 4, "EXPANDLEAVES", 			""+view.getTreeLayoutManager().isExpandLeaves());
    		writeParam(out, 4, "SHOWBOOTSTRAP", 		""+view.getTreeLayoutManager().isShowBootstrap());
    		writeParam(out, 4, "SHOWINNERNODES", 		""+view.getTreeLayoutManager().isShowInnerNodes());
    		writeParam(out, 4, "SHOWLEGEND", 			""+view.getTreeLayoutManager().isShowLegend());
    		writeParam(out, 4, "TREATLABELASBOOTSTRAP", ""+view.getTreeLayoutManager().isTreatLabelAsBootstrap());
    		writeParam(out, 4, "USEDISTANCES", 			""+view.getTreeLayoutManager().isUseDistances());
    		writeTag(out, 3, "LAYOUTSETTINGS", false);
    		
			writeTag(out, 2, "ATTRIBUTES", false);
			writeTag(out, 1, "TREEVIEW", false);

        	out.flush();
        } 
        catch (IOException e) {
			Configuration.getLogger().debug(e.toString());
        }
    }

	
	
	private void traverseTreeExport(BufferedWriter out, TreeNodeI node, TreeViewI view) throws IOException {
		// export the current node
		NodeComponent nc = view.getNodesComponent(node);
		
		writeTag(out, 4, "NODE", true);
		writeParam(out, 5, "ID", 		""+node.getID());
		
		writeTag(out, 5, "CHILDREN", true);
		for (TreeNodeI child : node.getChildren()) 
			writeParam2(out, 6, "CHILDID", ""+child.getID(), ""+child.getEdgeToParent().getWeight());
		writeTag(out, 5, "CHILDREN", false);
		
		writeParam(out, 5, "NAME", 		""+node.getLabel());
		writeParam(out, 5, "COLLAPSED", ""+nc.isCollapsed());
		writeParam(out, 5, "NODECOLOR", color2str(view.getTreeColorManager().getNodeColor(nc)));
		writeParam(out, 5, "EDGECOLOR", color2str(view.getTreeColorManager().getEdgeColor(node.getEdgeToParent())));
		writeParam(out, 5, "EDGESTROKE",stroke2str((BasicStroke)view.getTreeStrokeManager().getEdgeStroke(node.getEdgeToParent())));
		writeParam(out, 5, "FONT", 		font2str(view.getTreeFontManager().getFont(nc)));
		
		writeTag(out, 4, "NODE", false);
		
		// traverse over children		
		for (TreeNodeI child : node.getChildren()) 
			traverseTreeExport(out, child, view);
	}
}
