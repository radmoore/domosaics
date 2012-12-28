package angstd.algos.indels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainSet;
import angstd.model.configuration.Configuration;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.views.domaintreeview.DomainTreeViewI;



/**
 * This class represents an implementation of the Sankoff algorithm for 
 * domains. The reference implementation from "J. Clemente, K. Valiente,
 * G. Valiente, T. Gojobori. Optimized ancestral state reconstruction 
 * using Sankoff parsimony. BMC Bioinformatics 10:51. (2009)" was slightly
 * modified to fit into the need of domain arrangement reconstruction 
 * using insertion and deletion operations.
 * 
 * @author Andreas Held
 *
 */
public class Sankoff4Sets extends AbstractReconstructionAlgo {
	
	/** mapping between a node and its cost vector */
	private Map<TreeNodeI, Double[]> node2scoreVec;
	
	/** costs for an insertion */
	private double inCost;
	
	/** costs for a deletion */
	private double delCost;
	
	/**
	 * An enumeration of all states within a cost vector of a tree node.
	 * In the case of domains those states are present and not present.
	 * To access an cost vector entry the getVal() method can be used.
	 * To check if the actual state is observed within a leaf node the
	 * observed() method can be used.
	 * 
	 * @author Andreas Held
	 *
	 */
	private enum State {
		NOTPRESENT(0),
		PRESENT (1);
		
		/** index which may be used for access a cost vector entry */
		private int val;
		
		private State(int val) {
			this.val = val;
		}
		
		/**
		 * Returns the value which can be used as index for a cost vector
		 * 
		 * @return
		 * 		index for a cost vector
		 */
		public int getVal() {
			return val;
		}
		
		/**
		 * Whether or not the specified domain is contained by the 
		 * specified arrangement if state as present. Analogous for
		 * state is not present.
		 * 
		 * @param da
		 * 		the arrangement to check if the state is observed in there
		 * @param dom
		 * 		the domain used to validate the state observation
		 * @return
		 * 		whether or not the state is observed in the specified arrangement
		 */
		public boolean observed(DomainArrangement da, Domain dom) {
			if (val == 1 && da.contains(dom))
				return true;
			if (val == 0 && !da.contains(dom))
				return true;
			return false;
		}
		
		public String toString() {
			if (val == 0)
				return "NOTPRESET";
			else
				return "PRESENT";
		}
	}
	
	public Sankoff4Sets(DomainTreeViewI view, boolean useSets) {
		super(view, useSets);
	}
	
	/**
	 * Sets the paremeters for insertion deletion costs.
	 * It would be wise to call this method before starting the 
	 * algorithm.
	 * 
	 * @param tree
	 * 		the tree to be evaluated
	 * @param inCost
	 * 		costs for an insertion
	 * @param delCost
	 * 		costs for a deletion
	 */
	public void setParams(TreeI tree, double inCost, double delCost) {
		super.tree = tree;
		this.inCost = inCost;
		this.delCost = delCost;
	}
	
	/**
	 * Method being triggered to start the sankoff algorithm for all 
	 * domains within the dataset separately. Instead of the 
	 * root node the last common ancestor for the actual domain is taken which may
	 * speed up the algorithm for big trees (change of the reference implementation).
	 */
	@Override
    protected String doInBackground() {
		try {	
			// do some initialization
			node2daSet = new HashMap<TreeNodeI, DomainSet>();
			node2scoreVec = new HashMap<TreeNodeI, Double[]>();
			DomainSet doms = new DomainSet();

			Iterator<TreeNodeI> iter = tree.getNodeIterator();
			while(iter.hasNext()) {
				TreeNodeI node = iter.next();
			
				// create dummy arrangements for inner nodes
				node2daSet.put(node, new DomainSet(node.getArrangement()));
			
				// create the alphabet (all domains within the dataset)
				if (node.hasArrangement())
					doms.add(node.getArrangement().getDomains());
			
				// init score vectors
				node2scoreVec.put(node, new Double[2]);
			}

			// create CostMatrix
			CostMatrix costs = new CostMatrix(inCost, delCost); // in / del

			// do sankoff for all domains
			for (int i = 0; i < doms.size() && !isCancelled(); i++) {
				setProgress((int) Math.round(100 / (double) doms.size() * (i+1)));
				
				// get Hotspot this is the maximum node where the domain can be inserted
				TreeNodeI lca = HotspotAlgo.findHotspot(doms.get(i), tree);
				sankoff_Up(lca, lca, costs, doms.get(i));	
				sankoff_Down(lca, costs, doms.get(i));
			}
			
		}
		catch(Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		
		return null;
    }

	/**
	 * Sankoff up phase for all nodes calculating their cost vectors. 
	 * If the actual node is a leaf the costs are zero if the state in question
	 * is observed, else infinity. <br>
	 * If the node is an inner node the costs are calculated based on
	 * the cost matrix for state changes and the child nodes. <br>
	 * The reference algorithm was changed in a way that the algorithm works 
	 * also for non binary trees. <br>
	 * Because domain events are being calculated the score vector
	 * of the lca node has to be modified. The costs of an insertion
	 * must be added after the sankoff up phase finished (change of the reference implementation).
	 * Otherwise the costs for the initial occurrence of the domain would
	 * be never taken into account.
	 * 
	 * @param actNode
	 * 		The actual node which cost vector is being calculated
	 * @param lca
	 * 		the last common ancestor node for the actually used domain
	 * @param costs
	 * 		the cost matrix
	 * @param dom
	 * 		the domain for which the algorithm is executed
	 */
	private void sankoff_Up(TreeNodeI actNode, TreeNodeI lca, CostMatrix costs, Domain dom) {
		Double[] scoreVec = new Double[2];
		
		// do it for all nodes in postorder
		for (TreeNodeI child : actNode.getChildren())
			sankoff_Up(child, lca, costs, dom);
		
		if (actNode.isLeaf()) {
			DomainArrangement da = actNode.getArrangement();
			if (da == null) {
				System.out.println("Error during Sankoff, leaf node has no arrangement assigned");
				cancel(true);
				return;
			}
			
			for (State state : State.values()) {
				if (state.observed(da, dom))
					scoreVec[state.getVal()] = 0.0;
				else
					scoreVec[state.getVal()] = Double.POSITIVE_INFINITY;
			}
			
			node2scoreVec.put(actNode, scoreVec);
			return; 
		}
		
		// inner node (MULTIPLE CHILDREN)
		for (State state : State.values()) {
			double cost = 0;
			for (TreeNodeI child : actNode.getChildren()) 
				cost += cost(child, state, costs);
			scoreVec[state.getVal()] = cost;
		}
		
		if (lca.equals(actNode)) 
			scoreVec[State.PRESENT.getVal()] += costs.get(0, 1);

		node2scoreVec.put(actNode, scoreVec);
	}
	
	/**
	 * Helper method calculating the costs for a score vector entry
	 * assuming that the actually observed node has the assigned state 
	 * (e.g. domain is present). <br>
	 * For instance if the domain is assumed to be present in the 
	 * ancestral node and the child node specified as parameter does not
	 * contain the domain the costs would be a deletion cost plus the 
	 * costs that the specified child node does not contain the domain.
	 * 
	 * @param child
	 * 		the child node used to calculate the cost for the parent node
	 * @param actState
	 * 		the assumed state of the parent node
	 * @param costs
	 * 		the cost matrix
	 * @return
	 * 		the costs for specified ancestral state in dependency to the specified child 
	 */
	private double cost(TreeNodeI child, State actState, CostMatrix costs) {
		double min = Double.POSITIVE_INFINITY;
		for (State state : State.values()) {
			double cost = costs.get(actState.getVal(), state.getVal()) + node2scoreVec.get(child)[state.getVal()];
			if (cost < min) 
				min = cost;
		}
		return min;
	}
	
	/**
	 * This method leads into the Sankoff down phase by calling it with 
	 * the root (or last common ancestor) mode. The minimum states 
	 * (states with minimal costs) are gathered and ties resolved by
	 * assigning the domain to the node. <br>
	 * Once the root (lca) node is done the alternative sankof_Down
	 * method is used recursively to calculate the states for all other nodes.
	 * 
	 * @param root
	 * 		the trees root or the last common ancestor for the actual domain
	 * @param costs
	 * 		the cost matrix
	 * @param dom
	 * 		the actual domain for which the algorithm is executed
	 */
	private void sankoff_Down(TreeNodeI root, CostMatrix costs, Domain dom) {
		// get root (last common ancestor) states with minimum costs
		List<State> ancestralStates = score2minsStates(node2scoreVec.get(root));
	
		// resolve ties
		State state = State.NOTPRESENT;
		if (ancestralStates.contains(State.PRESENT)) {
			node2daSet.get(root).add(dom);
			state = State.PRESENT;
		}

		for (TreeNodeI child : root.getChildren())
			sankoff_Down(state, child, costs, dom);			
	}
	
	/**
	 * Sankoff down phase for all nodes except the root (last common
	 * ancestor) node. Decides the state for inner nodes. Ties are 
	 * resolved by assigning the domain to the node. <br>
	 * The state parameter is the assigned state for the parent node.
	 * This information is used in method minStates() to retrieve
	 * the states of the actual node which led to the parent nodes
	 * state (This is not necessarily the state with minimum cost).
	 * 
	 * @param state
	 * 		the state which was assigned to the parent node
	 * @param actNode
	 * 		the actual node which state is going to be decided
	 * @param costs
	 * 		the cost matrix
	 * @param dom
	 * 		the actual domain for which the algorithm is executed
	 */
	private void sankoff_Down(State state, TreeNodeI actNode, CostMatrix costs, Domain dom) {
		List<State> ancestralStates = minStates(state, actNode, costs);
		
		// Resolve ties
		State actState = State.NOTPRESENT;
		if (ancestralStates.contains(State.PRESENT)) {
			node2daSet.get(actNode).add(dom);
			actState = State.PRESENT;
		}
	
		for (TreeNodeI child : actNode.getChildren())
			sankoff_Down(actState, child, costs, dom);
	}
	
	/**
	 * This method is just suited for the root or last common ancestor node.
	 * It returns the states with minimum cost independently of the states
	 * from other nodes.
	 * 
	 * @param scoreVec
	 * 		the score vector in which the minimum states are searched
	 * @return
	 * 		the states with minimum cost
	 */
	private static List<State> score2minsStates (Double[] scoreVec) {
		List<State> res = new ArrayList<State>();
		
		// find minimum
		double min = scoreVec[0];
		for (int i = 1; i < scoreVec.length; i++)
			if (scoreVec[i] < min) 
				min = scoreVec[i];
		
		// create list with all states equal to the minimum
		for (State state : State.values())
			if (scoreVec[state.getVal()] == min)
				res.add(state);
	
		return res;
	}
	
	/**
	 * Calculates the minimum states which led to the chosen state of 
	 * the parent node.
	 * 
	 * @param state
	 * 		the parent nodes state
	 * @param actNode
	 * 		the actual observed node
	 * @param costs
	 * 		the cost matrix
	 * @return
	 * 		minimum states which led to the chosen state of the parent node.
	 */
	private List<State> minStates(State state, TreeNodeI actNode, CostMatrix costs) {
		double min = Integer.MAX_VALUE;
		List<State> ancestralStates = new ArrayList<State>();
		
		for (State actState : State.values()) {
			double trans_cost = costs.get(state.getVal(), actState.getVal()) + node2scoreVec.get(actNode)[actState.getVal()];
			if (trans_cost < min) {
				min = trans_cost;
				ancestralStates.add(actState);
			} else if(trans_cost == min)
				ancestralStates.add(actState);
		}
		return ancestralStates;
	}
	
	/**
	 * The cost matrix for insertion and deletions.
	 * 
	 * @author Andreas Held
	 *
	 */
	private class CostMatrix {
		
		/** not present index */
		private static final int NOTPRESENT = 0;
		
		/** present index */
		private static final int PRESENT = 1;
		
		/** array containing the cost for insertion deletion */
		protected double[][] costs;
		
		/**
		 * Constructor for a new CostMatrix
		 * 
		 * @param inCost
		 * 		costs for an insertion
		 * @param delCost
		 * 		costs for a deletion
		 */
		public CostMatrix(double inCost, double delCost) {
			costs = new double[2][2];
			costs[NOTPRESENT][NOTPRESENT] = 0; 			
			costs[PRESENT][PRESENT] = 0; 			
			costs[NOTPRESENT][PRESENT] = inCost; 		
			costs[PRESENT][NOTPRESENT] = delCost; 		
		}
		
		/**
		 * Returns the cost for the specified parent and child state. 
		 * E.g. if present in parent then parentState = 1, else parentState = 0,
		 * if present in child then childState = 1, else childState = 0
		 * 
		 * @param parentState
		 * 		state index for the state of the parent node
		 * @param childState
		 * 		state index for the state of the child node
		 * @return
		 * 		cost for the specified parent and child state.
		 */
		public double get(int parentState, int childState) {
			return costs[parentState][childState];
		}
	}
	
}