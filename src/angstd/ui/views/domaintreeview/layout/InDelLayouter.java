package angstd.ui.views.domaintreeview.layout;

import java.util.Iterator;
import java.util.List;

import angstd.model.domainevent.DomainEventI;
import angstd.model.tree.TreeEdgeI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.components.DomainEventComponent;
import angstd.ui.views.domainview.layout.UnproportionalLayout;
import angstd.ui.views.treeview.components.NodeComponent;



/**
 * InDelLayouter is a layouter class used to layout domain event components.
 * 
 * @author Andreas Held
 *
 */
public class InDelLayouter {

	private DomainTreeViewI view;
	
	public InDelLayouter(DomainTreeViewI view) {
		this.view = view;
	}
	
	public void layoutInDels() {
		// run through the tree and layout the in/dels
		
		Iterator<TreeNodeI> iter = view.getDomTree().getNodeIterator();
		while(iter.hasNext()) {
			TreeNodeI dtn = iter.next();
			
			for (int i = 0; i < dtn.childCount(); i++) {
				TreeEdgeI edge = (TreeEdgeI) dtn.getEdgeToChild(i);
				if (edge.hasDomainEvent())
					layoutDomainEvent(edge, view.getTreeComponentManager().getComponent((TreeNodeI)edge.getSource()), view.getTreeComponentManager().getComponent((TreeNodeI)edge.getTarget()));
			}
		}	
	}
	
	public void layoutDomainEvent(TreeEdgeI edge, NodeComponent p, NodeComponent c) {	
		// number of insertion deletions
		List<DomainEventI> domEvents = edge.getDomainEvents();
	
		int x1 = p.getX()+2;
		int x2 = c.getX();
		int y2 = c.getY();
		
		int maxWidth = Math.abs(x1-x2)-4;
		int width = UnproportionalLayout.UNPROPSIZE/2;
		int dist = 2;
		int height = 12; // g.setFont(Font.decode("Arial-12")); //maxHeight-2;
		
		double offsetX = 1.0 / maxWidth;
		if (offsetX == Double.POSITIVE_INFINITY) // TODO
			return;
		
		
		double availableWidth = maxWidth / (double) (domEvents.size());
		if (availableWidth > width)
			availableWidth = width;
		
		int y = y2 - height-1;
		
		for (int i = 0; i < domEvents.size(); i++) {
			DomainEventComponent dec = view.getDomainEventComponentManager().getComponent(domEvents.get(i));
			dec.getRelativeBounds().width = offsetX * availableWidth;
			dec.getRelativeBounds().x = (offsetX * dist + dec.getRelativeBounds().width)*i;
			
			dec.setBounds((int) (x1 + dec.getRelativeBounds().x * maxWidth), y, (int) (dec.getRelativeBounds().width * maxWidth), height);
		}
	}
}
