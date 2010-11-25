package angstd.algos.treecreation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import angstd.model.tree.Tree;
import angstd.model.tree.TreeEdge;
import angstd.model.tree.TreeEdgeI;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeFactory;
import angstd.model.tree.TreeNodeI;

public class SpeciesTreeCreationUtil {

//	CE28701:Eukaryota;Metazoa;Nematoda;Chromadorea;Rhabditida;Rhabditoidea;Rhabditidae;Peloderinae;Caenorhabditis.
//	CE07757:Eukaryota;Metazoa;Nematoda;Chromadorea;Rhabditida;Rhabditoidea;Rhabditidae;Peloderinae;Caenorhabditis.
//	CE38632:Eukaryota;Metazoa;Nematoda;Chromadorea;Rhabditida;Rhabditoidea;Rhabditidae;Peloderinae;Caenorhabditis.
//	CE19566:Eukaryota;Metazoa;Nematoda;Chromadorea;Rhabditida;Rhabditoidea;Rhabditidae;Peloderinae;Caenorhabditis.
//	CE09331:Eukaryota;Metazoa;Nematoda;Chromadorea;Rhabditida;Rhabditoidea;Rhabditidae;Peloderinae;Caenorhabditis.
//	CE27829:Eukaryota;Metazoa;Nematoda;Chromadorea;Rhabditida;Rhabditoidea;Rhabditidae;Peloderinae;Caenorhabditis.


	public static TreeI createSpeciesTree(Map<String, String> linages) { //, Map<String, DomainArrangement> proteins) {
		TreeNodeFactory nodeFactory = new TreeNodeFactory ();
		
		// create the tree and add the root
		TreeI tree = new Tree();
		TreeNodeI root = nodeFactory.createNode();
		tree.addNode(root);
		tree.setRoot(root);
		
		// resolve linages into tree structure
		Map<String, TreeNodeI> labels = new HashMap<String, TreeNodeI>();
		TreeNodeI parent = root;
		
		Iterator<String> iter = linages.keySet().iterator();
		while(iter.hasNext()) {
			String name = iter.next();
			String linageStr = linages.get(name);
			String[] token = linageStr.split(";");

			TreeNodeI child;
			
			for (int i = 0; i < token.length; i++) {
				String label = token[i];
				if (labels.get(label) != null) {	// node already added
					parent = labels.get(label);
					continue;
				}
				
				// node is new and must therefore be added
				child = nodeFactory.createNode();
				child.setLabel(label);
				TreeEdgeI edge = new TreeEdge(parent, child);
				
				tree.addNode(child);
				tree.addEdge(edge);
				labels.put(label, child);	
				parent = child;
			}
			
			// finally add the protein to its organism
			TreeNodeI protein = nodeFactory.createNode();
			protein.setLabel(name);
			tree.addNode(protein);

			tree.addEdge(new TreeEdge(parent, protein)); //, 0));
		}
		
		// tree is created from the linages, now collapse node with just one child
		traverse(tree, root);

		return tree;
	}

	private static void traverse(TreeI tree, TreeNodeI parent) {
		if (parent.isLeaf()) 
			return;
		
		// backup childs if they change
		TreeNodeI[] childs = new TreeNodeI[parent.childCount()];
		for (int c = 0; c < parent.childCount(); c++) 
			childs[c] = parent.getChildAt(c);
		
		if (parent.childCount() == 1) {
			if (parent.getChildAt(0).isLeaf()) 
				return;

			TreeNodeI child = parent.getChildAt(0);
			if (tree.getRoot().equals(parent)) {	// if its the root node
				child.removeEdge(child.getEdgeToParent());
				tree.removeNode(parent);
				tree.setRoot(child);	
			} else {								// if its an inner node
				TreeNodeI newParent = parent.getParent();
				newParent.removeEdge(newParent.getEdgeToChild(parent));
				TreeEdgeI edge = new TreeEdge(newParent, child);
				tree.addEdge(edge);
				tree.removeNode(parent);
			}
			traverse(tree, child);
		} else 
			for (TreeNodeI child : childs) 
				traverse(tree, child); 
	}
}
