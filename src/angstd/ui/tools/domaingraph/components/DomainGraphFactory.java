package angstd.ui.tools.domaingraph.components;

import java.awt.Image;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.DomainViewI;

/**
 * DomainGraphFactory initializes the backing node and edge tables for the
 * prefuse graph. This class should be used to create 
 * instances of {@link DomainGraph}.
 * <p>
 * When adding nodes to the graph and increasing their connectivity 
 * counter only visible arrangements of the backend domain view are 
 * taken into account.
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainGraphFactory {

	/**
	 * Creates a new DomainGraph using a backend domainview.
	 * 
	 * @param view
	 * 		the backend domain view used to create the graph
	 * @return
	 * 		the co occurrence graph
	 */
	public DomainGraph create(DomainViewI view) {
		
		// init graph schema
        Schema nodeSchema = new Schema();
        nodeSchema.addColumn("name", String.class);
        nodeSchema.addColumn("status", int.class);
        nodeSchema.addColumn("image", Image.class);
        nodeSchema.addColumn("connectivity", int.class);
        
        Schema edgeSchema = new Schema();
        edgeSchema.addColumn(Graph.DEFAULT_SOURCE_KEY, Integer.TYPE);
        edgeSchema.addColumn(Graph.DEFAULT_TARGET_KEY, Integer.TYPE);
        edgeSchema.addColumn("weight", Integer.TYPE);
        
        Table nodes = nodeSchema.instantiate();
        Table edges = edgeSchema.instantiate();

        // instanciate the graph using the schemas
        DomainGraph graph = new DomainGraph(view, nodes, edges);
        
        // build the actual graph structure
        Map<String, Node> addedNodes = new HashMap<String, Node>();
        DomainArrangement[] daSet = view.getDaSet();
		
		for (int i = 0; i < daSet.length; i++) {
			
			// take only visible arrangements
			if (!view.getArrangementComponentManager().getComponent(daSet[i]).isVisible())
				continue;
			
			// connect the actual domain with the next domain within the protein
			Iterator<Domain> iter = daSet[i].getDomainIter();
			Domain actDom = null;
			if (iter.hasNext()) {
				actDom = iter.next();
				if(addedNodes.get(actDom.getFamID())==null) {
					Node node = graph.addNode(actDom);
					addedNodes.put(actDom.getFamID(), node);
				}
			}
			
			while(iter.hasNext()) {
				Domain nextDom = iter.next();
				Node target = null;
				if(addedNodes.get(nextDom.getFamID())==null) {
					target = graph.addNode(nextDom);
					addedNodes.put(nextDom.getFamID(), target);
				} else
					target = addedNodes.get(nextDom.getFamID());
				
				// check if there already exists an edge
				Node source = addedNodes.get(actDom.getFamID());
				
				// no connection to a node itself
				if (source.equals(target)) {
					actDom = nextDom;
					continue;
				}
				
				Edge edge = graph.getEdge(source, target);
				if (edge == null) {
					edge = graph.addEdge(source, target);
					edge.setInt("weight", 1);
					
					graph.incConnectivity(source);
					graph.incConnectivity(target);
				}
				else {
					int weight = edge.getInt("weight");
					edge.setInt("weight", weight+1);
				}
				
				actDom = nextDom;
			}
		}
     
        return graph;
	}
}
