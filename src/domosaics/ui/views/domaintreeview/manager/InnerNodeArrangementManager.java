package domosaics.ui.views.domaintreeview.manager;

import java.util.HashMap;
import java.util.Map;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.view.manager.DefaultViewManager;




public class InnerNodeArrangementManager extends DefaultViewManager {

	protected Map<TreeNodeI, DomainArrangement> node2da;

	protected DomainTreeViewI view;
	
	
	/**
	 * Constructor for a new InnerNodeArrangementManager
	 * 
	 * @param view
	 * 		the view providing the functionality
	 */
	public InnerNodeArrangementManager(DomainTreeViewI view) {
		this.view = view;
		node2da = new HashMap<TreeNodeI, DomainArrangement>();
	}
	
	public void reset() {
		node2da.clear();
	}
	
	public boolean hasArrangement(TreeNodeI nc) {
		if (node2da.get(nc) == null)
			return false;
		return true;
	}
	
	public DomainArrangement getDA(TreeNodeI nc) {
		return node2da.get(nc);
	}
	
	public void storeDA(TreeNodeI nc, DomainArrangement da) {
		node2da.put(nc, da);
	}

}
