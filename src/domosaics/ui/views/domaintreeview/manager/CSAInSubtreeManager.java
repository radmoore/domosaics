package domosaics.ui.views.domaintreeview.manager;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.manager.DefaultViewManager;




/**
 * CSAInSubtreeManager manages the collapsing of a subtree by collapsing
 * at the node and also redundant arrangements. The non redundant 
 * arrangements remain visible.  The collapsing is done
 * by setting redundant arrangement to invisible, which means that
 * they are not processed by the layout or renderer anymore.
 * To collapse and decollapse node the addNode() and removeNode() methods
 * must be triggered.
 * 
 * @author Andreas Held
 *
 */
public class CSAInSubtreeManager extends DefaultViewManager {

	/** the nodes currently collapsed in CSA mode */
	protected List<NodeComponent> csaNodes = new ArrayList<NodeComponent>();
	
	/** for each in CSA mode collapsed node the necessary parameters */
	protected Map<NodeComponent, CSAParamter> node2param;

	/** view providing the collapse functionality */
	protected DomainTreeViewI view;
	
	
	/**
	 * Constructor for a new CSAInSubtreeManager
	 * 
	 * @param view
	 * 		the view providing the collapse functionality
	 */
	public CSAInSubtreeManager(DomainTreeViewI view) {
		this.view = view;
		csaNodes = new ArrayList<NodeComponent>();
		node2param = new HashMap<NodeComponent, CSAParamter>();
	}
	
	/**
	 * Adds a node to the manager. This method should be invoked whenever
	 * a node gets collapsed in csa mode. All necessary calculations
	 * are also be delegated from here.
	 * 
	 * @param node
	 * 		the node which goews into csa mode
	 */
	public void addNode(NodeComponent node) {
		csaNodes.add(node);
		node2param.put(node, new CSAParamter());
		
		// delegate important calculating of necessary parameters to manage this node
		collapseSubtree(node);
		
		structuralChange();
	}
	
	/**
	 * Returns whether or not the specified node is in CSA mode.
	 * 
	 * @param nc
	 * 		the node to check for CSA collapse
	 * @return
	 * 		whether or not the specified node is in CSA mode.
	 */
	public boolean isCollapsedAndCSAMode(NodeComponent nc) {
		if (node2param.keySet().contains(nc))
			return true;
		return false;
	}
	
	public Collection<NodeComponent> getCollapsedAndCSAModeNodes() {
		return node2param.keySet();
	}
	
	/**
	 * Removes a node from the manager. This method should be invoked
	 * the a node is decollapsed from csa mode.
	 * 
	 * @param node
	 * 		the node which should not be in csa mode anymore
	 */
	public void removeNode(NodeComponent node) {
		csaNodes.remove(node);
		node2param.remove(node);
		structuralChange();
	}
	
	/**
	 * Returns the list of all nodes which are currently in csa mode
	 * 
	 * @return
	 * 		list of all nodes which are currently in csa mode
	 */
	public List<NodeComponent> getCSAnodes() {
		return csaNodes;
	}
	
	/**
	 * Returns whether or not the csaManager contains nodes which are
	 * still collapsed in csa mode.
	 * 
	 * @return
	 * 		whether or not the csaManager contains nodes which are
	 * 		collapsed in csa mode.
	 */
	public boolean isActive() {
		return !csaNodes.isEmpty();
	}
	
	/**
	 * Helper method to access and initialize the needed parameters
	 * to manage a CSAmode collapsed node. 
	 * 
	 * @param node
	 * 		the node component which parameters are requested
	 * @return
	 * 		the necessary parameters to manage the csa mode for the specified node component
	 */
	protected CSAParamter getParams(NodeComponent node) {
		return node2param.get(node);
	}
	
	/**
	 * Returns the manipulated subtree bounds for the specified node
	 * component.
	 * 
	 * @param node
	 * 		the node component in csa mode which subtree bounds are requested
	 * @return
	 * 		the subtree bounds for the requested node component.
	 */
	public Rectangle getSubtreeBounds(NodeComponent node) {
		return getParams(node).subtreeBounds;
	}
	
	/**
	 * Returns a sorted list of domain arrangement components for the
	 * occurring within the subtree of the collapsed node. Those 
	 * arrangements  don't include redundant arrangements, just the 
	 * arrangements to be relayouted.
	 * 
	 * @param node
	 * 		the node component in csa mode
	 * @return
	 * 		non redundant list of domain arrangement components occurring within the subtree of the specified node.
	 * 		
	 */
	public List<ArrangementComponent> getArrangements(NodeComponent node) {
		return  getParams(node).nonRedundantComponents;
	}

	/**
	 * Returns the redundancy count for arrangement components.
	 * 
	 * @param node
	 * 		the node component in csa mode
	 * @param dac
	 * 		the arrangement component which redundancy count is requested
	 * @return
	 * 		the redundancy count for the specified arrangement component
	 */
	public int getRedundancyCount(NodeComponent node, ArrangementComponent dac) {
		if (getParams(node).dac2redundant.get(dac) == null)
			return -1;
		return getParams(node).dac2redundant.get(dac);
	}
	
	/**
	 * Increases the redundancy counter for DomainArrangements.
	 * 
	 * @param node
	 * 		the node component in csa mode
	 * @param da
	 * 		the arrangement which redundancy count should be increased.
	 */
	public void incRedundancyCount(NodeComponent node, DomainArrangement da) {
		ArrangementComponent dac = view.getArrangementComponentManager().getComponent(da);
		
		if (getParams(node).dac2redundant.get(dac) == null) {
			getParams(node).dac2redundant.put(dac, 1);
			getParams(node).nonRedundant.add(dac.getDomainArrangement());
		}
		else {
			int newVal = getParams(node).dac2redundant.get(dac)+1;
			getParams(node).dac2redundant.put(dac, newVal);
		}
	}
	
	/**
	 * Checks whether or not the specified node is in csa mode
	 * 
	 * @param node
	 * 		the node to check for csa mode collapsing
	 * @return
	 * 		whether or not the specified node is in csa mode
	 */
	public boolean isInCSAMode(NodeComponent node) {
		return csaNodes.contains(node);
	}
	
	/**
	 * Helper method calculating the parameters to manage the collapsing of
	 * a node in csa mode.
	 * 
	 * @param node
	 * 		the node to collapse
	 */
	protected void collapseSubtree (NodeComponent node) {
		// change subtree bounds so the layout manager and renderer can work with it
		getParams(node).subtreeBounds = node.getSubtreeBounds().getBounds();
		getParams(node).subtreeBounds.height -= node.getBounds().getHeight();
		
		// gather the dataset for the subtree
		List<DomainArrangement> daSetList = new ArrayList<DomainArrangement>();
		daSetList = view.getTree().gatherDAs4Subtree(daSetList, node.getNode());
		DomainArrangement[] daSet = daSetList.toArray(new DomainArrangement[daSetList.size()]);
		
		// now do the magic
		incRedundancyCount(node, daSet[0]);
		view.getArrangementComponentManager().setVisible(view.getArrangementComponentManager().getComponent(daSet[0]), true);
		getParams(node).nonRedundantComponents.add(view.getArrangementComponentManager().getComponent(daSet[0]));
		
		for (int i = 1; i < daSet.length; i++) {
			boolean redundant = false;
			
			ArrangementComponent dac = view.getArrangementComponentManager().getComponent(daSet[i]);
			
			for (int j = 0; j < getParams(node).nonRedundant.size(); j++) 
				if (getParams(node).nonRedundant.get(j).getDomains().isEqualTo(daSet[i].getDomains())) {
					redundant = true;
					incRedundancyCount(node, getParams(node).nonRedundant.get(j));
					break;
				}
			if (!redundant) {
				incRedundancyCount(node, daSet[i]);
				getParams(node).nonRedundantComponents.add(dac);
				view.getArrangementComponentManager().setVisible(dac, true);
			}
		}
	}

	/**
	 * Helper class to store the important parameters for a collapsing
	 * of a node in csa mode.
	 * 
	 * @author Andreas Held
	 *
	 */
	class CSAParamter {
		/** map assigning an arrangement the number of same domain compositions within the dataset */
		Map<ArrangementComponent, Integer> dac2redundant;

		/** list where only non redundant arrangements may enter */
		List<DomainArrangement> nonRedundant;
		
		/** important because its sorted */
		List<ArrangementComponent> nonRedundantComponents;
		
		/** the subtree bounds of the collapsed CSA node */
		Rectangle subtreeBounds;
		
		public CSAParamter() {
			dac2redundant = new HashMap<ArrangementComponent, Integer>();
			nonRedundant = new ArrayList<DomainArrangement>();
			nonRedundantComponents = new ArrayList<ArrangementComponent>();
			subtreeBounds = new Rectangle();
		}
	
	}

}
