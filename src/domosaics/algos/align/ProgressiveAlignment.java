package domosaics.algos.align;

import java.util.ArrayList;
import java.util.List;

import domosaics.algos.align.nw.NW4DomainsAffine;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainVector;
import domosaics.model.arrangement.GapDomain;
import domosaics.model.tree.TreeI;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;




public class ProgressiveAlignment {
	
	public static DomainArrangement[] align(DomainTreeViewI view) {
		traverseAlign (view, view.getDomTree().getRoot());
		return view.getDomTree().getDaSet();
	}
	
	public static DomainVector getDomainColumns(TreeI domTree) {
		return traverseGetDomainCols (domTree.getRoot());
	}
	
	private static DomainVector traverseGetDomainCols(TreeNodeI node) {
		
		// evaluate the children first
		for (int i = 0; i < node.childCount(); i++)
			traverseGetDomainCols (node.getChildAt(i));
		
		// don't align leafs with them self
		if (node.isLeaf())
			return null;
		
		// TODO just for checking purpose (binary tree)
		if (node.childCount() != 2) {
			System.out.println("ERROR, not a valid guide tree");
			return null;
		}
		
		// get the arrangements to align	
		TreeNodeI node1 = node.getChildAt(0);
		TreeNodeI node2 = node.getChildAt(1);
		DomainArrangement da1 = node1.getArrangement();
		DomainArrangement da2 = node2.getArrangement();
		
		// align the arrangements using NeedlemanWunsch
		DomainVector[] aligned = new NW4DomainsAffine(da1, da2).getMatch();
		
		// create resulting inner node arrangement
		DomainArrangement da3 = new DomainArrangement();
		DomainVector consensus = new DomainVector();
		
		// create "consensus" for the two alignments
		for (int i = 0; i < aligned[0].size(); i++) {
			if (!(aligned[0].get(i) instanceof GapDomain))
				consensus.add(aligned[0].get(i));
			else if (!(aligned[1].get(i) instanceof GapDomain))
				consensus.add(aligned[1].get(i));
			else // shouldn't be possible
				consensus.add(new GapDomain(da3));
		}

		for (int i = 0; i < consensus.size(); i++)
			da3.addDomain(consensus.get(i));
		
		node.setArrangement(da3);
		
		return consensus;
	}
	
	
	
//	/**
//	 * Aligns progressively domains and inserts GAP domains while doing so.
//	 * 
//	 * @param daSet
//	 * 		set of arrangements to be aligned
//	 * @return
//	 * 		aligned set of domain arrangements
//	 */
//	public static DomainArrangement[] align(TreeI tree, DomainArrangement[] daSet) {
//		// 1. create distance matrix
////		double[][] distMatrix = new DomainDistance().calc(daSet, false);
//		
//		// TODO 2. what if we are within the domain view and have no tree associated with the domains? // create guide tree using UPGMA cluster algorithm.
////		Tree tree = new UPGMA(daSet, distMatrix).computeTree();
////		tree.loadDasIntoTree(daSet);
//	
//		// 3. align progressively using the guide tree
//		traverseAlign (tree.getRoot());
//		return tree.getDaSet();
//	}
	
	
	private static void traverseAlign(DomainTreeViewI view, TreeNodeI node) {
		// evaluate the children first
		for (int i = 0; i < node.childCount(); i++)
			traverseAlign (view, node.getChildAt(i));
		
		// don't align leafs with them self
		if (node.isLeaf())
			return;
		
		// TODO just for checking purpose (binary tree)
		if (node.childCount() != 2) {
			System.out.println("ERROR, not a valid guide tree");
			return;
		}
		
		// get the arrangements to align	
		TreeNodeI node1 = node.getChildAt(0);
		TreeNodeI node2 = node.getChildAt(1);
		DomainArrangement da1 = node1.getArrangement();
		DomainArrangement da2 = node2.getArrangement();
		
		// align the arrangements using NeedlemanWunsch
		DomainVector[] aligned = new NW4DomainsAffine(da1, da2).getMatch();
		
		// create resulting inner node arrangement
		DomainArrangement da3 = new DomainArrangement();
		DomainVector consensus = new DomainVector();
		
		// create "consensus" for the two alignments
		for (int i = 0; i < aligned[0].size(); i++) {
			if (!(aligned[0].get(i) instanceof GapDomain))
				consensus.add(aligned[0].get(i));
			else if (!(aligned[1].get(i) instanceof GapDomain))
				consensus.add(aligned[1].get(i));
			else // shouldn't be possible
				consensus.add(new GapDomain(da3));
		}
		
		// TODO modify gap positions for unprop view depending on the gap domain positions
	
//		for (Domain dom : da1.getDomains())
//			view.getDomainComponentManager().removeComponent(dom);
//		da1.clear();
//		for (int i = 0; i < aligned[0].size(); i++)
//			da1.addDomain(aligned[0].get(i));
//		
//		for (Domain dom : da2.getDomains())
//			view.getDomainComponentManager().removeComponent(dom);
//		da2.clear();
//		for (int i = 0; i < aligned[1].size(); i++)
//			da2.addDomain(aligned[1].get(i));
//		
//		for (Domain dom : da3.getDomains())
//			view.getDomainComponentManager().removeComponent(dom);
//		da3.clear();
//		for (int i = 0; i < consensus.size(); i++)
//			da3.addDomain(consensus.get(i));
//		
//		node.setArrangement(da3);
		
		// TODO test output
//		if (da1.getName() == null)
//			System.out.print("inner node "+da1.getTreeNode().getID()+": ");
//		else
//			System.out.print(da1.getName()+": ");
//		for (int i = 0; i < aligned[0].size(); i++) {
//			System.out.print(aligned[0].get(i).getFamID()+" - ");
//		}
//		System.out.println();
//		
//		if (da2.getName() == null)
//			System.out.print("inner node "+da2.getTreeNode().getID()+": ");
//		else
//			System.out.print(da2.getName()+": ");
//		for (int i = 0; i < aligned[1].size(); i++) {
//			System.out.print(aligned[1].get(i).getFamID()+" - ");
//		}
//		System.out.println();
//		
//		System.out.print("resulting inner node "+node.getID()+": ");
//		for (int i = 0; i < consensus.size(); i++) {
//			System.out.print(consensus.get(i).getFamID()+" - ");
//		}
//		System.out.println();
//		System.out.println("----------------------------------");
		
		// insert gaps into existing alignments if necessary
		if (!node1.isLeaf()) {
			
			// the unaligned consensus is gapfree so each gap position is an index
			List<Integer> gapPositions = new ArrayList<Integer>();
			for (int i = 0; i < aligned[0].size(); i++) 
				if (aligned[0].get(i) instanceof GapDomain)
					gapPositions.add(i);

			// now add the gaps to the children
			for (int i = 0; i < gapPositions.size(); i++) 
				traverseAddGaps(gapPositions.get(i), node1);
		}
		
		if (!node2.isLeaf()) {
			List<Integer> gapPositions = new ArrayList<Integer>();
			for (int i = 0; i < aligned[1].size(); i++) 
				if (aligned[1].get(i) instanceof GapDomain)
					gapPositions.add(i);
			
			// now add the gaps to the children
			for (int i = 0; i < gapPositions.size(); i++) 
				traverseAddGaps(gapPositions.get(i), node2);
		}
	}
	
	/**
	 * add fresh inserted gaps to all children of the actual node
	 * @param position
	 * @param node
	 */
	private static void traverseAddGaps(int position, TreeNodeI node) {
		node.getArrangement().getDomains().add(position, new GapDomain(node.getArrangement()));
		
		for (int i = 0; i < node.childCount(); i++)
			traverseAddGaps (position, node.getChildAt(i));
	}

}
