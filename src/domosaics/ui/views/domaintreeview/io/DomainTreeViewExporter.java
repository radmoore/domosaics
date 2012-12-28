package domosaics.ui.views.domaintreeview.io;

import java.awt.BasicStroke;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jdom2.*;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.io.XdomWriter;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.manager.DomainLayoutManager;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.io.ViewExporter;


public class DomainTreeViewExporter extends ViewExporter<DomainTreeViewI>{

	public void write(BufferedWriter out, DomainTreeViewI view) {
        try {
        	// write each view into the file
			writeTag(out, 1, "DOMAINTREEVIEW", true);
			writeParam(out, 2, "VIEWNAME", view.getViewInfo().getName());
			
			writeTag(out, 2, "DATA", true);
			
			// store associated sequences if there are any
			if (view.isSequenceLoaded()) {
				writeTag(out, 3, "SEQUENCEDATA", true);
				SequenceI[] seqs = view.getSequences();
				new FastaWriter().write(out, seqs);
				writeTag(out, 3, "SEQUENCEDATA", false);
			}

			writeTag(out, 3, "TREEDATA", true);
			traverseTreeExport(out, view.getTree().getRoot(), view);
			writeTag(out, 3, "TREEDATA", false);
			
			writeTag(out, 3, "DOMAINDATA", true);
			new XdomWriter().write(out, view.getDaSet());
			writeTag(out, 3, "DOMAINDATA", false);
			
			writeTag(out, 2, "DATA", false);

			writeTag(out, 2, "ATTRIBUTES", true);
			// layout options
			writeTag(out, 3, "TREEDEFAULTSETTINGS", true);
        	writeParam(out, 4, "DEFAULTEDGECOLOR", 		color2str(view.getTreeColorManager().getDefaultEdgeColor()));
        	writeParam(out, 4, "DEFAULTNODECOLOR", 		color2str(view.getTreeColorManager().getDefaultNodeColor()));
        	writeParam(out, 4, "DEFAULTSELECTIONCOLOR", color2str(view.getTreeColorManager().getDefaultSelectionColor()));
        	writeParam(out, 4, "DEFAULTEDGELABELCOLOR", color2str(view.getTreeColorManager().getEdgeLabelColor()));
        	writeParam(out, 4, "DEFAULTEDGESTROKE", 	stroke2str((BasicStroke)view.getTreeStrokeManager().getDefaultEdgeStroke()));
        	writeParam(out, 4, "DEFAULTFONT", 			font2str(view.getTreeFontManager().getFont()));
        	writeParam(out, 4, "MAXIMUMFONTSIZE", ""+	view.getTreeFontManager().getMaximumFontSize());
        	writeParam(out, 4, "MINIMUMFONTSIZE", ""+	view.getTreeFontManager().getMinimumFontSize());
        	writeTag(out, 3, "TREEDEFAULTSETTINGS", false);
        	
    		writeTag(out, 3, "TREELAYOUTSETTINGS", true);
    		writeParam(out, 4, "SHOWEDGELABELS", 		""+view.getTreeLayoutManager().isDrawEdgeWeights());
    		writeParam(out, 4, "EXPANDLEAVES", 			""+view.getTreeLayoutManager().isExpandLeaves());
    		writeParam(out, 4, "SHOWBOOTSTRAP", 		""+view.getTreeLayoutManager().isShowBootstrap());
    		writeParam(out, 4, "SHOWINNERNODES", 		""+view.getTreeLayoutManager().isShowInnerNodes());
    		writeParam(out, 4, "SHOWLEGEND", 			""+view.getTreeLayoutManager().isShowLegend());
    		writeParam(out, 4, "TREATLABELASBOOTSTRAP", ""+view.getTreeLayoutManager().isTreatLabelAsBootstrap());
    		writeParam(out, 4, "USEDISTANCES", 			""+view.getTreeLayoutManager().isUseDistances());
    		writeTag(out, 3, "TREELAYOUTSETTINGS", false);
    		
    		writeTag(out, 3, "DOMAINLAYOUTSETTINGS", true);
    		writeParam(out, 4, "VIEWLAYOUT", 			layout2String(view.getDomainLayoutManager()));
    		writeParam(out, 4, "FITTOSCREEN", 			""+view.getDomainLayoutManager().isFitDomainsToScreen());
    		writeParam(out, 4, "SHOWSHAPES", 			""+view.getDomainLayoutManager().isShowShapes());
    		writeTag(out, 3, "DOMAINLAYOUTSETTINGS", false);
    		
    		writeTag(out, 3, "DOMAINSETTINGS", true);
    		ArrangementManager manager = new ArrangementManager();
    		manager.add(view.getDaSet());
    		Iterator<DomainFamily> famIter = manager.getFamilyIterator();
    		while (famIter.hasNext()) {
    			DomainFamily fam = famIter.next();
        		writeTag(out, 4, "DOMAINFAMILY", true);
        		writeParam(out, 5, "ID", fam.getId());
        		writeParam(out, 5, "ACC", fam.getName());
        		writeParam(out, 5, "COLOR", color2str(view.getDomainColorManager().getDomainColor(fam)));
        		writeParam(out, 5, "SHAPE", ""+view.getDomainShapeManager().getShapeID(fam));
        		writeTag(out, 4, "DOMAINFAMILY", false);
        	}
        	writeTag(out, 3, "DOMAINSETTINGS", false);
		
    		writeTag(out, 2, "ATTRIBUTES", false);
			writeTag(out, 1, "DOMAINTREEVIEW", false);

    		out.flush();
        } catch (IOException e) {
			Configuration.getLogger().debug(e.toString());	
        }
    }
	
	private void traverseTreeExport(BufferedWriter out, TreeNodeI node, DomainTreeViewI view) throws IOException {
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
		writeParam(out, 5, "CSACOLLAPSED", ""+view.getCSAInSubtreeManager().isCollapsedAndCSAMode(nc));
		writeParam(out, 5, "NODECOLOR", color2str(view.getTreeColorManager().getNodeColor(nc)));
		writeParam(out, 5, "EDGECOLOR", color2str(view.getTreeColorManager().getEdgeColor(node.getEdgeToParent())));
		writeParam(out, 5, "EDGESTROKE",stroke2str((BasicStroke)view.getTreeStrokeManager().getEdgeStroke(node.getEdgeToParent())));
		writeParam(out, 5, "FONT", 		font2str(view.getTreeFontManager().getFont(nc)));
		
		writeTag(out, 4, "NODE", false);
		
		// traverse over children		
		for (TreeNodeI child : node.getChildren()) 
			traverseTreeExport(out, child, view);
	}
	
	private String layout2String(DomainLayoutManager manager) {
		if (manager.isProportionalView())
			return "PROPORTIONAL";
		else if (manager.isUnproportionalView())
			return "UNPROPORTIONAL";
		else if (manager.isMsaView()) 
			return "MSA";
		else
			return "PROPORTIONAL";
		
	}
	
}
